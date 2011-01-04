package de.Fr4gg0r.SGS.Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

public class Gps extends AnimatedActivity implements View.OnClickListener{
	private static final String europe = "NTP_SERVER=europe.pool.ntp.org";
	private static final String usa = "NTP_SERVER=north-america.pool.ntp.org"; //stock
	private static final String europe_choice = "europe.pool.ntp.org";
	private static final String usa_choice = "north-america.pool.ntp.org (stock)";
	private Spinner s;
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.gps);
		s = (Spinner) findViewById(R.id.gps_s1);
		findViewById(R.id.gps_b1).setOnClickListener(this);
		s.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, new String[]{europe_choice, usa_choice}));
		try {
			final BufferedReader reader = new BufferedReader(new FileReader("/system/etc/gps.conf"));
			String line = null;
			while(reader.ready()){
				line = reader.readLine();
				if(line.startsWith("NTP_SERVER=")){
					Log.d("line", line);
					if(line.substring(line.indexOf('=')+1, line.length()).equals(europe_choice)){
						s.setSelection(0);
					}
					else s.setSelection(1);
					break;
				}
			}
			reader.close();
		} catch (Exception e) {
			finish();
			Toast.makeText(this, "error: "+e.toString(), 1).show();
			return;
		}
	}
	private void apply(){
		try {
			final StringBuilder sb = new StringBuilder();
			final BufferedReader reader = new BufferedReader(new FileReader("/system/etc/gps.conf"));
			String line = null;
			while(reader.ready()){
				line = reader.readLine();
				if(line.startsWith("NTP_SERVER=")){
					if(((String)s.getSelectedItem()).equals(europe_choice)){
						sb.append(europe).append('\n');
					}
					else sb.append(usa).append('\n');
					break;
				}
				sb.append(reader.readLine()).append('\n');
			}
			while(reader.ready()){
				sb.append(reader.readLine()).append('\n');
			}
			reader.close();
			final BufferedWriter writer = new BufferedWriter(new FileWriter("/sdcard/gps.conf"));
			writer.write(sb.toString());
			writer.close();
			final Process p = Runtime.getRuntime().exec("su");
			final DataOutputStream out = new DataOutputStream(p.getOutputStream());
			out.writeBytes("busybox mount -o remount,rw /system\n");
			out.writeBytes("busybox cp -f /sdcard/gps.conf /system/etc/\n");
			out.writeBytes("busybox rm -f /sdcard/gps.conf\n");
			out.close();
			finish();
			Toast.makeText(this, "GPS Ntp server set to:\n"+((String)s.getSelectedItem()), 1).show();
		} catch (Exception e) { //wyne
		}
	}
	public void onClick(View v){
		switch(v.getId()){
		case R.id.gps_b1:
			apply();
			break;
		}
	}
}
