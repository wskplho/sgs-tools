package de.Fr4gg0r.SGS.Tools;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import de.Fr4gg0r.SGS.Tools.MyMenu.MyMenuCallback;

public class Info extends AnimatedListActivity {
	private MyMenu menu;
	
    public void onCreate(Bundle savedInstanceState) {
       requestWindowFeature(Window.FEATURE_NO_TITLE);
       menu = new MyMenu(this);
       super.onCreate(savedInstanceState);
       setListAdapter(new Adapter(this));
       addContentView(menu, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
       menu.addSection(getString(R.string.menu), getResources().getDrawable(android.R.drawable.ic_menu_preferences));
       menu.setCallback(new MyMenuCallback(){
    	   public void menuItemSelected(int which){
    		   //only 1 section so which always 0
    		   finish();
    	   }
       });
       getListView().setOnItemClickListener(new OnItemClickListener() {
         public void onItemClick(AdapterView<?> parent, View view,
             final int position, long id) {
        	 switch(position){
        	 case 14:
        		 Intent i = new Intent(Intent.ACTION_MAIN, null);
        		 i.setComponent(new ComponentName("com.sec.android.app.lbstestmode", "com.sec.android.app.lbstestmode.LbsTestMode"));
        		 startActivity(i);
        		 break;
        	 case 24: 
        	 AlertDialog.Builder builder = new AlertDialog.Builder(Info.this);
        	 builder.setTitle(getString(R.string.attention));
        	 builder.setMessage(getString(R.string.hardresetWarning));
        	 builder.setPositiveButton("OK", new OnClickListener(){
        		 public void onClick(DialogInterface dialog, int id){
        		 startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(numbers[position]))));
        		 }
        	 });
        	 builder.setNegativeButton("Cancel", new OnClickListener(){
        		 public void onClick(DialogInterface dialog, int id){
        			 dialog.dismiss();
        		 }
        	 });
        	 AlertDialog alert = builder.create();
        	 alert.show(); 
        	 break;
        	 case 25:
        		 AlertDialog.Builder builder2 = new AlertDialog.Builder(Info.this);
            	 builder2.setTitle(R.string.attention);
            	 builder2.setMessage(getString(R.string.factoryresetWarning));
            	 builder2.setPositiveButton("OK", new OnClickListener(){
            		 public void onClick(DialogInterface dialog, int id){
            		 startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(numbers[position]))));
            		 }
            	 });
            	 builder2.setNegativeButton("Cancel", new OnClickListener(){
            		 public void onClick(DialogInterface dialog, int id){
            			 dialog.dismiss();
            		 }
            	 });
            	 AlertDialog alert2 = builder2.create();
            	 alert2.show(); 
            	 break;
            default: startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Uri.encode(numbers[position])))); break;
        	 }
         }
       });
    }
    public static final String[] numbers = new String[]{"*#06#", "*2767*4387264636#", "*#0*#", "*#*#4636#*#*", "*#0782#", "*#0011#", "*#1234#", "*#9090#", "*#*#197328640#*#*", "*#7465625#", "*#2263#", "*#232337#", "*#*#232331#*#*", 
    	"*#*#232338#*#*", "*#*#1472365#*#*", "*#*#2663#*#*", "*#*#2664#*#*", "*#*#0588#*#*", "*#0842#", "*#0228#", "*#0673#", "*#*#8255#*#*", "*#*#7594#*#*","*#9900#", "*2767*3855#", "*#*#7780#*#*",
    	"*#7284#", "*#07#", "*#03#", "*#32489#", "*#0589#", "*#272886#", "*#301279#"};
    private class Adapter extends BaseAdapter{
		private LayoutInflater mInflater;
		private final String[] descriptions = getResources().getStringArray(R.array.descriptions); 
    	public Adapter(Context context){
    		mInflater = LayoutInflater.from(context);
    	}
		public int getCount() {
			return descriptions.length;
		}

		public Object getItem(int arg0) {
			return arg0;
		}

		public long getItemId(int arg0) {
			return arg0;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.inforow, null);
				holder = new ViewHolder();
				holder.description = (TextView)convertView.findViewById(R.id.inforow_description);
				holder.number = (TextView)convertView.findViewById(R.id.inforow_number);
				convertView.setTag(holder);
			}
			else{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.description.setText(descriptions[position]);
			holder.number.setText(numbers[position]);
			return convertView;
		}
    }
    static class ViewHolder {
        TextView description;
        TextView number;
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