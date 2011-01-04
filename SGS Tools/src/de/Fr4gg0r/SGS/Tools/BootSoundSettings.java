package de.Fr4gg0r.SGS.Tools;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import de.Fr4gg0r.SGS.Tools.MyMenu.MyMenuCallback;

public class BootSoundSettings extends AnimatedActivity{
	private MyMenu menu;
	private boolean isVibrant;
	public void onCreate(Bundle b){
		super.onCreate(b);
		final SharedPreferences shared = getSharedPreferences("settings", 0);
		final ProgressDialog pg = new ProgressDialog(this);
		pg.setMessage("Backuping stock sounds...");
		if(shared.getBoolean("backupSounds", true)){
			new Thread(){
				public void run(){
					//we do this to save the stock sounds
					Log.d("first start", "true");
					final File file = new File("/system/media/audio/ui/shutdown.ogg");
					final String dest = file.exists() ? "shutdown.ogg" : "Shutdown_128.ogg";
					if(!file.exists())isVibrant = true;
					try{
						Process p = Runtime.getRuntime().exec("su");
						DataOutputStream out = new DataOutputStream(p.getOutputStream());
						out.writeBytes("busybox mount -o remount,rw /system\n");
						out.writeBytes("cd /system/media/audio/ui\n");
						out.writeBytes("busybox cp -i "+dest+" shutdown.ogg.bak\n"); 
						out.writeBytes("cd /system/etc\n");
						out.writeBytes("busybox cp -i PowerOn.wav PowerOn.wav.bak\n");  
						out.flush();
						out.close();
						p.waitFor();
						p.destroy();
						pg.dismiss();
						//PowerOn.wav.bak and shutdown.ogg.bak are now the stock sounds
					}
					catch(Exception e){}
					Editor edit = shared.edit();
					edit.putBoolean("backupSounds", false);
					if(isVibrant)edit.putBoolean("isVibrant", isVibrant);
					edit.commit();
				}
			}.start();
			pg.show();
		}
		isVibrant = shared.getBoolean("isVibrant", false);
		final File file = new File("/sdcard/sgstools");
		if(!file.exists()){
			file.mkdir();
		}
		bootSounds.add("standard");
		bootSounds.add("no bootsound");
		shutdownSounds.add("standard");
		shutdownSounds.add("no bootsound");
		file.listFiles(new FileFilter(){
			public boolean accept(File f){
				if(f.getName().endsWith(".wav")){
					bootSounds.add(f.getAbsolutePath());
				}
				return false;
			}
		});
		file.listFiles(new FileFilter(){
			public boolean accept(File f){
				if(f.getName().endsWith(".ogg")){
					shutdownSounds.add(f.getAbsolutePath());
				}
				return false;
			}
		});
		setContentView(R.layout.bootsoundsettings);
		menu = new MyMenu(this);
	    addContentView(menu, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	       menu.addSection(getString(R.string.menu), getResources().getDrawable(android.R.drawable.ic_menu_preferences));
	       menu.setCallback(new MyMenuCallback(){
	    	   public void menuItemSelected(int which){
	    		   //only 1 section so which always 0
	    		   finish();
	    	   }
	       });
		Adapter a1 = new Adapter(this, 0);
		a1.setList(bootSounds);
		Adapter a2 = new Adapter(this, 0);
		a2.setList(shutdownSounds);
		final Spinner s1 = (Spinner) findViewById(R.id.bootsoundsettings_s1);
		final Spinner s2 = (Spinner) findViewById(R.id.bootsoundsettings_s2);
		s1.setAdapter(a1);
		s2.setAdapter(a2);
		s1.setOnItemSelectedListener(new OnItemSelectedListener(){

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(arg2 <2)return;
				Log.d(Integer.toString(s1.getSelectedItemPosition()), Integer.toString(s2.getSelectedItemPosition()));
				String source = ((TextView)arg1).getText().toString();
				Log.d("source", source);
				MediaPlayer mp = new MediaPlayer();
				try {
					mp.setDataSource(source);
					mp.prepare();
				} catch (Exception e) {
					Toast.makeText(BootSoundSettings.this, "This sound cannot be played", 0).show();
				}
				mp.start();
				mp.release();
			}
			public void onNothingSelected(AdapterView<?> arg0) {
				// i don't care				
			}
		});
		final Button b1 = (Button) findViewById(R.id.bootsoundsettings_b1);
		b1.setCompoundDrawables(getResources().getDrawable(android.R.drawable.btn_star), null, null, getResources().getDrawable(android.R.drawable.sym_call_incoming));
		b1.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				//UGLY STYLE !!!!!!111
				Log.d(Integer.toString(s1.getSelectedItemPosition()), Integer.toString(s2.getSelectedItemPosition()));
				Process p = null;
				DataOutputStream out = null;
				try {
					p = Runtime.getRuntime().exec("su");
					out = new DataOutputStream(p.getOutputStream());
					out.writeBytes("busybox mount -o remount,rw /system\n");
					switch(s1.getSelectedItemPosition()){
					case 0: //standard
							out.writeBytes("busybox cp -f /system/etc/PowerOn.wav.bak /system/etc/PowerOn.wav\n");
							Log.d("executed", "cp -f /system/etc/PowerOn.wav.bak /system/etc/PowerOn.wav\n");
						break;
					case 1: //no sound
							out.writeBytes("busybox rm -f /system/etc/PowerOn.wav\n");
							Log.d("executed", "busybox rm -f /system/etc/PowerOn.wav\n");
						break;
					default: //all other custom sounds
							out.writeBytes("busybox cp -f "+bootSounds.get(s1.getSelectedItemPosition())+" /system/etc/PowerOn.wav\n");
							Log.d("executed:", "cp -f "+bootSounds.get(s1.getSelectedItemPosition())+" /system/etc/PowerOn.wav\n");
					}
					final String dest = isVibrant ? "/system/media/audio/ui/Shutdown_128.ogg" : "/system/media/audio/ui/shutdown.ogg";
					switch(s2.getSelectedItemPosition()){
					case 0: //standard
							out.writeBytes("busybox cp -f /system/media/audio/ui/shutdown.ogg.bak "+dest+"\n");
							Log.d("executed", "busybox cp -f /system/media/audio/ui/shutdown.ogg.bak "+dest+"\n");
						break;
					case 1: //no sound
							out.writeBytes("busybox rm -f /system/media/audio/ui/Shutdown_128.ogg\n");
							out.writeBytes("busybox rm -f /system/media/audio/ui/shutdown.ogg\n");
							Log.d("executed:", "busybox rm -f "+dest+"\n");
						break;
					default: //all other custom sounds
							out.writeBytes("busybox cp -f "+shutdownSounds.get(s2.getSelectedItemPosition())+" "+dest+"\n");
							Log.d("executed:", "busybox cp -f "+shutdownSounds.get(s2.getSelectedItemPosition())+" "+dest+"\n");
					}
						out.writeBytes("busybox mount -o remount,ro /system\n");
						out.flush();
						out.close();
						Toast.makeText(BootSoundSettings.this, "boot sound set to: "+bootSounds.get(s1.getSelectedItemPosition())+"\nshutdown sound set to: "+shutdownSounds.get(s2.getSelectedItemPosition()), 1).show();
				} catch (Exception e) {
					Toast.makeText(BootSoundSettings.this, "An error occurred\nAre you sure your phone is rooted and busybox" +
							"is installed?", 1).show();
				}
			}
		});
	}
	private final ArrayList<String> bootSounds = new ArrayList<String>();
	private final ArrayList<String> shutdownSounds = new ArrayList<String>();
	private class Adapter extends ArrayAdapter{

		private LayoutInflater mInflater;
		private ArrayList<String> list;
    	public Adapter(Context context, int blub){
    		super(context, blub);
    		mInflater = LayoutInflater.from(context);
    	}
		public int getCount() {
			return list.size();
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
			tx.setText(list.get(position));
			tx.setTextColor(Color.BLACK);
			return tx;
		}
		public View getDropDownView (int position, View convertView, ViewGroup parent){
			ViewHolder holder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.createshortcutspinnerrow, null);
				holder = new ViewHolder();
				holder.description = (TextView)convertView.findViewById(R.id.createshortcutspinnerrow_t1);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.description.setText(list.get(position));
			return convertView;
			
		}
		protected void setList(ArrayList<String> a){
			list = a;
		}
	}
    static class ViewHolder {
        TextView description;
    }
    public boolean onKeyDown(int keycode, KeyEvent event){
    	if(keycode == KeyEvent.KEYCODE_MENU){
    		menu.showOrDismiss();
    		return true;
    	}
    	if(keycode == KeyEvent.KEYCODE_BACK){
    		if(menu.isShown())menu.dismiss();
    		else finish();
    		return true;
    	}
    	return false;
    }
}
