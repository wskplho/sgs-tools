package de.Fr4gg0r.SGS.Tools;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import de.Fr4gg0r.SGS.Tools.MyMenu.MyMenuCallback;

public class ScriptReceiver extends AnimatedListActivity{
	private MyMenu menu;
	//must be global..
	private String output = "";
	private boolean done;
	private Runnable update;
	private ArrayList<String> usedFromZip;
	private boolean remove;
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.scriptreceivermain);
		menu = new MyMenu(this);
		File file = new File("/sdcard/sgstools");
		if(!file.exists()){
			//create dir if not already exits
			file.mkdir();
		}
		final ArrayList<String> scriptz = new ArrayList<String>();
		file.listFiles(new FileFilter(){
			public boolean accept(File file){
				String name = file.getAbsolutePath();
	    		if(name.endsWith(".txt")){
	    			scriptz.add(name.substring(17, name.length()));
	    		}
	    		if(file.getName().endsWith(".zip"))scriptz.add(file.getAbsolutePath());
	    		return false;
			}
		});
		final String[] data = scriptz.toArray(new String[0]);		
		setListAdapter(new ArrayAdapter<String>(this, R.layout.simpletextview, data));
    	menu = new MyMenu(this);   
		addContentView(menu, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	       menu.addSection(getString(R.string.menu), getResources().getDrawable(android.R.drawable.ic_menu_preferences));
	       menu.setCallback(new MyMenuCallback(){
	    	   public void menuItemSelected(int which){
	    		   //only 1 section so which always 0
	    		   finish();
	    	   }
	       });
	       getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				final String name = ((TextView)view).getText().toString();
				AlertDialog.Builder builder = new AlertDialog.Builder(ScriptReceiver.this);
				builder.setTitle("Delete File?");
				builder.setMessage("Do you want to delete "+name);
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(name.endsWith(".zip")){
							final File file = new File(name);
							file.delete();
						}
						else{
							final File file = new File("/sdcard/sgstools/"+name);
							file.delete();
						}
						//restart activity now!
						startActivity(ScriptReceiver.this.getIntent());
						finish();
					}
				});
				builder.setNegativeButton("No", new DialogInterface.OnClickListener() {	
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				builder.create().show();
				return true;
				}
	       });
	       getListView().setOnItemClickListener(new OnItemClickListener() {
	         public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {	
	        	final String name = ((TextView)view).getText().toString();
	        	if(name.endsWith(".q.txt")){
	        		//quick scripts, don't ask if we really want to execute them
	        		runTxtScript("/sdcard/sgstools/"+name);
	        		return;
	        	}
	        	AlertDialog.Builder al = new AlertDialog.Builder(ScriptReceiver.this);
	    		al.setTitle("Execute Script");
	    		al.setIcon(android.R.drawable.ic_dialog_alert);
	    		al.setMessage("Do you really want to execute the following script:\n"+name);
	    		al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    				if(name.endsWith(".txt"))runTxtScript("/sdcard/sgstools/"+name);
	    				else runZipScript(name);
	    			}
	    		});
	    		al.setNegativeButton("No", new DialogInterface.OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    				dialog.dismiss();
	    			}
	    		});
	    		//I always forget this.. -.-
	    		al.create().show();
	         	}
	       });
    }
	private void runZipScript(final String path){
		new Thread(){
			public void run(){
				usedFromZip = new ArrayList<String>();
				remove = true;
				//first unzip the zip archieve.. this method was simply copied from a java tutorial
			    Enumeration entries;
			    ZipFile zipFile;
			    try {
					zipFile = new ZipFile(path);
					entries = zipFile.entries();
					while(entries.hasMoreElements()){
						ZipEntry entry = (ZipEntry) entries.nextElement();
						usedFromZip.add(entry.getName());
						File f = new File("/sdcard/sgstools/"+entry.getName());
						if(f.exists())f.delete();
						copyInputStream(zipFile.getInputStream(entry), new BufferedOutputStream(new FileOutputStream("/sdcard/sgstools/"+entry.getName())));
					}
					zipFile.close();
			    } catch (IOException e) { }
			    final ArrayList<String> paths = new ArrayList<String>();
			    for(String s: usedFromZip){
					if(s.endsWith(".txt")){
						paths.add(s);
					}
				}
				if(paths.size() > 0){
					if(paths.size() > 1){
						runOnUiThread(new Runnable(){ //runTxtScript will start a new thread
							public void run(){
								final String[] data = paths.toArray(new String[0]);
								AlertDialog.Builder builder = new AlertDialog.Builder(ScriptReceiver.this);
								builder.setAdapter(new ArrayAdapter<String>(ScriptReceiver.this, R.layout.completionrow, data), new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										runTxtScript("/sdcard/sgstools/"+paths.get(which));
									}
								});
								AlertDialog al = builder.create();
								al.setOnCancelListener(new OnCancelListener(){
									public void onCancel(DialogInterface d){
										for(String s: usedFromZip){
											final File f = new File("/sdcard/sgstools/"+s);
											f.delete();
										}
									}
								});
								al.show();
							}
						});
					}
					else{
						runOnUiThread(new Runnable(){ //runTxtScript will start a new thread
							public void run(){
								runTxtScript("/sdcard/sgstools/"+paths.get(0)); 
							}
						});
					}
				}
				else{
					runOnUiThread(new Runnable(){
						public void run() {
							Toast.makeText(ScriptReceiver.this, "The zip does not contain a valid script file", 1).show();								
						}
					});
				}	
			}
		}.start();
	}
	private void runTxtScript(final String path){
		//bugfix: reset output before each script
		output = "";
		//do this in a new Thread to put some eyecandy with progressbar and status updates for user
		final ProgressDialog pd = new ProgressDialog(ScriptReceiver.this);
		pd.setCancelable(false);
		//a nice Handler for handling runnables... mainly for ui updates from new thread
		final Handler handler = new Handler();
		//we use this for updating the text in progressdialog
		final Runnable updateText = new Runnable(){
			public void run(){
				pd.setMessage(output);
			}
		};
		new Thread(){
			public void run(){
				done = false;
				//first read all commands in and save them in a string
				StringBuilder sb = null;
				try{
					BufferedReader in = new BufferedReader(new FileReader(path));
					sb = new StringBuilder();
					while(in.ready()){
						sb.append(in.readLine());
						sb.append("\n");
					}
				}
				catch(Exception e){	}
				final String commands = sb.toString();					
				try {
					final Process p = Runtime.getRuntime().exec("su");
					final DataOutputStream out = new DataOutputStream(p.getOutputStream());
					final BufferedReader inReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
					final BufferedReader errReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					update = new Runnable(){
						public void run(){
							//check for new output from inputstream or errorstream
							try{
								//this gets annoying..
								while(inReader.ready()){
									output += inReader.readLine()+"\n";
									handler.post(updateText);
								}
								while(errReader.ready()){
									output += errReader.readLine()+"\n";
									handler.post(updateText);
								}										
							}
							catch(Exception e){}
							if(!done){
								//we will come back in 100ms and check again for output/errors
								handler.postDelayed(update, 100);
							}
						}
					};
					handler.post(update);
					//for convenience we mount /system rw before executing and remount it ro when script is done
					out.writeBytes("busybox mount -o remount,rw /system\n");
					out.writeBytes(commands);
					out.close();
					p.waitFor();
					//update output that is still waiting in inputstreams
					while(inReader.ready()){
						output += inReader.readLine()+"\n";
						handler.post(updateText);
					}
					while(errReader.ready()){
						output += errReader.readLine()+"\n";
						handler.post(updateText);
					}
				} catch (Exception e) {	}
				done = true;
				handler.removeCallbacks(update);						
				//status update: done!
				pd.setCancelable(true);
				output += "\nAll Done! Press back key now";
				handler.post(updateText);
				//remove unziped files
				if(remove){
					for(String s: usedFromZip){
						final File f = new File("/sdcard/sgstools/"+s);
						f.delete();
					}
				}
				//end run method of new thread
			}
		}.start();
		pd.show();
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
    //copied from java unzip tutorial
    public static final void copyInputStream(InputStream in, OutputStream out)
    throws IOException
    {
      byte[] buffer = new byte[1024];
      int len;

      while((len = in.read(buffer)) >= 0)
        out.write(buffer, 0, len);

      in.close();
      out.close();
    }
}
