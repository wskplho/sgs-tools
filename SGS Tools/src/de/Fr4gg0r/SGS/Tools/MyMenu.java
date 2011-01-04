package de.Fr4gg0r.SGS.Tools;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

public class MyMenu extends View {
	public interface MyMenuCallback {
		public void menuItemSelected(int which);
	}
	
	private final Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final Paint rectPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final RectF rect = new RectF();
	private final RectF rect2 = new RectF();
	private final Paint screw = new Paint(Paint.ANTI_ALIAS_FLAG);
	private final int transparent = Color.argb(0, 0, 0, 0);
	private boolean draw;
	private int width;
	private int height;
	private int translationX;
	private boolean touched;
	private final int selected = Color.parseColor("#00BFFF");
	private final int not_selected = Color.BLACK;
	private int selectedItemId;
	private int itemCount; 
	private final ArrayList<String>descriptions = new ArrayList<String>();
	private final ArrayList<Drawable>icons = new ArrayList<Drawable>();
	private float scaleX = 1;
	private float scaleY = 1;
	private MyMenuCallback callback;
	private boolean flyOut;
	public MyMenu(Context context) {
		super(context);
		rectPaint.setColor(Color.parseColor("#104E8B"));
		rectPaint.setStyle(Paint.Style.STROKE);
		rectPaint.setStrokeWidth(3);
		rectPaint2.setColor(not_selected);
		rectPaint2.setAlpha(190);
		screw.setColor(Color.GRAY);
		screw.setTextSize(23);
		screw.setFakeBoldText(true);
	}
	public boolean isShown(){
		return draw;
	}
	public final void setCallback(MyMenuCallback callback){
		this.callback = callback;
	}
	public final boolean onTouchEvent(MotionEvent event){
		final int y = (int) event.getY();
		switch(itemCount){
		case 1:
			if(!draw || y < height - 90){
				if(!touched){
					dismiss();
					return false;
				}
			}
			if(y > height - 90 && event.getAction() == MotionEvent.ACTION_DOWN){
				touched = true;
				//selectedItemId = 0; //not necessary
				invalidate();
				return true;
			}
			if(event.getAction() == MotionEvent.ACTION_UP){
				if(touched && y > height-90 && !flyOut){
					touched = false;
					invalidate();
					callback.menuItemSelected(0); //Always 0!
					dismiss();
					return true;
				}
				else{
					if(touched && y < height-90){
						touched = false;
						invalidate();
						return true;
					}
				}
			}
			return true;
		case 2:
			final int x = (int) event.getX();
			if(!draw || y < height - 90){
				if(!touched){
					dismiss();
					return false;
				}
			}
			if(y > height - 90 && event.getAction() == MotionEvent.ACTION_DOWN){
				touched = true;
				selectedItemId = x > width/2 ? 1 : 0;
				invalidate();
				return true;
			}
			if(event.getAction() == MotionEvent.ACTION_UP){
				if(touched && y > height-90 && !flyOut){
					touched = false;
					if(selectedItemId == 0 && x < width/2){
						invalidate();
						callback.menuItemSelected(0);  //always 0!
						dismiss();
						return true;
					}
					if(selectedItemId == 1 && x > width/2){
						invalidate();
						callback.menuItemSelected(1); //always 1!
						dismiss();
						return true;
					}
					invalidate();
				}
				else{
					if(touched && y < height-90){
						touched = false;
						invalidate();
						return true;
					}
				}
			}
			return true;
		}
		return true;
	}
	public final void onDraw(Canvas c){
		c.drawColor(transparent);
		c.scale(scaleX, scaleY);
		if(draw){
			switch(itemCount){
			case 2:
				c.translate(translationX, 0);
				c.drawRoundRect(rect2, 10, 10, rectPaint2);
				if(touched){
					rectPaint2.setColor(selected);
					rectPaint2.setAlpha(190);
					switch(selectedItemId){
					case 0: 
						rect2.set(3, height-90, width/2-3, height-3);
						c.drawRoundRect(rect2, 10, 10, rectPaint2);
						break;
					case 1:
						rect2.set(width/2+3, height-90, width-3, height-3);
						c.drawRoundRect(rect2, 10, 10, rectPaint2);
						break;
					}
					rect2.set(3, height-90, width-3, height-3);
					rectPaint2.setColor(not_selected);
					rectPaint2.setAlpha(190);
				} 
				c.drawRoundRect(rect, 10, 10, rectPaint);
				screw.setColor(Color.GRAY);
				c.drawCircle(rect2.left+10, rect2.top+10, 4, screw);
				c.drawCircle(rect2.left+10, rect2.bottom-10, 4, screw);
				c.drawCircle(rect2.right-10, rect2.top+10, 4, screw);
				c.drawCircle(rect2.right-10, rect2.bottom-10, 4, screw);
				screw.setColor(Color.LTGRAY);
				c.drawCircle(rect2.left+10, rect2.top+10, 2, screw);
				c.drawCircle(rect2.left+10, rect2.bottom-10, 2, screw);
				c.drawCircle(rect2.right-10, rect2.top+10, 2, screw);
				c.drawCircle(rect2.right-10, rect2.bottom-10, 2, screw);
				c.drawText(descriptions.get(1), width-descriptions.get(1).length()*13, height-50, screw);
				if(icons.get(0)!=null)icons.get(0).draw(c); 
				c.drawText(descriptions.get(0), width/2-descriptions.get(0).length()*40, height-50, screw);
				if(icons.get(1)!=null)icons.get(1).draw(c); 
				return;
			
			case 1:
				c.translate(translationX, 0);
				if(touched){
					rectPaint2.setColor(selected);
					rectPaint2.setAlpha(190);
					c.drawRoundRect(rect2, 10, 10, rectPaint2);
					rectPaint2.setColor(not_selected);
					rectPaint2.setAlpha(190);
				}
				else c.drawRoundRect(rect2, 10, 10, rectPaint2);
				c.drawRoundRect(rect, 10, 10, rectPaint);
				screw.setColor(Color.GRAY);
				c.drawCircle(rect2.left+10, rect2.top+10, 4, screw);
				c.drawCircle(rect2.left+10, rect2.bottom-10, 4, screw);
				c.drawCircle(rect2.right-10, rect2.top+10, 4, screw);
				c.drawCircle(rect2.right-10, rect2.bottom-10, 4, screw);
				screw.setColor(Color.LTGRAY);
				c.drawCircle(rect2.left+10, rect2.top+10, 2, screw);
				c.drawCircle(rect2.left+10, rect2.bottom-10, 2, screw);
				c.drawCircle(rect2.right-10, rect2.top+10, 2, screw);
				c.drawCircle(rect2.right-10, rect2.bottom-10, 2, screw);
				c.drawText(descriptions.get(0), width/2-descriptions.get(0).length()*12, height-40, screw);
				if(icons.get(0)!=null)icons.get(0).draw(c); 
				return;
			}
		}
	}
	public final void onSizeChanged(int width, int height, int oldw, int oldh){
		rect.set(3, height-90, width-3, height-3);
		rect2.set(3, height-90, width-3, height-3);
		switch(itemCount){
		case 1:
			icons.get(0).setBounds(width/2+15, height-65, width/2+55, height-25);
			break;
		case 2:
			icons.get(1).setBounds(width/2+descriptions.get(0).length()*15, height-50, 40+width/2+descriptions.get(0).length()*15, height-10);
			icons.get(0).setBounds(width/2-descriptions.get(0).length()*25-40, height-50, width/2-descriptions.get(0).length()*25, height-10); //adjust bounds 
			break;
		}
		this.width = width;
		this.height = height;
	}
	private final void flyIn(){
		new Thread(){
			public void run(){
				Random rr = new Random();
				try{
				switch(rr.nextInt(2)){
				case 0:
					translationX = width;
					while(translationX > -50){ //overbounce
						translationX -= 4;
						postInvalidate();
						Thread.sleep(2);
					}
					while(translationX < 0){ //bounce back
						translationX += 2;
						postInvalidate();
						Thread.sleep(2);
					}
					break;
				case 1:
					translationX = -width;
					while(translationX < 50){ //overbounce
						translationX += 4;
						postInvalidate();
						Thread.sleep(2);
					}
					while(translationX > 0){ //bounce back
						translationX -= 2;
						postInvalidate();
						Thread.sleep(2);
					}
					break;
				}
				}
				catch(Exception e){}
			}
		}.start();
	}
	private final void show(){
		draw = true;
		invalidate();
	}
	public final void dismiss(){
		new Thread(){
			public void run(){
				flyOut=true;
				while(scaleX >0.5){
					scaleX-=0.02f;
					scaleY-=0.02f;
					postInvalidate();
					try {
						Thread.sleep(24);
					} catch (InterruptedException e) {	}
				}
				scaleX = 1;
				scaleY = 1;
				draw = false;
				touched = false;
				postInvalidate();
				flyOut=false;
			}
		}.start();
	}
	public final void showOrDismiss(){
		if(draw)dismiss();
		else{
			draw = true;
			flyIn();
		}
	}
	public final void addSection(String text, Drawable icon){
		itemCount++;
		descriptions.add(text);
		icons.add(icon);
	}
}
