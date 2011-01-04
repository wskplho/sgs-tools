package de.Fr4gg0r.SGS.Tools;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class Miscellanous extends AnimatedActivity{
	
	public void onCreate(Bundle b){
		super.onCreate(b);
		setContentView(R.layout.misc);
	}
	public void setBrightness(View v){
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = 0.01f;
		getWindow().setAttributes(lp);
	}
}
