package me.pacho.android.PathDrawer2D;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class LoggerThread extends Thread{
	
	
	private File file;
	private FileOutputStream fos;
	private boolean running;
	
	public LoggerThread(String directory, String archive_name){
		//
        File path=new File(Environment.getExternalStorageDirectory(),directory);
        try{
        	if(!path.exists())path.mkdirs();
        	if(!path.exists()) Log.e("LoggerThread", "Unable to create directory: "+path.getAbsolutePath());
        	else{
                file = new File(path.getAbsolutePath()+"/"+archive_name+".log"); 
                file.createNewFile();
                fos = new FileOutputStream(file);
        	}
        }
        catch(SecurityException e){
        	Log.e("LoggerThread", "Security Exception, unable to access external storage. Check the android manifest for the adecuate permissions.");
			e.printStackTrace();

        } catch (IOException e) {
        	Log.e("LoggerThread", "IOException, unable to create file:"+file.getAbsolutePath()+"/"+file.getName());
			e.printStackTrace();
		}
	}
	
	public void closeLog(){
		try {
			fos.close();
			running=false;
		} catch (IOException e) {
        	Log.e("LoggerThread", "IOException, unable to close file:"+file.getAbsolutePath()+"/"+file.getName());
			e.printStackTrace();
		}
	}
	
	public void log(String info){
		if(running){
			info+="\n";
			try {
				fos.write(info.getBytes());
			} catch (IOException e) {
	        	Log.e("LoggerThread", "IOException, unable to write to file:"+file.getAbsolutePath()+"/"+file.getName());
				e.printStackTrace();
			}
		}
	}
	
	public void run(){
		running=true;
	}
	
}
