package de.Fr4gg0r.SGS.Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class freeEditor extends AnimatedActivity{
	private String path;
	private EditText e;
	public void onCreate(Bundle b){
		super.onCreate(b);
		path = getIntent().getStringExtra("path");
		setTitle(path);
		final File file = new File(path);
		if(!file.canRead())getReadAccess();
		final StringBuilder sb = new StringBuilder();
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(file));
			while(reader.ready()){
				sb.append(reader.readLine()).append("\n");
			}
		} catch (Exception e) {
			Log.d("exception", e.toString());
		}
		setContentView(R.layout.freeeditor);
		e = (EditText) findViewById(R.id.freeeditor_e1);
		e.setText(sb.toString());
		e.setHorizontallyScrolling(true);
	}
	private void save(){
		final File file = new File(path);
		try {
			BufferedWriter out = null;
			out = new BufferedWriter(new FileWriter("/sdcard/"+file.getName()));
			out.write(e.getText().toString());
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
    public boolean onCreateOptionsMenu(Menu menu){
    	menu.add(0, 1, 0, "Save").setIcon(android.R.drawable.ic_menu_agenda);
    	return true;
    }
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()){
		case 0: //menu
			startActivity(new Intent(freeEditor.this, fileManager.class));
			finish();
			break;
		case 1:
			new Thread(){
				public void run(){
					save();
				}
			}.start();
			Toast.makeText(freeEditor.this, "Saving...  this takes some seconds", 1).show();
			startActivity(new Intent(freeEditor.this, fileManager.class));
			finish();
			break;
		}
		return true;
	}
	private void getReadAccess(){
		//we can't read, since we are (probably) su let's change this...
		try{
			Log.d("make read access", "-");
			final Process p = Runtime.getRuntime().exec("su");
			final DataOutputStream out = new DataOutputStream(p.getOutputStream());
			out.writeBytes("busybox mount -o remount,rw /system\n");
			out.writeBytes("chmod 664 "+path);
			out.writeBytes("busybox mount -o remount,ro /system\n");
			out.flush();
			out.close();
			p.waitFor();
			p.destroy();
		}
		catch(Exception e){}
	}
}
