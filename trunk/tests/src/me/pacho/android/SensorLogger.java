package me.pacho.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

public class SensorLogger extends Activity implements SensorEventListener{

    private SensorManager mSensorManager;
    private File file;
    private OutputStreamWriter osw;
    private FileOutputStream fos;
    private Sensor mAccelerometer;
    private Sensor mGyro;
    private Sensor mGrav;
    private String archive_name=System.nanoTime()+"";
    private TextView tGrav;
    private TextView tAcc;
    private TextView tGyro;
    private TextView tNacc;
    private float[] lastGravity;
    private float[] lastAcceleration;
  
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        file = new File(Environment.getExternalStorageDirectory(),"data/SENSOR_"+archive_name+".txt"); 
        Log.w("ExternalStorage", Environment.getExternalStorageState());
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
        } catch (IOException e) {     	
            Log.e("ExternalStorage", "Error opening " + file, e);
        }
        tAcc=(TextView) findViewById(R.id.tAcc);
        tNacc=(TextView) findViewById(R.id.tNacc);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
       
        tGyro=(TextView) findViewById(R.id.tGyro);
        mGyro=mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_UI);
        
        tGrav=(TextView) findViewById(R.id.tGrav);
        mGrav=mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this, mGrav, SensorManager.SENSOR_DELAY_UI);

    }
    
    protected void onDestroy() {
		super.onDestroy();
    	try {
			osw.close();
			fos.close();
			Log.i("Destroy","wey");
	        mSensorManager.unregisterListener(this);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void onSensorChanged(SensorEvent event) {
    	String prefix="";


        String line=event.timestamp+"";
        for(int i=0;i<event.values.length;i++){
        	line+=","+event.values[i];
        }
        line+="\n";
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
        	prefix="ACC:";
        	lastAcceleration=event.values;
        	tAcc.setText(prefix+line);
        	calculateNetAcceleration(event.timestamp);
        	
        }
        else if(event.sensor.getType() ==Sensor.TYPE_GYROSCOPE){
        	prefix="GYR:";
        	tGyro.setText(prefix+line);
        }
        else if (event.sensor.getType()== Sensor.TYPE_GRAVITY){
        	prefix="GRA:";
        	lastGravity=event.values;
        	tGrav.setText(prefix+line);
        	calculateNetAcceleration(event.timestamp);
        }
        else return;
        line=prefix+line;
        try {
			fos.write(line.getBytes());
		} catch (IOException e) {			
            Log.e("ExternalStorage", "Error writing line" + file, e);
		}
		
    }

	private void calculateNetAcceleration(long timestamp) {
    	try{
		String line="NACC:"+timestamp+","+(lastAcceleration[0]-lastGravity[0])+","+
    									(lastAcceleration[1]-lastGravity[1])+","+
    									(lastAcceleration[2]-lastGravity[2])+"\n";
		tNacc.setText(line);
		fos.write(line.getBytes());

    	}
    	catch(NullPointerException e){
    		//Aun no hay lecturas.
    	} catch (IOException e) {
			// TODO Auto-generated catch block
            Log.e("ExternalStorage", "Error writing line" + file, e);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

}
