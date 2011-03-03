package me.pacho.android.PathDrawer2D;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;

public class Drawer extends Activity {
	private static Context CONTEXT;
	private static PathManager pathManager;
	private static PathDrawer pathDrawer;
	private static LoggerThread logger;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        CONTEXT=this;
    	logger=new LoggerThread("data/PathDrawer/", System.nanoTime()+"");
    	logger.run();

    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	pathManager=new PathManager();
    }
    
    public void onDestroy(){
    	super.onDestroy();
    	logger.closeLog();
    }
    public static PathManager getPathManager(){
    	return pathManager;
    }

    public static LoggerThread getLogger(){
    	return logger;
    }
    public static Context getContext() {
		return CONTEXT;
	}
    
    static void registerRawData(PathDrawer rd){
    	 pathDrawer = rd;
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		//4 back
		//82 menu
		//84 buscar
		//25 low vol
		//24 + vol
		if(pathDrawer!=null){
		if(keyCode==KeyEvent.KEYCODE_MENU){
			//TODO
		}
		else if(keyCode==KeyEvent.KEYCODE_BACK){
			pathDrawer.reset();
		}
		else if(keyCode==KeyEvent.KEYCODE_SEARCH){
			
		}
		}
		return true;
	}
}