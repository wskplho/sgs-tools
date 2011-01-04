package de.Fr4gg0r.SGS.Tools;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateShortcut extends AnimatedActivity{
	public void onCreate(Bundle b){
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.createshortcut);
		setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_info);
		final EditText commands = (EditText) findViewById(R.id.createshortcut_e1);
		final EditText name = (EditText) findViewById(R.id.createshortcut_e2);
		final CheckBox su = (CheckBox) findViewById(R.id.createshortcut_check1);
		final Spinner sp = (Spinner) findViewById(R.id.createshortcut_s1);
		final Adapter adapter = new Adapter(this, 0);
		sp.setAdapter(adapter);
		final OnClickListener click = new OnClickListener(){
			public void onClick(View v){
				switch(v.getId()){
				case R.id.createshortcut_b1: //Button_OK
					String shortcutName = name.getText().toString();
					//not necessary//
					/*if(shortcutName.length() == 0){
						Toast.makeText(CreateShortcut.this, getString(R.string.createshortcutNoName), 0).show();
						break;
					}*/
					String cmds = commands.getText().toString();
					if(cmds.length() == 0){
						Toast.makeText(CreateShortcut.this, getString(R.string.createshortcutNoCommands), 0).show();
						break;
					}
					Intent shortcutIntent = new Intent();
					shortcutIntent.setClass(CreateShortcut.this, ExecuteShortcut.class);
					shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					shortcutIntent.putExtra("commands", cmds);
					shortcutIntent.putExtra("su", su.isChecked());
					Intent intent = new Intent();
					intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
					intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, shortcutName);
					BitmapDrawable bd = (BitmapDrawable) adapter.getSelectedDrawable(sp.getSelectedItemPosition());
					Bitmap bit = bd.getBitmap();
					intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bit);
					intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
					CreateShortcut.this.sendBroadcast(intent);
					finish();
					break;
				case R.id.createshortcut_b2: //Button_Cancel
					finish();
					break;
				}
			}
		};
		((Button)findViewById(R.id.createshortcut_b1)).setOnClickListener(click);
		((Button)findViewById(R.id.createshortcut_b2)).setOnClickListener(click);
		commands.setOnLongClickListener(new View.OnLongClickListener() {
			public boolean onLongClick(View v) {
				final String[] basics = new String[]{"shutdown", "reboot", "reboot recovery", "reboot download", "mount system rw", "mount system ro"};
				final String[] values = new String[]{"reboot -p", "reboot", "reboot recovery", "reboot download", "busybox mount -o remount,rw /system", "busybox mount -o remount,ro /system"};
				AlertDialog.Builder builder = new AlertDialog.Builder(CreateShortcut.this);
				builder.setAdapter(new ArrayAdapter<String>(CreateShortcut.this, R.layout.completionrow, basics), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						commands.setText(values[which]);
					}
				});
				builder.create().show();
				return true;
			}
		});
	}
	private class Adapter extends ArrayAdapter{

		private LayoutInflater mInflater;
		private final ArrayList<String> descriptions = new ArrayList<String>();
		private final ArrayList<Drawable> icons = new ArrayList<Drawable>();
    	public Adapter(Context context, int blub){
    		super(context, blub);
    		mInflater = LayoutInflater.from(context);
    		File sd = new File("/sdcard/sgstools");
    		if(sd.exists()){
        		sd.listFiles(new FileFilter(){
        			public boolean accept(File file){
        				if(file.getAbsolutePath().endsWith(".png") || file.getAbsolutePath().endsWith(".PNG")){
            				icons.add(Drawable.createFromPath(file.getAbsolutePath()));
            				descriptions.add(file.getName());
        				}
        				return false;
        			}
        		});   
    		}
    		else {
    			sd.mkdir();
    			icons.add(getResources().getDrawable(R.drawable.icon));
    			descriptions.add("standard");
    		}
    	}
		public int getCount() {
			return descriptions.size();
		}
		public Object getItem(int arg0) {
			return arg0;
		}
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}
		public View getView(final int position, View convertView, ViewGroup parent) {
			TextView tx = new TextView(getApplicationContext());
			tx.setText(descriptions.get(position));
			tx.setTextColor(Color.BLACK);
			return tx;
		}
		public View getDropDownView (int position, View convertView, ViewGroup parent){
			ViewHolder holder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.createshortcutspinnerrow, null);
				holder = new ViewHolder();
				holder.description = (TextView)convertView.findViewById(R.id.createshortcutspinnerrow_t1);
				holder.icon = (ImageView)convertView.findViewById(R.id.createshortcutspinnerrow_i1);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.description.setText(descriptions.get(position));
			holder.icon.setImageDrawable(icons.get(position));
			return convertView;
			
		}
		protected Drawable getSelectedDrawable(int pos){
			if(pos < 0)return getResources().getDrawable(R.drawable.icon);
			return icons.get(pos);
		}
	}
    static class ViewHolder {
        TextView description;
        ImageView icon;
    }
}
