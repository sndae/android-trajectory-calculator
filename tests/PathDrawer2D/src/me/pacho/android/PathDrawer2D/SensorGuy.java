package me.pacho.android.PathDrawer2D;

import java.util.ArrayList;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


public class SensorGuy implements SensorEventListener{

    private static final int WINDOW_SIZE = 20;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mGravity;
	private SensorManager mSensorManager;

    private ArrayList<float[]> hGyroscope=new ArrayList<float[]>();;
    private ArrayList<float[]> hNetAcceleration=new ArrayList<float[]>();;
    
    private float[] lastGravity=new float[3];
    private float[] lastGyroscope=new float[3];
    private float[] lastAveragedNetAcceleration=new float[3];
    private float[] lastAcceleration=new float[3];
    
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
        	lastAcceleration=event.values;
        	calculateNetAcceleration(event);
       	
        }
        else if(event.sensor.getType() ==Sensor.TYPE_GYROSCOPE){
        	hGyroscope.add(event.values);
        	if(hGyroscope.size()>=WINDOW_SIZE){
        		hGyroscope.remove(0);
        	}
        	lastGyroscope=average(hGyroscope);

        }
        else if (event.sensor.getType()== Sensor.TYPE_GRAVITY){

        	lastGravity=event.values;

        	calculateNetAcceleration(event);
        }
     
        else return;
		
    }
	
	private void calculateNetAcceleration(SensorEvent event) {
    	//Usando el sensor de gravedad
	    float[] lastNetAcceleration=new float[3];

		if(lastAcceleration!=null && lastGravity!=null){
				lastNetAcceleration[0]=lastAcceleration[0]-lastGravity[0];
				lastNetAcceleration[1]=lastAcceleration[1]-lastGravity[1];
				lastNetAcceleration[2]=lastAcceleration[2]-lastGravity[2];
				
				hNetAcceleration.add(lastNetAcceleration);
		    	if(hNetAcceleration.size()>=WINDOW_SIZE){
		    		hNetAcceleration.remove(0);
		    	}
		    	lastAveragedNetAcceleration=average(hNetAcceleration);
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
		return lastAcceleration;
	}	
	public float[] getNetAcceleration(){
		return lastAveragedNetAcceleration;
	}
	public float[] getGravity(){
		return lastGravity;
	}
	public float[] getGyroscope(){
		return lastGyroscope;
	}

}
