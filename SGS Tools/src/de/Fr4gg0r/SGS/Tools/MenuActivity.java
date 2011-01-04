package de.Fr4gg0r.SGS.Tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import de.Fr4gg0r.SGS.Tools.MyMenu.MyMenuCallback;

public class MenuActivity extends AnimatedListActivity{
	private MyMenu menu;
	
    private void storeToSd(int rawId, String name, boolean forceOverwrite){
		InputStream in = getResources().openRawResource(rawId);
		File file = new File("/sdcard/sgstools/"+name);
		if(file.exists()){
			if(forceOverwrite){
				Log.d("overriding old file", "-");
				file.delete();
			}
			else return;
		}
		int size = 0;
		try {
			size = in.available();
			byte[] buffer = new byte[size];
			in.read(buffer);
			in.close();
			FileOutputStream save = new FileOutputStream(file);
			save.write(buffer);
			save.flush();
			save.close();
		} catch (Exception e) {
			Log.d("exception", e.toString());
		}
    }
	public void onCreate(Bundle b){
		super.onCreate(b);
		final SharedPreferences prefs = getSharedPreferences("settings", 0);
		if(prefs.getBoolean("mainFirstStart", true)){
			Log.d("firstStart", "-");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Disclaimer");
			builder.setMessage("You are using this software at your own risk. Except for the Secret codes, root and an installed busybox is needed. Any tools included in this software, (besides the secret codes) could damage your phone, so that you phone is unusable and you have to reflash it.\nYOU USE THIS SOFTWARE AT YOUR OWN RISK, THE DEVELOPER OF THIS SOFTWARE IS NOT RESPONSIBLE FOR ANY DAMAGES CAUSED BY THIS APP\n----------------\nMost of the features of this app requires an installed busybox");
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setCancelable(false);
			builder.setPositiveButton("I Agree", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Editor edit = prefs.edit();
					edit.putBoolean("mainFirstStart", false);
					edit.commit();
					dialog.dismiss();
					final ProgressDialog pg = new ProgressDialog(MenuActivity.this);
					pg.setMessage("Checking for root + busybox.. \n" +
							"Creating scripts...");
					//put files from assets to sd to minimize size of app
					File file = new File("/sdcard/sgstools");
					if(!file.exists())file.mkdir();
					file = new File("/sdcard/sgstools/.nomedia");
					try {						
						file.createNewFile();
					}catch (Exception e) {}
					new Thread(){
						public void run(){
							storeToSd(R.raw.standard, "standard.png", false);
							storeToSd(R.raw.clear_thumb_db, "clean_thumb_db.txt", false);
							storeToSd(R.raw.restore_browser, "restore_browser.txt", false);
							storeToSd(R.raw.reboot_download, "reboot_download.png", false);
							storeToSd(R.raw.reboot_recovery, "reboot_recovery.png", false);
							storeToSd(R.raw.shutdown, "shutdown.png", false);
							storeToSd(R.raw.reboot, "reboot.png", false);		
							storeToSd(R.raw.mount_system_ro, "mount_system_ro.q.txt", false);
							storeToSd(R.raw.mount_systen_rw, "mount_system_rw.q.txt", false);
							storeToSd(R.raw.delete_batterystats_, "delete_batterystats_.q.txt", false);
							storeToSd(R.raw.backup_efs_, "backup_efs_.q.txt", false);
							storeToSd(R.raw.clear_market_cache, "clear_market_cache.q.txt", false);
							storeToSd(R.raw.restore_email, "restore_email.txt", false);
							storeToSd(R.raw.restore_market, "restore_market.txt", false);
							if(!Utils.checkRoot()){
								noRoot();
								return;
							}
							if(!Utils.checkBusyBox()){
								noBusyBox();
							}
							pg.dismiss();
						}
					}.start(); 
					pg.show();
				}
			});
			builder.setNegativeButton("I do not agree", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			});
		builder.create().show();
		}
		if(prefs.getBoolean("ver419", true)){
			Editor edit = prefs.edit();
			edit.remove("ver418");
			edit.putBoolean("ver419", false);
			edit.commit();
			AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
			builder.setTitle("Changes -- Version 4.1.9");
			builder.setMessage("-included 'clear thumb db script'");
			builder.show();
			new Thread(){
				public void run(){
					storeToSd(R.raw.clear_thumb_db, "clean_thumb_db.txt", false);
					storeToSd(R.raw.restore_browser, "restore_browser.txt", false);
					storeToSd(R.raw.restore_launcher, "restore_launcher.txt", false);
					storeToSd(R.raw.restore_contacts, "restore_contacts.txt", false);
					storeToSd(R.raw.restore_gallery, "restore_gallery.txt", false);
					storeToSd(R.raw.restore_camera, "restore_camera.txt", false);
					storeToSd(R.raw.restore_email, "restore_email.txt", false);
					storeToSd(R.raw.restore_market, "restore_market.txt", false);
					
				}
			}.start();
		}
		setTitle("Version 4.1.9");
	    setListAdapter(new Adapter(this));
		menu = new MyMenu(this);
	       addContentView(menu, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	       menu.addSection("Exit", getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
	       menu.setCallback(new MyMenuCallback(){
	    	   public void menuItemSelected(int which){
	    		   //only 1 section so which always 0
	    		   finish();
	    	   }
	       });
	    getListView().setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int position, long id){
				Intent intent = new Intent();
				switch(position){
				case 0: 
					intent.setClass(MenuActivity.this, Info.class);
					break;
				case 1: 
					intent.setClass(MenuActivity.this, remove.class);
					break;
				case 2:
					intent.setClass(MenuActivity.this, Backup.class);
					break;
				case 3: 
					intent.setClass(MenuActivity.this, ScriptReceiver.class);
					break;
				case 4: 
					intent.setClass(MenuActivity.this, BootSoundSettings.class);
					break;
				case 5:
					intent.setClass(MenuActivity.this, fileManager.class);
					break;
				case 6:
					intent.setClass(MenuActivity.this, marketFix.class);
					break;
				case 7:
					//if(!(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ECLAIR_MR1)){
					//	Toast.makeText(MenuActivity.this, "Caution.. not everything is tested to work on 2.1...", 1).show();
					//}
					intent.setClass(MenuActivity.this, stockApps.class);
					break;
				case 8: intent.setClass(MenuActivity.this, Gps.class);
					break;
				}
				startActivity(intent);
			}
		});
	}
	private void noRoot(){
		runOnUiThread(new Runnable(){
			public void run(){
				AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
				builder.setTitle("Root");
				builder.setMessage("Your phone is not rooted. You will need root for most features of this app. You can root your device with " +
						"clicking 'Yes', which will launch the market where you can download an app for rooting. (highly recommended)");
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {	
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.z4mod.z4root")));
					}
				});
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {	
						dialog.dismiss();
					}
				});
				builder.show();
			}
		});
	}
	private void noBusyBox(){
		runOnUiThread(new Runnable(){
			public void run(){
				AlertDialog.Builder builder = new AlertDialog.Builder(MenuActivity.this);
				builder.setTitle("BusyBox");
				builder.setMessage("It seems as if there is no busybox on your phone. This app (and many other root apps) needs busybox to run properly.\nInstall busybox now (highly recommended)?");
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {	
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=stericson.busybox")));
					}
				});
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {	
						dialog.dismiss();
					}
				});
				builder.show();
			}
		});
	}
	private class Adapter extends BaseAdapter{
		private LayoutInflater mInflater;
		private final String[] activities = new String[]{"Secret Codes", "Cleanup System Apps",	"Backup/Restore Homescreen", 
				"Apply script", "Modify Boot Sound / Shutdown Sound", ".prop Editor", "MarketFix",
				"Upgrade Stock apps", "Gps Fix"}; //"Miscellanous tweaks"
		private final Drawable[] icons = new Drawable[activities.length];
		public Adapter(Context context){
			mInflater = LayoutInflater.from(context);
			final Resources res = getResources();
			icons[0] = res.getDrawable(android.R.drawable.ic_dialog_info);
			icons[1] = res.getDrawable(android.R.drawable.ic_menu_compass);
			icons[2] = res.getDrawable(R.drawable.ic_menu_refresh);
			icons[3] = res.getDrawable(android.R.drawable.ic_menu_upload);
			icons[4] = res.getDrawable(android.R.drawable.ic_menu_agenda);
			icons[5] = icons[4];
			icons[6] = res.getDrawable(android.R.drawable.btn_star_big_off);
			icons[7] = res.getDrawable(android.R.drawable.btn_dropdown);
			icons[8] = res.getDrawable(android.R.drawable.btn_radio);
		}
		public int getCount() {
			// TODO Auto-generated method stub
			return activities.length;
		}

		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.menuactivity, null);
				holder = new ViewHolder();
				holder.activity = (TextView)convertView.findViewById(R.id.menu_row_text);
				holder.icon = (ImageView)convertView.findViewById(R.id.menu_row_icon);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.activity.setText(activities[position]);
			holder.icon.setImageDrawable(icons[position]);
			return convertView;
		}
		public long getItemId(int position) {
			return position;
		}
	}
    static class ViewHolder {
        TextView activity;
        ImageView icon;
    }
    public boolean onKeyDown(int keycode, KeyEvent event){
    	if(keycode == KeyEvent.KEYCODE_MENU){
    		menu.showOrDismiss();
    		return true;
    	}
    	if(keycode == KeyEvent.KEYCODE_BACK){
    		menu.showOrDismiss();
    		return true;
    	}
    	return false;
    }
}
