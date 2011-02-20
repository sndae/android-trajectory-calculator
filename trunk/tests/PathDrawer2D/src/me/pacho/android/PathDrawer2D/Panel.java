package me.pacho.android.PathDrawer2D;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class Panel extends SurfaceView implements SurfaceHolder.Callback {
	
	private CanvasThread canvasthread;
	private SensorGuy sensorGuy;
	private int count=0;
	private int[] screenSize=new int[2]; 
	public Panel(Context context, AttributeSet attrs) {
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
        sensorGuy=Drawer.getSensorGuy();

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
    	if(count==0){
    		canvas.drawColor(Color.BLACK);
    	}

        screenSize[0]=canvas.getWidth();
        screenSize[1]=canvas.getHeight();
        Paint paint = new Paint();
        //entre 5 y -5 m/s^2
        float[] acceleration=sensorGuy.getAcceleration();//{3f,0f,-3f};//
        float scale=screenSize[1]/8;
        int zero=(int)(screenSize[1]*0.4);
      //x
        paint.setColor(Color.RED);
        canvas.drawPoint(count, acceleration[0]*scale+zero, paint);
      //y
        paint.setColor(Color.BLUE);
        canvas.drawPoint(count, acceleration[1]*scale+zero, paint);
      //z
        paint.setColor(Color.WHITE);
        canvas.drawPoint(count, acceleration[2]*scale+zero, paint);
        
        canvas.drawRect(0,0,screenSize[0],50, paint);
        paint.setColor(Color.WHITE);
        canvas.drawText("Count: "+count, 5,20, paint);
        count++;
        if(count>screenSize[0]){
        	count=0;
        }
   }
     
}