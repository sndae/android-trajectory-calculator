package me.pacho.android.PathDrawer2D;


import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class PathManager implements SensorEventListener{
	
	SensorManager mSensorManager;
	//ArrayList<Float[]> radialVelocities;
	//ArrayList<Float[]> linealAccelerations;
	ArrayList<Float[]> trajectory;
	float[] lastLinealAcceleration={0f,0f,0f};
	float[] lastLinealVelocity={0f,0f,0f};
	float[] lastRadialVelocity={0f,0f,0f};
	long lastTimeStamp=0;
	
	public PathManager(){
		trajectory=new ArrayList<Float[]>();
        mSensorManager = (SensorManager) Drawer.getContext().getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
        
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
        	if(lastTimeStamp!=0){
        		
        	}
    		copyArray(lastLinealAcceleration, event.values);
    		lastTimeStamp=event.timestamp;
        }
        else if(event.sensor.getType() ==Sensor.TYPE_GYROSCOPE){
        	if(lastTimeStamp!=0){
        		
        	}
    		copyArray(lastRadialVelocity, event.values);
    		lastTimeStamp=event.timestamp;
        }
        
	}
	
	

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	private void copyArray(float[] destination, float[] source) {
		System.arraycopy(source, 0, destination, 0, 3);
	}
}
