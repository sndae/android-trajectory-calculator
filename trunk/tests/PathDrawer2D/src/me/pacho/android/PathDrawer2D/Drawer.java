package me.pacho.android.PathDrawer2D;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

public class Drawer extends Activity {
	private static Context CONTEXT;
	private static SensorGuy sensorGuy;
	private static RawData rawData;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        CONTEXT=this;
    }
    
    @Override
    public void onResume(){
    	super.onResume();
        sensorGuy=new SensorGuy();
    }
    public static SensorGuy getSensorGuy(){
    	return sensorGuy;
    }

    
    public static Context getContext() {
		return CONTEXT;
	}
    
    static void registerRawData(RawData rd){
    	 rawData = rd;
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		//4 back
		//82 menu
		//84 buscar
		//25 low vol
		//24 + vol
		if(rawData!=null){
		if(keyCode==KeyEvent.KEYCODE_MENU){
			//TODO
		}
		else if(keyCode==KeyEvent.KEYCODE_BACK){
			rawData.reset();
		}
		else if(keyCode==KeyEvent.KEYCODE_SEARCH){
			
		}
		}
		return true;
	}
}