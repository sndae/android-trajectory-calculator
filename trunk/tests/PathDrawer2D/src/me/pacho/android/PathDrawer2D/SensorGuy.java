package me.pacho.android.PathDrawer2D;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;



public class SensorGuy implements SensorEventListener{

    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mGravity;
    private Sensor	mMagnetic;
	private SensorManager mSensorManager;

    
    private float[] lastGravity={0f,0f,0f};
    private float[] lastGyroscope={0f,0f,0f};
    private float[] lastAcceleration={0f,0f,0f};
    private float[] lastMagnetic={0f,0f,0f};
    
    
    private float[] mOrientation = new float[3]; 
    private float[] mRotationM = new float[9];               // Use [16] to co-operate with android.opengl.Matrix 
    private float[] mRemapedRotationM = new float[9]; 

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
        
        mMagnetic=mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mMagnetic, SensorManager.SENSOR_DELAY_FASTEST);
        
        
    }
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	 public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
        	copyArray(lastAcceleration,event.values);
        	calculateOrientation();

        }
        else if(event.sensor.getType() ==Sensor.TYPE_GYROSCOPE){
        	copyArray(lastGyroscope,event.values);
        }
        else if (event.sensor.getType()== Sensor.TYPE_GRAVITY){
        	copyArray(lastGravity,event.values);
        }
        else if (event.sensor.getType()== Sensor.TYPE_MAGNETIC_FIELD){
        	copyArray(lastMagnetic,event.values);
        	calculateOrientation();
        }
     
        else return;
		


    }
	

	private void calculateOrientation(){
        if(SensorManager.getRotationMatrix(mRotationM, null, lastAcceleration, lastMagnetic)){
        	SensorManager.remapCoordinateSystem(mRotationM, SensorManager.AXIS_X, SensorManager.AXIS_Z, mRemapedRotationM); 
            SensorManager.getOrientation(mRemapedRotationM, mOrientation); 

        }
	}
	
	private void copyArray(float[] destination, float[] source) {
		System.arraycopy(source, 0, destination, 0, 3);
	}
	public float[] getAcceleration(){
		float[] resp={lastAcceleration[0]-lastGravity[0],lastAcceleration[1]-lastGravity[1],lastAcceleration[2]-lastGravity[2]};
		return resp;
	}	

	public float[] getGravity(){
		return lastGravity;
	}
	public float[] getGyroscope(){	
		return new float[]{rad2deg(lastGyroscope[0]),rad2deg(lastGyroscope[1]),rad2deg(lastGyroscope[2])};
	}
	public float[] getOrientation(){
		return new float[]{rad2deg(mOrientation[0]),rad2deg(mOrientation[1]),rad2deg(mOrientation[2])};
	}
	
	public float rad2deg(float value){
		return ((Math.round((Math.toDegrees(value)) *2)/2)+360)%360;
	}


}
