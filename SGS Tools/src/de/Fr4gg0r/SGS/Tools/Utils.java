package de.Fr4gg0r.SGS.Tools;

import java.io.DataOutputStream;
import java.io.File;

public class Utils {
	static final boolean checkBusyBox(){
		return new File("/system/xbin/busybox").exists() || new File("/system/bin/busybox").exists() || new File("/sbin/busybox").exists();
	}
	static final boolean checkRoot(){
		return new File("/system/xbin/su").exists() || new File("/system/bin/su").exists() || new File("/sbin/su").exists();
	}
	
	static final void getReadAccess(String path){
		try{
			final Process p = Runtime.getRuntime().exec("su");
			final DataOutputStream out = new DataOutputStream(p.getOutputStream());
			out.writeBytes("busybox mount -o remount,rw /system\n");
			out.writeBytes("chmod 664 "+path);
			out.writeBytes("busybox mount -o remount,ro /system\n");
			out.flush();
			out.close();
			p.waitFor();
			p.destroy();
		}
		catch(Exception e){}
	}
	static final void mountSystemRW(){
		try{
			final Process p = Runtime.getRuntime().exec("su");
			final DataOutputStream out = new DataOutputStream(p.getOutputStream());
			out.writeBytes("busybox mount -o remount,rw /system\n");
			out.flush();
			out.close();
			p.waitFor();
			p.destroy();
		}
		catch(Exception e){}
	}
	static final void mountSystemRO(){
		try{
			final Process p = Runtime.getRuntime().exec("su");
			final DataOutputStream out = new DataOutputStream(p.getOutputStream());
			out.writeBytes("busybox mount -o remount,ro /system\n");
			out.flush();
			out.close();
			p.waitFor();
			p.destroy();
		}
		catch(Exception e){}
	}
	static class AnimationHolder{
		static int oldWidth;
		static int oldHeight;
	}
}
