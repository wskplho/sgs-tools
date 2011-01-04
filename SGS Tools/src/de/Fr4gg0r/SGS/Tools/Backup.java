package de.Fr4gg0r.SGS.Tools;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Backup extends AnimatedActivity{
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.backup);
		Button b1 = (Button) findViewById(R.id.backup_b1);
		b1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backupNow(null);
			}
		});
		Button b2 = (Button) findViewById(R.id.backup_b2);
		b2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				manageBackups();
			}
		});
		Button b3 = (Button) findViewById(R.id.backup_b3);
		b3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				restore(null);
			}
		});
	}
	private void manageBackups(){
		File f = new File("/sdcard/sgstools");
		final ArrayList<String> backups = new ArrayList<String>();
		f.listFiles(new FileFilter(){
			public boolean accept(File f){
				if(f.isDirectory() && f.getName().endsWith("_twBackup"))backups.add(f.getName().substring(0, f.getName().length()-9));
				return false;
			}
		});
		if(backups.size() > 0){
			AlertDialog.Builder builder = new AlertDialog.Builder(Backup.this);
			ArrayAdapter<String> ar = new ArrayAdapter<String>(Backup.this, R.layout.completionrow, backups);
			builder.setAdapter(ar, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, final int which) {
					AlertDialog.Builder b = new AlertDialog.Builder(Backup.this);
					b.setTitle("Delete Backup?");
					b.setMessage("Do you want to delete the backup: "+backups.get(which)+" ?");
					b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int w) {
							if(!deleteRF("/sdcard/sgstools/"+backups.get(which)+"_twBackup")){
								Toast.makeText(Backup.this, "Could not delete backup", 0).show();
							}
						}
					});
					b.setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					b.show();
				}
			});
			AlertDialog al = builder.create();
			registerForContextMenu(al.getListView());
			al.show();
		}
		else{
			Toast.makeText(this, "No backup found", 0).show();
		}
	}
	private boolean deleteRF(String path){
		final File f = new File(path);
		if(!f.isDirectory()){
			return f.delete();
		}
		else{
			f.listFiles(new FileFilter(){
				public boolean accept(File f){
					f.delete(); //dirty but useful :D
					return true;
				}
			});
			return f.delete();
		}
	}
	private void backupNow(View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText e = new EditText(this);
		builder.setView(e);
		builder.setTitle("Choose a name");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					final String name = e.getText().toString();
					final File f = new File("/sdcard/sgstools/"+name+"_twBackup");
					if(f.mkdir()){
						final Process p = Runtime.getRuntime().exec("su");
						final DataOutputStream out = new DataOutputStream(p.getOutputStream());
						out.writeBytes("busybox mount -o remount,rw /system\n");
						out.writeBytes("busybox cp -f /dbdata/databases/com.sec.android.app.twlauncher/launcher.db /sdcard/sgstools/"+name+"_twBackup/\n");
						out.writeBytes("busybox cp -f /dbdata/databases/com.sec.android.app.twlauncher/shared_prefs/launcher.xml /sdcard/sgstools/"+name+"_twBackup/\n");
						out.writeBytes("busybox cp -f /data/data/com.sec.android.app.twlauncher/files/launcher.preferences /sdcard/sgstools/"+name+"_twBackup/\n");
						out.flush();
						out.close();
						Toast.makeText(Backup.this, "done\nBackup: "+e.getText().toString(), 0).show();
					}
					else{
						Toast.makeText(Backup.this, "Error, name already used?", 0).show();
					}
				} catch (Exception e) { }
			}
		});
		builder.show();
	}
	public void restore(View v){
		File f = new File("/sdcard/sgstools");
		final ArrayList<String> backups = new ArrayList<String>();
		f.listFiles(new FileFilter(){
			public boolean accept(File f){
				if(f.isDirectory() && f.getName().endsWith("_twBackup"))backups.add(f.getName().substring(0, f.getName().length()-9));
				return false;
			}
		});
		if(backups.size() > 0){
			backups.add(0, "Make sure you have reinstalled all apps!!");
			AlertDialog.Builder builder = new AlertDialog.Builder(Backup.this);
			ArrayAdapter<String> ar = new ArrayAdapter<String>(Backup.this, R.layout.completionrow, backups);
			builder.setAdapter(ar, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					if(which > 0){
						try {
							final String dir = "/sdcard/sgstools/"+backups.get(which)+"_twBackup/";
							final int uid = getPackageManager().getApplicationInfo("com.sec.android.app.twlauncher", 0).uid;
							final Process p = Runtime.getRuntime().exec("su");
							final DataOutputStream out = new DataOutputStream(p.getOutputStream());
							
							out.writeBytes("busybox cp -f "+dir+"launcher.db"+" /dbdata/databases/com.sec.android.app.twlauncher/launcher.db\n");
							out.writeBytes("chown "+uid+" /dbdata/databases/com.sec.android.app.twlauncher/launcher.db\n");
							
							out.writeBytes("busybox cp -f "+dir+"launcher.xml"+" /dbdata/databases/com.sec.android.app.twlauncher/shared_prefs/launcher.xml\n");
							out.writeBytes("chown "+uid+" /dbdata/databases/com.sec.android.app.twlauncher/shared_prefs/launcher.xml\n");
							
							out.writeBytes("busybox cp -f "+dir+"launcher.preferences"+" /data/data/com.sec.android.app.twlauncher/files/launcher.preferences\n");
							out.writeBytes("chown "+uid+" /data/data/com.sec.android.app.twlauncher/files/launcher.preferences\n");
							
							out.writeBytes("reboot\n");
							out.flush();
							out.close();
							Toast.makeText(Backup.this, "Please wait...", 1).show();
						} catch (Exception e) { }
					}
				}
			});
			builder.show();
		}
		else{
			Toast.makeText(this, "No backup found", 0).show();
		}
	}
}
