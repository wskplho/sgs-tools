package de.Fr4gg0r.SGS.Tools;

import java.io.DataOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ExecuteShortcut extends Activity{ //animation senseless
	public void onCreate(Bundle b){
		super.onCreate(b);
		String commands = getIntent().getStringExtra("commands");
		Log.d("commands", commands);
		try {
			Process p = null;
			if(getIntent().getBooleanExtra("su", false)){
				p = Runtime.getRuntime().exec("su");
				Log.d("su", "true");
			}
			else{
				p = Runtime.getRuntime().exec("sh");
			}
			DataOutputStream out = new DataOutputStream(p.getOutputStream());
			out.writeBytes(commands);
			out.flush();
			out.close();
			p.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Toast.makeText(this, commands+"\nexecuted", 0).show();
		finish();
	}
}
