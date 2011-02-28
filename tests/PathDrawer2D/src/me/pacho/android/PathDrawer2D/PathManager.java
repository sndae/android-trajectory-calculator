/*This version of the Path Manager Class uses the orientation matrix provided by the android environment to make
 *  the path calculations.
 */

package me.pacho.android.PathDrawer2D;


import java.util.ArrayList;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class PathManager implements SensorEventListener{
	
	SensorManager mSensorManager;
	ArrayList<Float[]> trajectory;
	
	
	public float[] lastLinealAcceleration={0f,0f,0f};
	public float[] lastLinealVelocity={0f,0f,0f};
	public float[] lastMagnetic={0f,0f,0f};
	public float[] lastOrientation ={0f,0f,0f};
	public float[] lastGravity ={0f,0f,0f};
	public float[] lastNetAcceleration={0f,0f,0f};

	//float[] lastRadialVelocity={0f,0f,0f};

    
    private float[] mRotationM = new float[9];               // Use [16] to co-operate with android.opengl.Matrix 
    private float[] mRemapedRotationM = new float[9]; 


    public long lastTimeStamp=0;
	//deltaTime is in seconds
    public double deltaTime=0;
	
    public long nanoDeltaTime=0;
	
	public PathManager(){
		trajectory=new ArrayList<Float[]>();
		
		Float[] temp={0f,0f,0f};
		trajectory.add(temp);
		
        mSensorManager = (SensorManager) Drawer.getContext().getSystemService(Context.SENSOR_SERVICE);
        
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);

        //mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);

        
	}

	@Override
	public void onSensorChanged(SensorEvent event) {


		
			
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
    		Log.i("Last Time Stamp",lastTimeStamp+","+event.sensor.getType());

        	if(lastTimeStamp!=0){
    			nanoDeltaTime=event.timestamp-lastTimeStamp;
    			deltaTime=(double)(nanoDeltaTime/1000000000d);
            	calculateOrientation();
            	calculateNetAcceleration();
            	correctAcceleration();
            	//approx 1.
            	calculateMovement();

        	}
    		copyArray(lastLinealAcceleration, event.values);
        	//For the next change
        	calculateNextLinealVelocity();
        	lastTimeStamp=event.timestamp;

        }
        
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
    		copyArray(lastMagnetic, event.values);
        }
        
        else if (event.sensor.getType() == Sensor.TYPE_GRAVITY){
    		copyArray(lastGravity, event.values);
        }
        
        
        
        /*else if(event.sensor.getType() ==Sensor.TYPE_GYROSCOPE){
        	if(lastTimeStamp!=0){
        		
        	}
    		copyArray(lastRadialVelocity, event.values);
    		lastTimeStamp=event.timestamp;
        }*/
        
	}
	
	private void calculateMovement() {
		Float[] lastPosition=trajectory.get(trajectory.size()-1);
		Float[] newPosition={(float) (lastPosition[0]+lastLinealVelocity[0]*deltaTime),
							(float) (lastPosition[1]+lastLinealVelocity[1]*deltaTime),
							(float) (lastPosition[2]+lastLinealVelocity[2]*deltaTime)};
		trajectory.add(newPosition);
	}

	private void calculateNextLinealVelocity() {
		lastLinealVelocity[0]+=lastNetAcceleration[0]*deltaTime;
		lastLinealVelocity[1]+=lastNetAcceleration[2]*deltaTime;
		lastLinealVelocity[2]+=lastNetAcceleration[1]*deltaTime;
	}

	private void correctAcceleration() {
		//Rotation in X
		float a=lastOrientation[0];
		lastNetAcceleration[1]=(float) (lastNetAcceleration[1]*Math.cos(a)-lastNetAcceleration[2]*Math.sin(a));
		lastNetAcceleration[2]=(float) (lastNetAcceleration[1]*Math.sin(a)+lastNetAcceleration[2]*Math.cos(a));
		//Rotation in Y
		a=lastOrientation[1];
		lastNetAcceleration[0]=(float) (lastNetAcceleration[0]*Math.cos(a)+lastNetAcceleration[2]*Math.sin(a));
		lastNetAcceleration[2]=(float) (-lastNetAcceleration[0]*Math.sin(a)+lastNetAcceleration[2]*Math.cos(a));
		//Rotation in Z
		a=lastOrientation[1];
		lastNetAcceleration[0]=(float) (lastNetAcceleration[0]*Math.cos(a)-lastNetAcceleration[1]*Math.sin(a));
		lastNetAcceleration[1]=(float) (lastNetAcceleration[0]*Math.sin(a)+lastNetAcceleration[1]*Math.cos(a));
		
	}

	private void calculateNetAcceleration() {
			float[] resp={lastLinealAcceleration[0]-lastGravity[0],lastLinealAcceleration[1]-lastGravity[1],lastLinealAcceleration[2]-lastGravity[2]};
			copyArray(lastNetAcceleration,resp);
	}

	private void calculateOrientation(){
        if(SensorManager.getRotationMatrix(mRotationM, null, lastLinealAcceleration, lastMagnetic)){
        	SensorManager.remapCoordinateSystem(mRotationM, SensorManager.AXIS_X, SensorManager.AXIS_Z, mRemapedRotationM); 
            SensorManager.getOrientation(mRemapedRotationM, lastOrientation); 

        }
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	private void copyArray(float[] destination, float[] source) {
		System.arraycopy(source, 0, destination, 0, 3);
	}
	
	public Float[] getLastPosition(){
		return trajectory.get(trajectory.size()-1);
	}
}
