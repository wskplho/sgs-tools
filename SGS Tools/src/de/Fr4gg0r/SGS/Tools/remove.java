package de.Fr4gg0r.SGS.Tools;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater.Factory;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class remove extends AnimatedListActivity {
	public Adapter Adapter;
	private boolean backup;
	private ProgressDialog pg; 
	private final Handler handler = new Handler();
	
	public void doRemove(){	
		if(!Utils.checkBusyBox()){
			Toast.makeText(remove.this, "BusyBox could not be found in /system/xbin or /system/bin...\n" +
					"Will try it though..\n" +
					"If it fails, you need to install BusyBox.", 1).show();
		}
		int index = 0;
		final Runtime run = Runtime.getRuntime();
		Process p = null;
		DataOutputStream out = null;
		try {
			p = run.exec("su");
			out = new DataOutputStream(p.getOutputStream());
			out.writeBytes("busybox mount -o remount,rw /system\n");
			out.flush();
		} catch (Exception e) {
		}
		StringBuilder sb = new StringBuilder();
		for(boolean state: Adapter.checked){
			if(state){
				final ApplicationInfo pi = Adapter.packageInfos.get(index);
				try {
					sb.append(pi.packageName).append("\n");
					if(backup){
						out.writeBytes("busybox cp -f " +pi.sourceDir+" /sdcard/sgstools/\n");
						out.writeBytes("busybox cp -f " +pi.sourceDir.substring(0, pi.sourceDir.length()-3)+"odex /sdcard/sgstools\n");
					}
					out.writeBytes("busybox rm -f " +pi.sourceDir+"\n");
					out.writeBytes("busybox rm -rf " +pi.dataDir+"\n");
					out.writeBytes("busybox rm -f " +pi.sourceDir.substring(0, pi.sourceDir.length()-3)+"odex\n");
					out.flush();
				} catch (Exception e) {		}
			}
			index++;
		}
		try {		
			out.writeBytes("busybox mount -o remount,ro /system\n");
			out.flush();
			out.close();	
		} catch (Exception e) {
			Log.d("exception", e.toString());
		}
		Toast.makeText(this, "Removed packages:\n"+sb.toString(), 1).show();
		finish();
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pg = new ProgressDialog(remove.this);
		Adapter = new Adapter(this);
		setListAdapter(Adapter);
		setMenuBackground();
    }
    public boolean onCreateOptionsMenu(Menu menu){
    	menu.add(0, 0, 0, "Menu").setIcon(android.R.drawable.ic_menu_preferences);
    	menu.add(0, 1, 0, "Delete Selected").setIcon(android.R.drawable.ic_menu_agenda);
    	menu.add(0, 2, 0, "Advanced Mode").setIcon(android.R.drawable.ic_menu_edit);
    	return true;
    }
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		AlertDialog al = null;
		switch(item.getItemId()){
		case 0:
			finish();
			return true;
		case 1:
			al = new AlertDialog.Builder(remove.this).create();
			al.setTitle(getString(R.string.attention));
			al.setIcon(android.R.drawable.ic_dialog_alert);
			final LinearLayout l = new LinearLayout(remove.this);
			l.setOrientation(LinearLayout.VERTICAL);
			final TextView tx = new TextView(remove.this);
			tx.setText(getString(R.string.removeWarning));
			tx.setTextSize(18);
			final CheckBox check = new CheckBox(remove.this);
			check.setText("Backup apps in /sdcard/sgstools/");
			check.setChecked(true);
			l.addView(tx);
			l.addView(check);
			al.setView(l);
			//al.setMessage(getString(R.string.removeWarning));
			al.setButton("OK", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					dialog.dismiss();
					backup = check.isChecked();
					doRemove();
				}
			});
			al.setButton2("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				} //dismiss() gets automatically called
			});
			al.show();
			break;
		case 2:
			al = new AlertDialog.Builder(remove.this).create();
			al.setTitle("Enable advanced mode?");
			al.setIcon(android.R.drawable.ic_dialog_alert);
			al.setMessage("Only do this if you are an experienced user!\n" +
					"This will let you remove ANY system app, so be careful and only use this" +
					" if you know and are able to restore them, even if it bricks your phone" +
					" (reflash then or restore via recovery).");
			al.setButton("OK", new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){
					dialog.dismiss();
					new Thread(new Runnable(){
						public void run(){
							PackageManager pm = getPackageManager();
							List<ApplicationInfo> apps = pm.getInstalledApplications(0);
							Adapter.descriptions.clear();
							Adapter.descriptions2.clear();
							Adapter.icons.clear();
							Adapter.packageInfos.clear();
							for(ApplicationInfo ai: apps){
								if((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0){
									Adapter.packageInfos.add(ai);
									Adapter.icons.add(pm.getApplicationIcon(ai));
									Adapter.descriptions2.add((String) pm.getApplicationLabel(ai));
									Adapter.descriptions.add(ai.packageName);
								}
							}
							Adapter.checked = new boolean[Adapter.icons.size()];
							pg.dismiss();
							handler.post(new Runnable(){
								public void run(){
									Adapter.notifyDataSetChanged();
								}
							});
						}
					}).start();
					pg.show();
				}
			});
			al.setButton2("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
				} //dismiss() gets automatically called
			});
			al.show();
			break;
		}
		return true;
	}
	private void setMenuBackground(){
		getLayoutInflater().setFactory( new Factory() {
			public View onCreateView ( String name, Context context, AttributeSet attrs ) {
				Log.d("name", name);
				if ( name.equalsIgnoreCase( "com.android.internal.view.menu.IconMenuItemView" ) ) {
					Log.d("FOUND", name);
					try { // Ask our inflater to create the view..
						LayoutInflater f = getLayoutInflater();
						final View view = f.createView( name, null, attrs );
						// Kind of apply our own background
						new Handler().post( new Runnable() {
							public void run () {
								//view.setBackgroundResource( R.drawable.menubackground);
								view.setBackgroundColor(Color.BLACK);
							}
						} );
						return view;
					}
					catch ( InflateException e ) {
					}
					catch ( ClassNotFoundException e ) {

					}
				}
				return null;
			}
		});
	}
    static class ViewHolder {
        TextView descriptionName;
        TextView descriptionPackage;
        ImageView icon;
        CheckBox checkbox;
    }
	private class Adapter extends BaseAdapter{
		
		public final ArrayList<Drawable> icons = new ArrayList<Drawable>();
		public final ArrayList<String> descriptions = new ArrayList<String>(); 
		public final ArrayList<String> descriptions2 = new ArrayList<String>();
		public final ArrayList<ApplicationInfo> packageInfos = new ArrayList<ApplicationInfo>();
		private boolean[] checked = new boolean[10];
		private LayoutInflater mInflater;
		
		public Adapter(final Context context){
			new Thread(new Runnable(){
				public void run(){
					mInflater = getLayoutInflater();
					final PackageManager packages = getPackageManager();
					List<ApplicationInfo> p = packages.getInstalledApplications(0);
					String name;
					final ArrayList<String> c = new ArrayList<String>();
					c.add("com.aldiko.android.samsung");		//setListAdapter(Adapter);
					c.add("com.sec.android.app.dlna");
					c.add("com.android.email");
					c.add("com.sec.android.app.myfiles");
					c.add("com.google.android.gm");
					c.add("com.layar");
					c.add("com.sec.android.app.memo");
					c.add("com.sec.android.app.minidiary");
					c.add("org.microemu.android.se.appello.lp.NFSNaviagtion");
					c.add("com.sec.android.app.calculator");
					c.add("com.sec.android.app.samsungapps");
					c.add("com.android.stk");
					c.add("com.sec.android.app.unifiedinbox");
					c.add("com.sec.android.app.voicerecorder");
					c.add("com.android.voicedialer");
					c.add("com.sec.android.app.clockpackage");
					c.add("com.sec.android.app.fm");
					c.add("com.palringo.android.utalk");
					c.add("com.sec.android.app.writeandgo");
					c.add("bs.app");
					c.add("com.android.providers.drm");
					c.add("com.sec.android.app.drmua");
					c.add("com.sec.android.app.controlpanel");
					c.add("com.tat.livewallpaper.bluesea");
					c.add("com.tat.livewallpaper.oceanwaves");
					c.add("com.tat.livewallpaper.dandelion");
					c.add("com.tat.livewallpaper.aurora");
					c.add("com.android.magicsmoke");
					c.add("com.sec.android.widgetapp.buddiesnow");
					c.add("com.sec.android.widgetapp.calendarclock");
					c.add("com.sec.android.widgetapp.infoalarm");
					c.add("com.sec.android.widgetapp.days");
					c.add("com.sec.android.widgetapp.dualclock");
					c.add("com.sec.android.wigdetapp.feedsandupdate");
					c.add("com.samsung.sec.android.appwidget.programmonitorwidget");
					c.add("com.sec.android.wigdetapp.stockclock");
					c.add("com.sec.android.widgetapp.weatherclock");
					c.add("com.monotype.android.font.cooljazz");
					c.add("com.monotype.android.font.rosemary");
					c.add("com.sec.android.app.screencapture");
					c.add("com.newspaperdirect.pressreader.android.samsung");
					int index;
					for(ApplicationInfo pi : p){
						index = 0;
						name = pi.packageName;
						for(String s: c){
							if(name.equals(s)){
								icons.add(packages.getApplicationIcon(pi));
								descriptions2.add((String) packages.getApplicationLabel(pi));
								descriptions.add(name);
								packageInfos.add(pi);
								c.remove(index);		//increase performance
								break; 				    //increase performance
							}
							index++;
						}
					}
					checked = new boolean[icons.size()];
					pg.dismiss();
					handler.post(new Runnable(){
						public void run(){
							notifyDataSetChanged();
						}
					});
				}
			}).start();
			pg.show();
		}
		public int getCount() {
			return descriptions.size();
		}

		public Object getItem(int arg0) {
			return arg0;
		}

		public long getItemId(int position) {
			return position;
		}
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.row, null);
				holder = new ViewHolder();
				holder.descriptionName = (TextView)convertView.findViewById(R.id.text);
				holder.descriptionPackage = (TextView)convertView.findViewById(R.id.text2);
				holder.icon = (ImageView)convertView.findViewById(R.id.icon);
				holder.checkbox = (CheckBox)convertView.findViewById(R.id.checkbox);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.descriptionName.setText(descriptions.get(position));
			holder.descriptionPackage.setText(descriptions2.get(position));
			holder.icon.setImageDrawable(icons.get(position));
			holder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				public void onCheckedChanged(CompoundButton b, boolean state){
					checked[position] = state;
				}
			});
			holder.checkbox.setChecked(checked[position]);
			return convertView;
		}
	}
    public boolean onKeyDown(int keycode, KeyEvent event){
    	if(keycode == KeyEvent.KEYCODE_BACK){
    		finish();
    		return true;
    	}
    	return false;
    }
}
