package de.Fr4gg0r.SGS.Tools;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

public class AnimatedActivity extends Activity{ //nice turn animation
    public final void onRestoreInstanceState(Bundle saved){
    	super.onRestoreInstanceState(saved);
    	if(Utils.AnimationHolder.oldHeight > Utils.AnimationHolder.oldWidth)
    		getWindow().getDecorView().findViewById(android.R.id.content).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim1));
    	else getWindow().getDecorView().findViewById(android.R.id.content).startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim2));
    }
    public final void onSaveInstanceState(Bundle save){
    	super.onSaveInstanceState(save);
    	final View v = getWindow().getDecorView().findViewById(android.R.id.content);
    	Utils.AnimationHolder.oldWidth = v.getWidth();
    	Utils.AnimationHolder.oldHeight = v.getHeight();
    }
}
