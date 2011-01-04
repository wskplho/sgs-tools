package de.Fr4gg0r.SGS.Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.Toast;
import de.Fr4gg0r.SGS.Tools.MyMenu.MyMenuCallback;

public class marketFix extends AnimatedActivity{
	private MyMenu menu;
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.marketfix);
		menu = new MyMenu(this);
	       addContentView(menu, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	       menu.addSection(getString(R.string.menu), getResources().getDrawable(android.R.drawable.ic_menu_preferences));
	       menu.setCallback(new MyMenuCallback(){
	    	   public void menuItemSelected(int which){
	    		   //only 1 section so which always 0
	    		   finish();
	    	   }
	       });
		findViewById(R.id.marketfix_b1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					final BufferedReader reader = new BufferedReader(new FileReader("/system/build.prop"));
					final StringBuilder sb = new StringBuilder();
					while(reader.ready()){
						String line = reader.readLine();
						if(line.startsWith("ro.build.fingerprint")){
							sb.append("ro.build.fingerprint=samsung/GT-I9000/GT-I9000/GT-I9000:2.2/FROYO/XXJPM:user/release-keys").append("\n");
							continue;
						}
						sb.append(line).append("\n");
					}
					final BufferedWriter writer = new BufferedWriter(new FileWriter("/sdcard/build.prop"));
					writer.write(sb.toString());
					writer.flush();
					writer.close();
					final Process p = Runtime.getRuntime().exec("su");
					final DataOutputStream out = new DataOutputStream(p.getOutputStream());
					out.writeBytes("busybox cp -f /sdcard/build.prop /system/\n");
					out.writeBytes("rm /sdcard/build.prop\n");
					if(((CheckBox)findViewById(R.id.marketfix_c1)).isChecked()){
						out.writeBytes("busybox rm -rf /data/data/com.android.vending\n");
					}
					if(((CheckBox)findViewById(R.id.marketfix_c2)).isChecked()){
						out.writeBytes("reboot\n");
					}
					out.flush();
					out.close();	
					finish();
				} catch (Exception e) {
					Toast.makeText(marketFix.this, "Error: "+e.toString(), 1).show();
				}
				
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
}
