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
import android.graphics.Color;
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

public class UpgradeLauncher extends AnimatedListActivity{
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
		tx.setText("Upgrading the tw launcher is a bit critical, because if something goes wrong, and there's no working launcher, " +
				"you got a problem.\n" +
				"So before trying any of these, download a second launcher (for example 'launcher pro') from market.\n" +
				"If everything works fine after update, you can uninstall it.\n" +
				"Your stock launcher will be backuped in /sdcard/sgstools and can will get reinstalled if you select 'apply script' and then" +
				"'restore_launcher.txt'\n" +
				"Remember to remove all apps from the dock before upgrading.");
		tx.setTextColor(Color.RED);
		fakeFileNames.add("Faster TouchWizLauncher (GTG)");
		fileNames.add("TouchWiz30Launcher.apk");
		descriptions.add("This is the TouchWiz Launcher from Samsung, modified by gtg465x. It appears to be faster and smoother... " +
				"The colored iphone like tiles in app menu are removed.");
		urls.add("http://www.informatik.uni-bremen.de/~jrahlf/launcher/TouchWiz30Launcher.apk");
		
		fakeFileNames.add("Faster TouchWizLauncher (GTG) + 5 icon dock");
		fileNames.add("TouchWiz30Launcher.apk");
		descriptions.add("This is the TouchWiz Launcher from Samsung, modified by gtg465x. It appears to be faster and smoother..." +
				"The colored iphone like tiles in app menu are removed.\n" +
				"This version features a 5 icons dock.");
		urls.add("http://www.informatik.uni-bremen.de/~jrahlf/launcher_newDock/TouchWiz30Launcher.apk");
		
		fakeFileNames.add("GTG TouchWizLauncher  with colored tiles");
		fileNames.add("TouchWiz30Launcher.apk");
		descriptions.add("This is the TouchWiz Launcher from Samsung, modified by gtg465x. It appears to be faster and smoother...\n" +
				"This version features the standard colored tiles in the app menu.");
		urls.add("http://www.informatik.uni-bremen.de/~jrahlf/launcher_tiles/TouchWiz30Launcher.apk");
		
		fakeFileNames.add("GTG TouchWizLauncher  with colored tiles + 5 icon dock");
		fileNames.add("TouchWiz30Launcher.apk");
		descriptions.add("This is the TouchWiz Launcher from Samsung, modified by gtg465x. It appears to be faster and smoother...\n" +
				"This version features the standard colored tiles in the app menu + 5 icon dock.");
		urls.add("http://www.informatik.uni-bremen.de/~jrahlf/launcher_tiles_newDock/TouchWiz30Launcher.apk");
		
		fakeFileNames.add("GTG TouchWizLauncher  with colored tiles + 5 icon dock without landscape");
		fileNames.add("TouchWiz30Launcher.apk");
		descriptions.add("This is the TouchWiz Launcher from Samsung, modified by gtg465x. It appears to be faster and smoother...\n" +
				"This version features the standard colored tiles in the app menu (same as above but without landscape mode).");
		urls.add("http://www.android-hilfe.de/attachments/samsung-galaxy-s/20778d1292268931-sgs-tools-app-zum-schnellen-bearbeiten-touchwiz30launcher.apk");
		
		fakeFileNames.add("GTG TouchWizLauncher with  5 icon dock + DENA THEMED");
		fileNames.add("TouchWiz30Launcher.apk");
		descriptions.add("This is the TouchWiz Launcher from Samsung, modified by gtg465x. It appears to be faster and smoother...\n" +
				"This version features the standard colored tiles in the app menu."+
				"\n\nDena Theme version.");
		urls.add("http://www.android-hilfe.de/attachments/root-hacking-modding-fuer-samsung-galaxy-s/17711d1289155290-touchwiz-mod-ungefaehr-10-mal-schneller-als-original-touchwiz30launcher.apk");
		
		fakeFileNames.add("Themed TwLauncher (gtg based) with settings");
		fileNames.add("TouchWiz30Launcher.apk");
		descriptions.add("Based on gtg modded Launcher. Includes 5 icon dock + icon tiles in app menu.\n" +
				"Modified by Fr4gg0r to enable settings. " +
				"Themed by philippsp from android-hilfe.de\nYour stock launcher will be backuped in /sdcard/sgstools/ (no override)!"); 
		urls.add("http://www.android-hilfe.de/attachments/themes-fuer-samsung-galaxy-s/19363d1291330145-theme-black-navy-theme-google-search-widget-schwarz-tester-gesucht-touchwiz30launcher.apk");		
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
				}
				catch(Exception e){
					pg.dismiss();
					secureToast(e.toString());
				}
				pg.dismiss();
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
				default : 
					if(android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.ECLAIR_MR1){
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
										AlertDialog.Builder builder = new AlertDialog.Builder(UpgradeLauncher.this);
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
				Toast.makeText(UpgradeLauncher.this, msg, 1).show();
			}
		});
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
}
