package de.Fr4gg0r.SGS.Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class propEditor extends Activity{ //no animation, the creation of all editTexts is too expensive
	private final ArrayList<EditText> edits = new ArrayList<EditText>();
	private final LayoutParams rowParams = new LayoutParams(-1, -2); //fill_parent, wrap_content
	private final LayoutParams rowParams2 = new LayoutParams(-2, -2); //wrap_content *2
	private final LinearLayout.LayoutParams divParams = new LinearLayout.LayoutParams(rowParams);
	private String path;
	private boolean blocked = false;
	private ScrollView sv;
	
	public void onCreate(Bundle b){
		super.onCreate(b);
		//setup ui
		divParams.setMargins(20, 20, 20, 20);
		sv = new ScrollView(this);
		final LinearLayout main = new LinearLayout(this);
		main.setOrientation(1); //vertical
		sv.addView(main);
		setContentView(sv);		
		//now data stuff
		path = getIntent().getStringExtra("path");
		setTitle(path);
		final File file = new File(path);
		if(!file.canRead())Utils.getReadAccess(path);
		//yay data now
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			int pos;
			while(reader.ready()){
				line = reader.readLine();
				if(line.length() == 0 || line.charAt(0) == '#')continue;
				pos = line.indexOf('=');
				if(pos < 0)continue;
				main.addView(addRow(line.substring(0, pos), line.substring(pos+1, line.length())));
				main.addView(addDivider());
			}
		} catch (FileNotFoundException e) {
			// WE CAN'T VIEW THIS FILE, EITHER NO ROOT OR FILE DOES NOT EXIST
			Toast.makeText(this, "The file does not exist or cannot be read by this app (no su rights)", 1).show();
			finish();
		} catch (IOException e) { } //pointless
	}
	private void save(){
		final File file = new File(path);
		StringBuilder builder = null;
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(file));
			builder = new StringBuilder();
			String line;
			int pos = 0;
			while(reader.ready()){
				line = reader.readLine();
				if(line.length() == 0){
					builder.append("\n");
					continue;
				}
				if(line.charAt(0) == '#'){
					builder.append(line).append("\n");
					continue;
				}
				int pos2 = line.indexOf('=');
				if(pos2 < 0){
					builder.append(line).append("\n");
					continue;
				}
				builder.append(line.substring(0, pos2));
				builder.append("=");
				builder.append(edits.get(pos).getText().toString());
				builder.append("\n");
				pos++;
			}
		} catch (FileNotFoundException e) {	} catch (IOException e) { } //pointless
		try {
			BufferedWriter out = null;
			out = new BufferedWriter(new FileWriter("/sdcard/"+file.getName()));
			out.write(builder.toString());
			out.close();
		} catch (Exception e) {
			Log.d("exception", e.toString());
			finish();
		}
		try {
			final Process p = Runtime.getRuntime().exec("su");
			final DataOutputStream out = new DataOutputStream(p.getOutputStream());
			out.writeBytes("busybox mount -o remount,rw /system\n");
			out.writeBytes("busybox cp -f /sdcard/"+file.getName()+" /system/\n");
			out.writeBytes("busybox rm -f /sdcard/"+file.getName());
			out.flush();
			out.close();
			p.waitFor();
		} catch (IOException e) { } catch (InterruptedException e) { }
	}
	private void changeState(){
		blocked = !blocked;
		for(EditText e: edits){
			e.setFocusable(blocked);
			e.setEnabled(blocked);
			e.setFocusableInTouchMode(blocked);
		}
	}
	private LinearLayout addRow(String title, String content){
		final LinearLayout l = new LinearLayout(this);
		final EditText e = new EditText(this);
		final TextView t = new TextView(this);
		l.setHorizontalGravity(1); //center horizontal
		l.setOrientation(1); //vertical
		l.setLayoutParams(rowParams);
		e.setLayoutParams(rowParams2);
		t.setLayoutParams(rowParams);
		t.setText(title);
		e.setText(content);
		t.setGravity(1); //center_horizontal
		e.setGravity(1);
		e.setFocusable(false); //by default not enabled
		e.setEnabled(false); //by default not enabled
		edits.add(e);
		l.addView(t);
		l.addView(e);
		return l;
	}
	private View addDivider(){
		final View v = new View(this);
		v.setBackgroundResource(android.R.drawable.divider_horizontal_bright);
		v.setLayoutParams(divParams);
		return v;
	}
    public boolean onKeyDown(int keycode, KeyEvent event){
    	if(keycode == KeyEvent.KEYCODE_MENU){
    		//CRAPPY STANDARD MENU HERE :(
    		return false;
    	}
    	if(keycode == KeyEvent.KEYCODE_BACK){
    		startActivity(new Intent(propEditor.this, fileManager.class));
    		finish();
    		return true;
    	}
    	return false;
    }
    public boolean onCreateOptionsMenu(Menu menu){
    	menu.add(0, 0, 0, "Menu").setIcon(android.R.drawable.ic_menu_preferences);
    	menu.add(0, 1, 0, "Save").setIcon(android.R.drawable.ic_menu_agenda);
    	menu.add(0, 2, 0, "Block/Unblock").setIcon(android.R.drawable.ic_menu_close_clear_cancel);
    	menu.add(0, 3, 0, "Free Edit Mode").setIcon(android.R.drawable.ic_menu_edit);
    	return true;
    }
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
		case 0: //menu
			startActivity(new Intent(propEditor.this, fileManager.class));
			finish();
			break;
		case 1:
			new Thread(){
				public void run(){
					save();
				}
			}.start();
			Toast.makeText(propEditor.this, "Saving...  this takes some seconds", 1).show();
			startActivity(new Intent(propEditor.this, fileManager.class));
			finish();
			break;
		case 2:
			changeState();
			break;
		case 3:
			Intent i = new Intent();
			i.setClass(propEditor.this, freeEditor.class);
			i.putExtra("path", path);
			startActivity(i);
			finish();
			break;
		}
		return true;
	}
}
