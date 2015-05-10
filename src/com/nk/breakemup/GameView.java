package com.nk.breakemup;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

public class GameView extends SurfaceView implements Runnable, SurfaceHolder.Callback {
	protected enum GamePhase {
		STARTING, RUNNING, ENDING
	}
	
	protected SurfaceHolder holder;
	protected Bitmap bitmap;
	public static Canvas canvas; // for debugging raycasts
	public static Paint paint = new Paint(); // debug
	protected int screenHeight, screenWidth;
	protected GameEngine engine;
	
	protected long sleepInterval = 33; // Delay between frames
	protected long trivialInterval = 10; // minimum delay between frames
	
	protected boolean levelCompleted = false;
	protected boolean gameOver = false;
	protected GamePhase phase = GamePhase.STARTING;
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		Display d = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		screenWidth = d.getWidth();
		screenHeight = d.getHeight();
		bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		
		engine = new GameEngine(screenWidth, screenHeight);
		
		setKeepScreenOn(true);
		registerTouchListener();
		
		getHolder().addCallback(this);
		
		Log.w("nk", "GameView initiated");
	}
	
	@Override
	public void run() {
		long t0, t1, delta;
		
		Log.w("nk", "Draw thread started");
		try {
			lockDrawAndPost();
			while (!gameOver){
				// Draw blocks, move&draw paddle, move&draw ball
				t0 = System.currentTimeMillis();
				engine.movePaddle();
				drawPaddle();
				drawBlocks();
				engine.moveBall(bitmap);
				//drawBall();
				drawTextDecorations();
				lockDrawAndPost();
				t1 = System.currentTimeMillis();
				
				delta = t0 + sleepInterval - t1;
				if (delta < trivialInterval){
					delta = trivialInterval;
				}
				Thread.sleep(delta);
			}
		}
		catch (InterruptedException ie){
			ie.printStackTrace();
		}
	}
	
	protected void drawPaddle(){
		Paddle paddle = engine.getPaddle();
		Rect paddleRect = paddle.getBoundingRect();
		
		paint.setColor(paddle.getColor());
		canvas.drawRect(paddleRect, paint);
	}
	
	protected void drawBlocks(){
		ArrayList<Block> blocks = engine.getBlocks();
		for (Block i : blocks){
			paint.setColor(i.getColor());
			Rect rect = i.getBoundingRect();
			canvas.drawRect(rect, paint);
		}
	}
	
	protected void drawTextDecorations(){
		paint.setColor(Color.WHITE);
		float y = screenHeight * 0.02f;
		canvas.drawText("Lives: " + engine.getLives(), 0.01f * screenWidth, y, paint);
		canvas.drawText("Score: " + engine.getScore(), 0.8f * screenWidth, y, paint);
	}
	
	protected void drawBall(){
		Ball ball = engine.getBall();
		Vector2 position = ball.getPosition();
		
		paint.setColor(ball.getColor());
		canvas.drawCircle(position.x, position.y, ball.getRadius(), paint);
	}
	
	protected void registerTouchListener(){
		setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent e) {
			    int action = e.getActionMasked();
			    float x = e.getX();
	        	
				switch (phase){
					case RUNNING:{
						switch (action){
							case MotionEvent.ACTION_DOWN:
							case MotionEvent.ACTION_MOVE:{
								engine.queuePaddleMove(x);
								break;
							}
							case MotionEvent.ACTION_UP:{
								engine.queuePaddleMove(-1f);
								break;
							}
							default:{
								break;
							}
						}
						break;
					}
					case STARTING:{
						phase = GamePhase.RUNNING;
						break;
					}
					default:{
						Log.d("nk", "Unknown gamephase");
						break;
					}
				}
				return true;
			}
			
		});
	}
	
	protected void unregisterTouchListener(){
		setOnTouchListener(null);
	}
	
	protected void lockDrawAndPost(){
		Canvas c = holder.lockCanvas();
		if (c != null){
			c.drawBitmap(bitmap, 0, 0, null);	
			holder.unlockCanvasAndPost(c);
			canvas.drawColor(Color.BLACK);
		}
		else {
			Log.e("nk", "Could not lock canvas!");
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder s, int format, int width, int height) {
		// stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder s) {
		this.holder = s;
		new Thread(this).start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder s) {
		if (holder != null){
			holder.removeCallback(this);
		}
		this.holder = null;
		unregisterTouchListener();
	}
	
}