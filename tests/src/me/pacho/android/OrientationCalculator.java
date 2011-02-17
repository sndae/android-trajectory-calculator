package me.pacho.android; 
import android.app.Activity; 
import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor; 
import android.hardware.SensorEvent; 
import android.hardware.SensorEventListener; 
import android.hardware.SensorManager; 
import android.os.Bundle; 
import android.os.IBinder;
import android.view.ViewGroup.LayoutParams; 
import android.widget.TextView; 
public class OrientationCalculator extends Service implements 
SensorEventListener { 
    	public String stringData; 

        private SensorManager mSensMan; 
        private float[] mGravs = new float[3]; 
        private float[] mGeoMags = new float[3]; 
        private float[] mOrientation = new float[3]; 
        private float[] mRotationM = new float[9];               // Use [16] to co-operate with android.opengl.Matrix 
        private float[] mRemapedRotationM = new float[9]; 
        public boolean stillWorking=true; 
    /** Called when the activity is first created. */ 
        public OrientationCalculator(){ 
                mSensMan = (SensorManager)getSystemService(Service.SENSOR_SERVICE); 
                mSensMan.registerListener(this, mSensMan.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_UI);  //Anonymous Sensors- no further use for them. 
                mSensMan.registerListener(this, mSensMan.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI); 
    } 
        @Override 
        public void onAccuracyChanged(Sensor sensor, int accuracy) { 
                // Do nothing 
        } 
        @Override 
        public void onSensorChanged(SensorEvent event) { 
                switch (event.sensor.getType()) { 
                case Sensor.TYPE_ACCELEROMETER: 
                        /* 
                         * NOTE: The data must be copied off the event.values 
                         * as the system is reusing that array in all SensorEvents. 
                         * Simply assigning: 
                         * mGravs = event.values won't work. 
                         * 
                         * I use a member array in an attempt to reduce garbage production. 
                         */ 
                        System.arraycopy(event.values, 0, mGravs, 0, 3); 
                        break; 
                case Sensor.TYPE_MAGNETIC_FIELD: 
                        // Here let's try another way: 
                        for (int i=0;i<3;i++) mGeoMags[i] = event.values[i]; 
                                break; 
                        default: 
                                return; 
                } 
                if (SensorManager.getRotationMatrix(mRotationM, null, mGravs, mGeoMags)){ 
                        SensorManager.remapCoordinateSystem(mRotationM, SensorManager.AXIS_X, SensorManager.AXIS_Z, mRemapedRotationM); 
                        SensorManager.getOrientation(mRemapedRotationM, mOrientation); 
                        onSuccess(); 
                } 
                else onFailure(); 
        } 
        void onSuccess(){ 
            if (stillWorking) stillWorking = false; 
            stringData=mOrientation[0]+","+mOrientation[1]+","+mOrientation[2]+"\n";
        } 
        void onFailure() { 
                if (!stillWorking) { 
                        stringData="Failed to retrive rotation Matrix"; 
                } 
        }
		@Override
		public IBinder onBind(Intent arg0) {
			// TODO Auto-generated method stub
			return null;
		} 
}
