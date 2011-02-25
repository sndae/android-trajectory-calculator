package me.pacho.android;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

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

    private static final int WINDOW_SIZE = 101;
	private SensorManager mSensorManager;
    private File file;
    private OutputStreamWriter osw;
    private FileOutputStream fos;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mGravity;
    private String archive_name=System.nanoTime()+"";
    
    private TextView tGravity;
    private TextView tAcceleration;
    private TextView tGyroscope;
    private TextView tNacceleration;
    //private OrientationCalculator orientationCalculator;
    private ArrayList<float[]> hGravity=new ArrayList<float[]>();
    private ArrayList<float[]> hGyroscope=new ArrayList<float[]>();;
    private ArrayList<float[]> hAcceleration=new ArrayList<float[]>();;
    

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        
        setContentView(R.layout.main);

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        File path=new File(Environment.getExternalStorageDirectory(),"data/SensorLogger/");
        
        if(!path.exists())path.mkdirs();

        file = new File(path.getAbsolutePath()+"/"+archive_name+".log"); 
        Log.w("ExternalStorage",file.getAbsolutePath());
        Log.w("ExternalStorage", Environment.getExternalStorageState());
        
        try {
            file.createNewFile();
            fos = new FileOutputStream(file);
        } catch (IOException e) {     	
            Log.e("ExternalStorage", "Error opening " + file, e);
        }
        
        tAcceleration=(TextView) findViewById(R.id.tAcc);
        tNacceleration=(TextView) findViewById(R.id.tNacc);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
       
        tGyroscope=(TextView) findViewById(R.id.tGyro);
        mGyroscope=mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        
        tGravity=(TextView) findViewById(R.id.tGrav);
        mGravity=mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_FASTEST);

        
      //  orientationCalculator=new OrientationCalculator();
      /*  while(!orientationCalculator.stillWorking){
        	try {
				//Thread.currentThread().sleep(500);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				tOrientation.setText("Waiting...");
				e.printStackTrace();
			}
        }*/
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
    	String line="";
        float[] promedio;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
        	hAcceleration.add(event.values);
        	if(hAcceleration.size()>=WINDOW_SIZE){
        		hAcceleration.remove(0);
        	}
        	promedio=average(hAcceleration);
        	line="ACC,"+event.timestamp+createString(promedio);
        	tAcceleration.setText(line);
        	calculateNetAcceleration(event);
       	
        }
        else if(event.sensor.getType() ==Sensor.TYPE_GYROSCOPE){
        	hGyroscope.add(event.values);
        	if(hGyroscope.size()>=WINDOW_SIZE){
        		hGyroscope.remove(0);
        	}
        	promedio=average(hGyroscope);
        	line="GYR,"+event.timestamp+createString(promedio);
        	tGyroscope.setText(line);

        }
        else if (event.sensor.getType()== Sensor.TYPE_GRAVITY){
           	hGravity.add(event.values);
        	if(hGravity.size()>=WINDOW_SIZE){
        		hGravity.remove(0);
        	}
        	promedio=average(hGravity);
        	line="GRA,"+event.timestamp+createString(promedio);
        	tGravity.setText(line);
        	calculateNetAcceleration(event);
        }
     
        else return;
        
       // tOrientation.setText("ORI: "+orientationCalculator.stringData);
        try {
        	fos.write(line.getBytes());
		} catch (IOException e) {			
            Log.e("ExternalStorage", "Error writing line" + file, e);
		}
		
    }

	private void calculateNetAcceleration(SensorEvent event) {
    	//Usando el sensor de gravedad

		if(hAcceleration.size()>=WINDOW_SIZE-1 && hGravity.size()>=WINDOW_SIZE-1){
				float[] acceleration=average(hAcceleration);
				float[] gravity=average(hGravity);
				float[] values={acceleration[0]-gravity[0],acceleration[1]-gravity[1],acceleration[2]-gravity[2]};

		    	String line="NACC,"+event.timestamp+createString(values);
		    	tNacceleration.setText(line);
	
		    	try {
		    		fos.write(line.getBytes());
		    	} catch (IOException e) {
		    		Log.e("ExternalStorage", "Error writing line" + file, e);
		    	}
	
			}
	}
	

	private float[] average(ArrayList<float[]> array){
		float[] sum={0f,0f,0f};
		int n=array.size();
		for(int i=0;i<array.size();i++){
			float[] actual=array.get(i);
			sum[0]+=actual[0];
			sum[1]+=actual[1];
			sum[2]+=actual[2];
		}
		float[] average={sum[0]/n,sum[1]/n,sum[2]/n};
		return average;
	}
	
	private String createString(float[] array){
		String line="";
        for(int i=0;i<array.length;i++){
        	line+=","+array[i];
        }
        line+="\n";
        return line;
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

}
