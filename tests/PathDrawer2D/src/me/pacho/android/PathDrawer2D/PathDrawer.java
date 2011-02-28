package me.pacho.android.PathDrawer2D;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class PathDrawer extends SurfaceView implements SurfaceHolder.Callback {
	private CanvasThread canvasthread;
	private PathManager pathManager;
	private int count=0;
	private int[] screenSize=new int[2]; 
	
	public PathDrawer(Context context, AttributeSet attrs) {
      super(context, attrs);
      getHolder().addCallback(this);
      canvasthread = new CanvasThread(getHolder(), this);
      setFocusable(true);

    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                    int height) {
            // TODO Auto-generated method stub
           
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
        canvasthread.setRunning(true);
        canvasthread.start();
        pathManager=Drawer.getPathManager();
        Drawer.registerRawData(this);
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            boolean retry = true;
            canvasthread.setRunning(false);
            while (retry) {
                    try {
                            canvasthread.join();
                            retry = false;
                    } catch (InterruptedException e) {
                            // we will try it again and again...
                    }
            }

    }
    
    @Override
    public void onDraw(Canvas canvas) {
        screenSize[0]=canvas.getWidth();
        screenSize[1]=canvas.getHeight();
        
        Float[] position=pathManager.getLastPosition();
        float[] nacc=pathManager.lastNetAcceleration;
        float[] vel=pathManager.lastLinealVelocity;
        float[] ori=pathManager.lastOrientation;
        /*float[] acceleration=sensorGuy.getAcceleration();        
        float scale_accel=screenSize[1]/45;
        int zero_accel=(int)(screenSize[1]*0.25);*/
        
       /* float[] gyroscope=sensorGuy.getGyroscope();        
        int zero_gyro=(int)(screenSize[1]*0.25);
        
        
        float[] orientation=sensorGuy.getOrientation();
        float scale_orientation=screenSize[1]/720;
        int zero_orientation=(int)(screenSize[1]*0.75);*/
        
        //BG Operations
	        Paint paint = new Paint();

	    	paint.setColor(Color.WHITE);
	    	canvas.drawLine(0, screenSize[1]/2, screenSize[0], screenSize[1]/2, paint);  

	        canvas.drawRect(0,0,screenSize[0],100, paint);
	        paint.setColor(Color.BLACK);
	        canvas.drawText("Pos: "+ String.format("%.2f", position[0])+","+String.format("%.2f", position[1])+","+ String.format("%.2f", position[2]), 5,20, paint);
	        canvas.drawText("NACC: "+ String.format("%.2f", nacc[0])+","+String.format("%.2f", nacc[1])+","+nacc[2], 5,40, paint);
	        canvas.drawText("VEL: "+ String.format("%.2f", vel[0])+","+String.format("%.2f", vel[1])+","+vel[2], 5,60, paint);
	        canvas.drawText("ORI: "+ String.format("%.2f", ori[0])+","+String.format("%.2f", ori[1])+","+ori[2], 5,80, paint);
	       /* canvas.drawText("NDT: "+pathManager.nanoDeltaTime, 5,140, paint);
	        canvas.drawText("TS: "+pathManager.lastTimeStamp+","+pathManager.deltaTime, 5,170, paint);*/
	        

	        float scale_accel=screenSize[1]/90;
	        int zero_accel=(int)(screenSize[1]*0.5);
	        
	           canvas.drawLine(0, screenSize[1]/2, screenSize[0], screenSize[1]/2, paint);  
	      	   canvas.drawText("Pos(m)", 5,screenSize[1]/2-5, paint);

		      //x
		        paint.setColor(Color.RED);
		        canvas.drawCircle(position[0]*scale_accel+zero_accel, position[1]*scale_accel+zero_accel,2f, paint);
		      /*y
		        paint.setColor(Color.BLUE);
		        canvas.drawCircle(count, position[1]*scale_accel+zero_accel,2f, paint);
		      //z
		        paint.setColor(Color.GREEN);
		        canvas.drawCircle(count, position[2]*scale_accel+zero_accel,2f, paint);*/
	        
	        
	        
	        
     /*  
      //Accelerometer 
           paint.setColor(Color.WHITE);
    		canvas.drawLine(0, zero_accel, screenSize[0], zero_accel, paint);

           canvas.drawLine(0, screenSize[1]/2, screenSize[0], screenSize[1]/2, paint);  
      	   canvas.drawText("Accelerometer(m/s^2)", 5,screenSize[1]/2-5, paint);

	      //x
	        paint.setColor(Color.RED);
	        canvas.drawCircle(count, acceleration[0]*scale_accel+zero_accel,2f, paint);
	      //y
	        paint.setColor(Color.BLUE);
	        canvas.drawCircle(count, acceleration[1]*scale_accel+zero_accel,2f, paint);
	      //z
	        paint.setColor(Color.GREEN);
	        canvas.drawCircle(count, acceleration[2]*scale_accel+zero_accel,2f, paint);
	  */   
	  //Gyroscope
	  /*      paint.setColor(Color.WHITE);
	      	canvas.drawText("Gyroscope(deg/seg)", 5,screenSize[1]/2-5, paint);

	      //x
	        paint.setColor(Color.RED);
	        canvas.drawCircle(count, -(gyroscope[0]-180)*scale_orientation+zero_gyro,2f, paint);
	      //y
	        paint.setColor(Color.BLUE);
	        canvas.drawCircle(count,-(gyroscope[1]-180)*scale_orientation+zero_gyro,2f, paint);
	      //z
	        paint.setColor(Color.GREEN);
	        canvas.drawCircle(count, -(gyroscope[2]-180)*scale_orientation+zero_gyro,2f, paint);*/
	 //Orientation
	 /*       paint.setColor(Color.WHITE);
	    	canvas.drawText("Orientation(deg)",screenSize[0]-90 ,screenSize[1]/2+15, paint);

		  //x
	        paint.setColor(Color.MAGENTA);
	        canvas.drawCircle(count, -(orientation[0]-180)*scale_orientation+zero_orientation,2f, paint);
	      //y
	        paint.setColor(Color.CYAN);
	        canvas.drawCircle(count, -(orientation[1]-180)*scale_orientation+zero_orientation,2f, paint);
	      //z
	        paint.setColor(Color.YELLOW);
	        canvas.drawCircle(count,- (orientation[2]-180)*scale_orientation+zero_orientation,2f, paint);*/

        
        count++;
        if(count>screenSize[0]){
        	count=0;
        }
   }
    
     
    public void reset(){
    	count=0;
    }
}