package de.Fr4gg0r.SGS.Tools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import de.Fr4gg0r.SGS.Tools.MyMenu.MyMenuCallback;

public class stockApps extends AnimatedListActivity{
	private MyMenu menu;
	private final Handler handler = new Handler();

	public void onCreate(Bundle b){
		super.onCreate(b);
		final ArrayList<String> fileNames = new ArrayList<String>();
		final ArrayList<String> fakeFileNames = new ArrayList<String>();
		final ArrayList<String> descriptions = new ArrayList<String>();
		final ArrayList<String> urls = new ArrayList<String>();
		setContentView(R.layout.scriptreceivermain);
		final TextView tx = (TextView) findViewById(R.id.text_listview);
		tx.setText("The files are downloaded from the Internet, so only do this if you have a data plan or are on wifi\n\n" +
				"You can revert any of this hacks with the matching script.. for example restore_gallery for gallery upgrade etc.");
		String name = android.os.Build.MODEL;
		String firm = android.os.Build.FINGERPRINT;
		
		urls.add("");
		fileNames.add("Upgrade tw launcher");
		fakeFileNames.add("Upgrade tw launcher");
		descriptions.add("");
		if(firm.contains("XXJPU") || firm.contains("XXJPX") || firm.contains("XXJPY")){
			urls.add("http://www.android-hilfe.de/attachments/samsung-galaxy-s/21523d1292955185-sgs-tools-app-zum-schnellen-bearbeiten-browser-jpy.apk");
			fileNames.add("Browser.apk");
			fakeFileNames.add("Stock Browser with more tabs");
			descriptions.add("With this modded browser, you can open more than 4 tabs.\n"+
					"Hacked by me (Fr4gg0r)\n" +
					"From JPY firmware");
		}
		else{
			urls.add("http://www.android-hilfe.de/attachments/root-hacking-modding-fuer-samsung-galaxy-s/17560d1288971488-browserhack-browser.apk");
			fileNames.add("Browser.apk");
			fakeFileNames.add("Stock Browser with useragent setting");
			descriptions.add("With this modded browser, you can set & save your useragent." +
					"For jpu/jpo firmware.");
		}

		
		fakeFileNames.add("Modded 3D Gallery ");
		fileNames.add("Gallery3D.apk");
		descriptions.add("This is the 3D Gallery, modded by b-reezy. \n" +
				"It does not have the battery drain bug like the version from Samsung.");
		urls.add("http://www.android-hilfe.de/attachments/anleitungen-fuer-samsung-galaxy-s/20510d1292067753-froyo-akkulaufzeit-verlaengern-gallery3d.apk");
		
		fakeFileNames.add("Modded Contacts app showing all letters");
		fileNames.add("Contacts.apk");
		descriptions.add("This is the stock Contacts.apk, modded to show all letter in the right index. \n" +
				"Thanks to ock from xda for this fix!\nYour stock contacts.apk and .odex will be backuped in /sdcard/sgstools/\n" +
				"You maybe have to reboot the phone after applying this.\nOnly translated in english and german (thanks scheichuwe).");
		urls.add("http://db.tt/VBvTZTQ");
		
		fakeFileNames.add("Hacked Camera.apk");
		fileNames.add("Camera.apk");
		if(name.equalsIgnoreCase("SAMSUNG-SGH-I897")){
			descriptions.add("Hacked Camera, by me (Fr4gg0r). (Captivate version) \n" +
					"-Make pictures with pressing power button\n" +
					"-Make pictures even at low battery");
			urls.add("http://www.android-hilfe.de/attachments/root-hacking-modding-fuer-samsung-galaxy-s/20170d1291736874-powerbutton-mod-im-kamera-app-camera.apk");
		}
		else{
			if(firm.contains("XXJPU") || firm.contains("XXJPX") || firm.contains("XXJPY")){
				descriptions.add("Hacked Camera, by me (Fr4gg0r).\n" +
						"(JPU version) \n" +
						"-Make pictures with pressing power button\n" +
						"-Make pictures even at low battery");
				urls.add("http://www.android-hilfe.de/attachments/root-hacking-modding-fuer-samsung-galaxy-s/20379d1291913991-powerbutton-mod-im-kamera-app-camera.apk");
			}
			else{
				descriptions.add("Hacked Camera, by me (Fr4gg0r). \n" +
						"-Make pictures with pressing power button\n" +
						"-Make pictures even at low battery");
				urls.add("http://www.android-hilfe.de/attachments/root-hacking-modding-fuer-samsung-galaxy-s/19946d1291508247-powerbutton-mod-im-kamera-app-camera.apk");
			}
		}
		fakeFileNames.add("Email app from Galaxy Tab");
		fileNames.add("Email.apk");
		descriptions.add("This is the Email.apk from galaxy tab.\n" +
				" Try it out.. \n" +
				"If you want to go back to stock email.apk, download 'stock email app'.");
		urls.add("http://www.android-hilfe.de/attachments/anleitungen-fuer-samsung-galaxy-s/20511d1292067913-froyo-akkulaufzeit-verlaengern-email.apk");
		
		fakeFileNames.add("New Market app");
		fileNames.add("Vending.apk");
		descriptions.add("This is the new Market app from google.\n" +
				" Try it out..");
		urls.add("http://www.android-hilfe.de/attachments/samsung-galaxy-s/20590d1292101734-sgs-tools-app-zum-schnellen-bearbeiten-vending.apk");
		
		fakeFileNames.add("stock email app");
		fileNames.add("Email.apk");
		descriptions.add("Temporay, until I figure out why restoring the email.apk does not work..");
		urls.add("http://www.android-hilfe.de/attachments/samsung-galaxy-s/20603d1292111209-sgs-tools-app-zum-schnellen-bearbeiten-email.apk");
		
		/*fakeFileNames.add("Swype 2.6");
		fileNames.add("Swype.apk");
		descriptions.add("This is swype in the version 2.6 . Your stock lib.so and swype.apk will be backuped in " +
				"/sdcard/sgstools/\n" +
				"After this, long touch an editText and select input method, then select swype");
		urls.add("http://download387.mediafire.com/x1vhkk6oo1wg/5tfv545li4t9fhn/Swype.apk");*/
		setListAdapter(new ArrayAdapter<String>(this, R.layout.simpletextview, fakeFileNames));
		menu = new MyMenu(this);
	    addContentView(menu, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	    menu.addSection(getString(R.string.menu), getResources().getDrawable(android.R.drawable.ic_menu_preferences));
	    menu.setCallback(new MyMenuCallback(){
	       public void menuItemSelected(int which){
	    	   //only 1 section so which always 0
	    	   finish();
	       }
	    });
	       getListView().setOnItemClickListener(new OnItemClickListener() {
		         public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {	
		        	 if(position == 0){
		        		 startActivity(new Intent(stockApps.this, UpgradeLauncher.class));
		        		 return;
		        	 }
		        	 promptUser(fileNames.get(position), descriptions.get(position), urls.get(position), position);
		         	}
		       });
	}
	private void promptUser(final String fileName, final String description, final String url, final int position){
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Important!");
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		final ScrollView sv = new ScrollView(this);
		final LinearLayout l = new LinearLayout(this);
		l.setOrientation(LinearLayout.VERTICAL);
		l.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		final TextView tx = new TextView(this);
		tx.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		tx.setTextSize(16);
		tx.setText(description);
		final CheckBox check = new CheckBox(this);
		check.setText("I've read the above text..");
		l.addView(tx);
		l.addView(check);
		sv.addView(l);
		builder.setView(sv);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if(check.isChecked()){
					getFile(url, fileName, position, true);
					//if(position == 8)getFile("http://www.informatik.uni-bremen.de/~jrahlf/libSwypeCore.so", "libSwypeCore.so", -1, false);
				}
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.show();
	}
	private void getFile(final String url, final String fileName, final int position, boolean showPG){
		final ProgressDialog pg = new ProgressDialog(this);
		pg.setMessage("Downloading...");
		pg.setCancelable(false);
		new Thread(){
			public void run(){
				try{
				    URL u = new URL(url);
				    HttpURLConnection c = (HttpURLConnection) u.openConnection();
				    c.setRequestMethod("GET");
				    c.setDoOutput(true);
				    c.connect();
				    final File file = new File("/sdcard/"+fileName);
				    if(file.exists())file.delete();
				    final FileOutputStream f = new FileOutputStream(file);
				    final InputStream in = c.getInputStream();
				    byte[] buffer = new byte[1024];
				    int len1 = 0;
				    while ( (len1 = in.read(buffer)) != -1 ) {
				      f.write(buffer,0, len1);
				    }
				    f.close();
				    in.close();
				    c.disconnect();
					pg.dismiss();
				}
				catch(Exception e){
					pg.dismiss();
					secureToast(e.toString());
					return;
				}
				handler.post(new Runnable(){
					public void run(){
						apply("/sdcard/"+fileName, fileName, position);
					}
				});
			}
		}.start();
		if(showPG)pg.show();
	}
	private void apply(final String filePath, final String fileName, final int position){
		Log.d("applying", filePath);
		final String appLabel = fileName.substring(0, fileName.indexOf('.'));
		final ProgressDialog pg = new ProgressDialog(this);
		final File apkBackup = new File("/sdcard/sgstools/"+appLabel+".apk");
		final File odexBackup = new File("/sdcard/sgstools/"+appLabel+".odex");
		pg.setMessage("Applying...");
		new Thread(){
			public void run(){
				switch(position){
				case -1: //lib swype core
					if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ECLAIR_MR1){
						try {
							final Process p = Runtime.getRuntime().exec("su");
							final DataOutputStream out = new DataOutputStream(p.getOutputStream());
							final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
							out.writeBytes("busybox mount -o remount,rw /system\n");
							out.writeBytes("busybox cp -i /system/lib/"+fileName+" /sdcard/sgstools/\n");
							out.writeBytes("busybox rm -f /system/lib/"+fileName+"\n");
							out.writeBytes("busybox cp -f "+filePath+" /system/lib/\n");
							out.writeBytes("rm "+filePath+"\n");
							out.flush();
							out.close();
							p.waitFor();
							while(reader.ready()){
								Log.d("error", reader.readLine());
							}
							reader.close();
						} catch (Exception e) {	
							pg.dismiss();
						} //nobody cares
						pg.dismiss();
					}
					else {
						pg.dismiss();
						secureToast("You need 2.2 for this fix!" );
					}
					break;
				default : 
					if(position == 4 || android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ECLAIR_MR1){
						try {
							final Process p = Runtime.getRuntime().exec("su");
							final DataOutputStream out = new DataOutputStream(p.getOutputStream());
							final BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
							out.writeBytes("busybox mount -o remount,rw /system\n");
							if(!apkBackup.exists())copyFile("/system/app/"+appLabel+".apk", "/sdcard/sgstools/"+appLabel+".apk");
								//out.writeBytes("busybox cp -f /system/app/"+appLabel+".apk /sdcard/sgstools/\n");
							if(!odexBackup.exists())copyFile("/system/app/"+appLabel+".odex", "/sdcard/sgstools/"+appLabel+".odex");
								//out.writeBytes("busybox cp -f /system/app/"+appLabel+".odex /sdcard/sgstools/\n");
							out.writeBytes("busybox rm -f /system/app/"+appLabel+".apk\n");
							out.writeBytes("busybox rm -f /system/app/"+appLabel+".odex\n");
							out.writeBytes("busybox cp -f "+filePath+" /system/app/\n");
							out.writeBytes("rm "+filePath+"\n");
							out.flush();
							out.close();
							p.waitFor();
							final StringBuilder sb = new StringBuilder();
							boolean show = false;
							if(reader.ready())show = true;
							while(reader.ready()){
								sb.append(reader.readLine());
							}
							if(show){
								runOnUiThread(new Runnable(){
									public void run(){
										sb.append("\nIf it says 'busybox not found', install busybox from market!");
										AlertDialog.Builder builder = new AlertDialog.Builder(stockApps.this);
										builder.setTitle("Occurred errors:");
										builder.setMessage(sb.toString());
										builder.show();
									}
								});
							}
							else{
								secureToast("Applied");
							}
							reader.close();
							p.destroy();
						} catch (Exception e) {	
							pg.dismiss();
						} //nobody cares
						pg.dismiss();
					}
					else {
						pg.dismiss();
						secureToast("You need 2.2 for this fix!" );
					}
					break;
				}
			}
		}.start();
		pg.show();
	}
	private void secureToast(final String msg){
		handler.post(new Runnable(){
			public void run(){
				Toast.makeText(stockApps.this, msg, 1).show();
			}
		});
	}
	private void copyFile(String in, String out){
		try{
	        FileChannel inChannel = new FileInputStream(in).getChannel();
	        FileChannel outChannel = new FileOutputStream(out).getChannel();
	        inChannel.transferTo(0, inChannel.size(), outChannel);
	        inChannel.close();
	        outChannel.close();
		}
		catch(Exception e){
			Log.d("exception", e.toString());
		}
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
