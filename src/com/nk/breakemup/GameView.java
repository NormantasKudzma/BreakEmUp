package com.nk.breakemup;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
		STARTING, RUNNING, ENDING, GAMEOVER, QUITTING
	}
	
	protected SurfaceHolder holder;
	protected Bitmap bitmap;
	protected Canvas canvas; // for debugging raycasts
	protected Paint paint = new Paint(); // debug
	protected int screenHeight, screenWidth;
	protected GameEngine engine;
	
	protected long sleepInterval = 33; // Delay between frames
	protected long trivialInterval = 10; // minimum delay between frames
	protected Thread gameThread = null;
	private Handler quitHandler;
	
	protected GamePhase phase = GamePhase.STARTING;
	
	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);

		Display d = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		screenWidth = d.getWidth();
		screenHeight = d.getHeight();
		bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		paint.setTextSize(0.05f * screenHeight);
		
		engine = new GameEngine(screenWidth, screenHeight);
		
		setKeepScreenOn(true);
		registerTouchListener();
		
		quitHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				((Activity)GameView.this.getContext()).onBackPressed();
			}
		};
		getHolder().addCallback(this);
		
		Log.w("nk", "GameView initiated");
	}
	
	@Override
	public void run() {
		long t0, t1, delta;
		
		Log.w("nk", "Draw thread started");
		try {
			if (phase == GamePhase.QUITTING){
				Log.w("nk", "Thread stopped. GameState is " + phase);
				return;
			}
			
			// Game is starting
			drawGameStarting();
			drawAllObjects();
			drawTextDecorations();
			lockDrawAndPost();
			while (phase == GamePhase.STARTING){
				Thread.sleep(150);
			}
			
			// Game is running
			while (!engine.isGameOver()){
				// Draw blocks, move&draw paddle, move&draw ball
				t0 = System.currentTimeMillis();
				engine.movePaddle();
				drawPaddle();
				drawBlocks();
				engine.moveBall(bitmap);
				drawBall();
				drawTextDecorations();
				lockDrawAndPost();
				t1 = System.currentTimeMillis();
				
				delta = t0 + sleepInterval - t1;
				if (delta < trivialInterval){
					delta = trivialInterval;
				}
				Thread.sleep(delta);
			}
			
			// Game is ending, if have lives or completed level - restart, 
			// else if no lives or phase is quitting - go back to main menu
			checkEndingPhase();
			switch (phase){
				case ENDING: {
					drawAllObjects();
					drawGameEnding(engine.isLevelComplete());
					lockDrawAndPost();
					phase = GamePhase.STARTING;
					Thread.sleep(1500);
					if (engine.isLevelComplete()){
						engine.startNewLevel();
					}
					else {
						engine.resetLevelVariables();
					}
					gameThread = new Thread(this);
					gameThread.start();
					break;
				}
				case GAMEOVER: {
					drawAllObjects();
					drawGameOver();
					lockDrawAndPost();
					Thread.sleep(2000);
					quitHandler.sendEmptyMessage(0);
					break;
				}
				default: {
					break;
				}
			}
			Log.w("nk", "Thread stopped. GameState is " + phase);
		}
		catch (InterruptedException ie){
			ie.printStackTrace();
		}
	}
	
	// Check if player still has lives left
	protected void checkEndingPhase(){
		if (phase != GamePhase.QUITTING){
			if (engine.getLives() > 0){
				phase = GamePhase.ENDING;
			}
			else {
				phase = GamePhase.GAMEOVER;
			}
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
		float y = screenHeight * 0.05f;
		canvas.drawText("Lives: " + engine.getLives(), 0.08f * screenWidth, y, paint);
		canvas.drawText("Level : " + engine.getLevel(), 0.22f * screenWidth, y, paint);
		canvas.drawText("Speed : " + engine.getBall().getSpeed(), 0.37f * screenWidth, y, paint);
		canvas.drawText("Score: " + engine.getScore(), 0.9f * screenWidth, y, paint);
	}
	
	protected void drawGameStarting(){
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText("Tap anywhere to start", screenWidth / 2, screenHeight / 2, paint);
	}
	
	protected void drawGameEnding(boolean levelCompleted){
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Paint.Align.CENTER);
		String str = levelCompleted ? "Level cleared" : "You lost a life";
		canvas.drawText(str, screenWidth / 2, screenHeight / 2, paint);
		canvas.drawText("Lives left : " + engine.getLives(), screenWidth / 2, screenHeight * 0.6f, paint);
	}
	
	protected void drawGameOver(){
		paint.setColor(Color.WHITE);
		paint.setTextAlign(Paint.Align.CENTER);
		canvas.drawText("Game over", screenWidth / 2, screenHeight / 2, paint);
		canvas.drawText("Final score: " + engine.getScore(), screenWidth / 2, screenHeight * 0.6f, paint);
	}
	
	protected void drawAllObjects(){
		drawPaddle();
		drawBlocks();
		drawBall();
		drawTextDecorations();
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
		Log.w("nk", "FIXME: SurfaceChanged called.");
	}

	@Override
	public void surfaceCreated(SurfaceHolder s) {
		this.holder = s;
		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder s) {
		Log.w("nk", "Surface destroyed called");
		unregisterTouchListener();
		phase = GamePhase.QUITTING;
		engine.isGameOver(true);
		try {
			if (gameThread != null){
				gameThread.join();
				gameThread = null;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (holder != null){
			holder.removeCallback(this);
		}
		this.holder = null;
		engine = null;
	}
}
