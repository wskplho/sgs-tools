package de.Fr4gg0r.SGS.Tools;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class App {
	private String apkPath;
	private String dataPath;
	protected Drawable icon;
	protected boolean isSystemApp;
	protected String appName;
	private String packageName;
	protected boolean isProtected;
	
	public App(final ApplicationInfo ai, final PackageManager pm){
		apkPath = ai.sourceDir;
		if(apkPath.startsWith("/data/app-private"))isProtected = true;
		else isProtected = false;
		dataPath = ai.dataDir;
		icon = pm.getApplicationIcon(ai);
		appName = (String) pm.getApplicationLabel(ai);
		isSystemApp = ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
		//isSystemApp = ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) && !((ai.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
	}
	
	public final String getApkPath(){
		return this.apkPath;
	}
	public final String getDataPath(){
		return this.dataPath;
	}
	public final String getApkName(){
		return apkPath.substring(apkPath.lastIndexOf('/')+1, apkPath.length());
	}
	public final String getPackageName(){
		if(packageName == null) packageName = dataPath.substring(dataPath.lastIndexOf('/')+1, dataPath.length());
		return packageName;
	}
}
