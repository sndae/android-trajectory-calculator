package me.pacho.android; 
import android.app.Activity; 
import android.hardware.Sensor; 
import android.hardware.SensorEvent; 
import android.hardware.SensorEventListener; 
import android.hardware.SensorManager; 
import android.os.Bundle; 
import android.view.ViewGroup.LayoutParams; 
import android.widget.TextView; 
public class OrientationDemo extends Activity implements 
SensorEventListener { 
    	TextView tData; 

        private SensorManager mSensMan; 
        private float mAzimuth; 
        private float mPitch; 
        private float mRoll; 
        private float[] mGravs = new float[3]; 
        private float[] mGeoMags = new float[3]; 
        private float[] mOrientation = new float[3]; 
        private float[] mRotationM = new float[9];               // Use [16] to co-operate with android.opengl.Matrix 
        private float[] mRemapedRotationM = new float[9]; 
        private boolean mFailed; 
    /** Called when the activity is first created. */ 
    @Override 
    public void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); 
//      I'd like to actually see something so let's have a view: 
        tData = new TextView(this); 

        addContentView(tData, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT)); 
//      Initiate the Sensor Manager and register this as Listener for the required sensor types: 
//      TODO: Find how to get a SensorManager outside an Activity, to implement as a utility class. 
                mSensMan = (SensorManager) getSystemService(SENSOR_SERVICE); 
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
//                      Rotate to the camera's line of view (Y axis along the camera's axis) 
//                      TODO: Find how to use android.opengl.Matrix to rotate to an arbitrary coordinate system. 
                        SensorManager.remapCoordinateSystem(mRotationM, SensorManager.AXIS_X, SensorManager.AXIS_Z, mRemapedRotationM); 
                        SensorManager.getOrientation(mRemapedRotationM, mOrientation); 
                        onSuccess(); 
                } 
                else onFailure(); 
        } 
        void onSuccess(){ 
                if (mFailed) mFailed = false; 
//              Convert the azimuth to degrees in 0.5 degree resolution. 
/*                mAzimuth = (float) Math.round((Math.toDegrees(mOrientation[0])) *2)/2;
                mPitch = (float) Math.round((Math.toDegrees(mOrientation[1])) *2)/2;
                mRoll = (float) Math.round((Math.toDegrees(mOrientation[2])) *2)/2;*/
//              Adjust the range: 0 < range <= 360 (from: -180 < range <= 180). 
/*                mAzimuth = (mAzimuth+360)%360; // alternative: mAzimuth = mAzimuth>=0 ? mAzimuth : mAzimuth+360;
                mPitch = (mAzimuth+360)%360;
                mRoll = (mAzimuth+360)%360;*/
                mAzimuth = mOrientation[0];
                mPitch = mOrientation[1];
                mRoll = mOrientation[0];
                tData.setText("Azimuth(Z)= " + mAzimuth+"\n"+"Pitch(X)= " + mPitch+"\n"+"Roll(Y)= " + mRoll); 

        } 
        void onFailure() { 
                if (!mFailed) { 
                        mFailed = true; 
                        tData.setText("Failed to retrive rotation Matrix"); 
                } 
        } 
}
