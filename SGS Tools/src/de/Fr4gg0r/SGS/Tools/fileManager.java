package de.Fr4gg0r.SGS.Tools;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import de.Fr4gg0r.SGS.Tools.MyMenu.MyMenuCallback;

public class fileManager extends AnimatedListActivity{
	private MyMenu menu;
	
	public void onCreate(Bundle b){
		super.onCreate(b);
		setListAdapter(new Adapter(this));
		menu = new MyMenu(this);
	       addContentView(menu, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
	       menu.addSection("Menu", getResources().getDrawable(android.R.drawable.ic_menu_preferences));
	       menu.setCallback(new MyMenuCallback(){
	    	   public void menuItemSelected(int which){
	    		   //only 1 item so which always 0.
	    		   finish();
	    	   }
	       });
	       getListView().setOnItemClickListener(new OnItemClickListener(){
				public void onItemClick(AdapterView<?> parent, View view, int position, long id){
					Intent intent = new Intent();
					intent.setClass(fileManager.this, propEditor.class);
					intent.putExtra("path", (String)parent.getAdapter().getItem(position));
					startActivity(intent);
					finish();
				}
			});
	}
	private class Adapter extends BaseAdapter{
		private LayoutInflater mInflater;
		private final String[] files = new String[]{"/default.prop", "/system/default.prop", "/system/build.prop"};
		private final Drawable[] icons = new Drawable[files.length];
		public Adapter(Context context){
			mInflater = LayoutInflater.from(context);
			final Resources res = getResources();
			icons[0] = res.getDrawable(android.R.drawable.ic_menu_edit);
			icons[1] = icons[0];
			icons[2] = icons[1];
		}
		public int getCount() {
			// TODO Auto-generated method stub
			return files.length;
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return files[position];
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.menuactivity, null);
				holder = new ViewHolder();
				holder.activity = (TextView)convertView.findViewById(R.id.menu_row_text);
				holder.icon = (ImageView)convertView.findViewById(R.id.menu_row_icon);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.activity.setText(files[position]);
			holder.icon.setImageDrawable(icons[position]);
			return convertView;
		}
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
	}
    static class ViewHolder {
        TextView activity;
        ImageView icon;
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
