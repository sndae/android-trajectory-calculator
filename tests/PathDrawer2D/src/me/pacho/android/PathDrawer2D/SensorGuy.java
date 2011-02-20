package me.pacho.android.PathDrawer2D;

import java.util.ArrayList;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


public class SensorGuy implements SensorEventListener{

    private static final int WINDOW_SIZE = 2;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mGravity;
	private SensorManager mSensorManager;

    private ArrayList<float[]> hGravity=new ArrayList<float[]>();
    private ArrayList<float[]> hGyroscope=new ArrayList<float[]>();;
    private ArrayList<float[]> hAcceleration=new ArrayList<float[]>();;
    
    private float[] lastAveragedGravity=new float[3];
    private float[] lastAveragedGyroscope=new float[3];
    private float[] lastAveragedAcceleration=new float[3];
    public SensorGuy(){
    	Drawer.getContext();
    	if(Drawer.getContext()==null){
    		Log.e("Context NULL","CONTEXT NULL");
    	}
        mSensorManager = (SensorManager) Drawer.getContext().getSystemService(Context.SENSOR_SERVICE);
    
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
       
        mGyroscope=mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        
        mGravity=mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_FASTEST);

    }
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	 public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
        	hAcceleration.add(event.values);
        	if(hAcceleration.size()>=WINDOW_SIZE){
        		hAcceleration.remove(0);
        	}
        	calculateNetAcceleration(event);
       	
        }
        else if(event.sensor.getType() ==Sensor.TYPE_GYROSCOPE){
        	hGyroscope.add(event.values);
        	if(hGyroscope.size()>=WINDOW_SIZE){
        		hGyroscope.remove(0);
        	}
        	lastAveragedGyroscope=average(hGyroscope);

        }
        else if (event.sensor.getType()== Sensor.TYPE_GRAVITY){
           	hGravity.add(event.values);
        	if(hGravity.size()>=WINDOW_SIZE){
        		hGravity.remove(0);
        	}
        	lastAveragedGravity=average(hGravity);

        	calculateNetAcceleration(event);
        }
     
        else return;
		
    }
	
	private void calculateNetAcceleration(SensorEvent event) {
    	//Usando el sensor de gravedad

		if(hAcceleration.size()>=WINDOW_SIZE-1 && hGravity.size()>=WINDOW_SIZE-1){
				float[] acceleration=average(hAcceleration);
				float[] gravity=average(hGravity);
				lastAveragedAcceleration[0]=acceleration[0]-gravity[0];
				lastAveragedAcceleration[1]=acceleration[1]-gravity[1];
				lastAveragedAcceleration[2]=acceleration[2]-gravity[2];

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

	
	public float[] getAcceleration(){
		return lastAveragedAcceleration;
	}
	public float[] getGravity(){
		return lastAveragedGravity;
	}
	public float[] getGyroscope(){
		return lastAveragedGyroscope;
	}

}
