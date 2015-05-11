package com.nk.breakemup;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Bitmap;
import android.util.Log;

public class GameEngine {
	public static final float deg2rad = 3.14159f / 180;
	public static final float rad2deg = 180 / 3.14159f;
	
	protected float screenWidth, screenHeight;
	protected float xmin, xmax, ymin, ymax;
	
	protected Ball ball;
	protected Paddle paddle;
	protected ArrayList<Block> blocks;
	protected ArrayList<GameBlock> colliders;
	
	protected int numBlocks = 28;
	protected int blocksPerRow = 7;
	
	protected int score = 0;
	protected int bonusLivePts = 0;
	protected int lives = 2;
	protected int level = 0;
	protected boolean levelCompleted = false;
	protected boolean gameOver = false;
	
	public GameEngine(int screenWidth, int screenHeight){
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		xmin = ymin = 2;
		xmax = this.screenWidth - xmin;
		ymax = this.screenHeight - ymin;
		
		startNewLevel();
	}
	
	public void startNewLevel(){
		level++;
		colliders = new ArrayList<GameBlock>();
		createBlocks();
		resetLevelVariables();
	}
	
	public void resetLevelVariables(){
		levelCompleted = false;
		gameOver = false;
		
		if (paddle != null){
			colliders.remove(paddle);
		}
		
		createPaddle();
		createBall();
	}
	
	// Create paddle in the lower middle of the screen
	// Paddle's speed is dependent on screen width and level
	public void createPaddle(){
		float midPoint = screenWidth / 2;
		paddle = new Paddle();
		paddle.setSize(new Vector2((int)(screenWidth / 4.8f), (int)(screenHeight * 0.028f)));
		paddle.setPosition(new Vector2(midPoint - paddle.getSize().x / 2, (int)(screenHeight * 0.91f)));
		paddle.setSpeed(screenWidth * 0.035f + level * 0.8f);
		Log.w("nk", "Paddle parameters " + paddle);
		
		colliders.add(paddle);
	}
	
	// Create and place blocks
	// Block point value is dependent on level and row in which it is placed
	public void createBlocks(){
		blocks = new ArrayList<Block>();
		Vector2 blockSz = new Vector2((int)(screenWidth * 0.75f / blocksPerRow), (int)(screenHeight * 0.26f / (numBlocks / blocksPerRow)));
		int numRows = numBlocks / blocksPerRow;
		int points = Block.BASE_POINTS * level;
		float spacingH = screenWidth * 0.15f / (blocksPerRow - 1);
		float spacingV = screenHeight * 0.1f / (numRows);
		
		for (int i = 0; i < numBlocks; i++){
			int row = (int)(i / blocksPerRow);
			int clr = Block.ROW_COLORS[row];
			Vector2 pos = new Vector2(0.05f * screenWidth + (i % blocksPerRow) * (blockSz.x + Math.signum((i % blocksPerRow)) * spacingH),
									  0.05f * screenHeight + row * (blockSz.y + spacingV));
			Block b = new Block();
			b.setColor(clr);
			b.setSize(blockSz);
			b.setPosition(pos);
			b.setPoints(points + (numRows - row) * 10);
			blocks.add(b);
			colliders.add(b);
			Log.w("nk", "New block " + b);
		}
	}
	
	// Create ball and assign a random movement direction to it
	// Ball's movement speed is dependent on level
	public void createBall(){
		float midPoint = screenWidth / 2;
		ball = new Ball();
		ball.setRadius(screenHeight * 0.018f);
		ball.setSpeed(screenWidth * 0.021f + level * 4);
		ball.setPosition(new Vector2(midPoint, screenHeight * 0.86f));
		
		Random rnd = new Random();
		float angle = (rnd.nextInt(90) + 45) * deg2rad;
		ball.setMovementDirection(new Vector2((float)Math.cos(angle), -(float)Math.sin(angle)));
		Log.w("nk", "Ball created: " + ball);
	}
	
	// Queues next paddle's movement
	// so paddle can be moved on frame update
	public void queuePaddleMove(float x){
		if (x > 0){
			float midPoint = screenWidth / 2;
			float delta = Math.signum(x - midPoint);
			paddle.queueNextMove(delta * paddle.getSpeed());
		}
		else {
			paddle.queueNextMove(0);
		}
	}
	
	public void movePaddle(){
		paddle.movePaddle();
		Vector2 pos = paddle.getPosition();
		if (pos.x < 0){
			pos.x = 0;
		}
		else {
			float paddleWidth = paddle.getSize().x;
			if (pos.x + paddleWidth > screenWidth - 1){
				pos.x = screenWidth - 1 - paddleWidth;
			}
		}
	}
	
	public boolean isGameOver(){
		return gameOver;
	}
	
	public void isGameOver(boolean isGameOver){
		this.gameOver = isGameOver;
	}
	
	public boolean isLevelComplete(){
		return levelCompleted;
	}
	
	public void checkLevelComplete(){
		gameOver = levelCompleted = blocks.isEmpty();
	}
	
	// Checks for collisions in ball's path, 
	// moves ball to the next position, flips
	// movement direction
	public void moveBall(Bitmap bitmap){
		Vector2 [] hotspots = ball.getHotspots();
		float dist = ball.getSpeed() * ball.getSpeed();
		Vector2 closestColl = null;
		GameBlock closestObj = null;
		for (Vector2 hs : hotspots){
			Collision c = raycast(bitmap, hs, ball.getMovementDirection(), ball.getSpeed());
			if (c.hasCollided()){
				GameBlock obj = getCollisionObj(c.getCollisionPoint());
				float d = Vector2.distanceSqr(hs, c.getCollisionPoint());
				if (d <= dist){
					dist = d;
					closestColl = c.getCollisionPoint();
				}
				if (obj != null){
					obj.hit(this);
					closestObj = obj;
				}
			}
		}
		
		dist = (float) Math.sqrt(dist);
		ball.move(dist);
		
		// Flip ball's movement direction if collision happened
		if (closestColl != null){
			if (closestColl.x <= xmin || closestColl.x >= xmax){
				ball.flipMovementDirectionX();
				return;
			}
			if (closestColl.y >= ymax){
				lives--;
				gameOver = true;
				return;
			}
			if (closestColl.y <= ymin){
				ball.flipMovementDirectionY();
				return;
			}
			if (closestObj != null){
				Vector2 imask = closestObj.getMovementInversionMask(closestColl);
				Vector2 dir = ball.getMovementDirection();
				dir.x *= imask.x;
				dir.y *= imask.y;
			}
			else {
				Log.w("nk", "Incorrect collision?");
			}
		}
	}
	
	public Paddle getPaddle(){
		return paddle;
	}

	public ArrayList<Block> getBlocks(){
		return blocks;
	}

	public Ball getBall(){
		return ball;
	}

	public ArrayList<GameBlock> getColliders(){
		return colliders;
	}
	
	public void addScore(int amount){
		score += amount;
		
		int scoreForBonusLive = 10_000;
		if (score > bonusLivePts + scoreForBonusLive){
			lives++;
			bonusLivePts += scoreForBonusLive;
		}
	}
	
	public int getScore(){
		return score;
	}
	
	public int getLives(){
		return lives;
	}
	
	public int getLevel(){
		return level;
	}
	
	// Draw a ray from origin towards direction, ray stops if it hits
	// wall or any currently drawn object.
	// Returns collision hit information or empty object if collision didn't happen
	public Collision raycast(Bitmap b, Vector2 origin, Vector2 direction){
		return raycast(b, origin, direction, screenWidth);
	}
	
	// Raycast with maximum depth
	public Collision raycast(Bitmap b, Vector2 origin, Vector2 direction, float maxDepth){
		Collision col = new Collision();
		if (origin.x < xmin || origin.x > xmax || origin.y < ymin || origin.y > ymax){
			return col;
		}
		
		int empty = 0xff000000;
		float depthSqr = maxDepth * maxDepth;
		Vector2 currentPoint = new Vector2(origin.x, origin.y);
		while (Vector2.distanceSqr(currentPoint, origin) < depthSqr){
			if (currentPoint.x > xmin && currentPoint.x < xmax &&
			    currentPoint.y > ymin && currentPoint.y < ymax){
				if (b.getPixel((int)currentPoint.x, (int)currentPoint.y) == empty){
					currentPoint.add(direction);
				}
				else {
					col.setCollisionPoint(currentPoint);
					col.hasCollided(true);
					break;
				}
			}
			else {
				col.setCollisionPoint(currentPoint);
				col.hasCollided(true);
				break;
			}
		}
		return col;
	}

	// Finds an object which contains a point
	public GameBlock getCollisionObj(Vector2 point){
		int px = (int)point.x, py = (int)point.y;
		for (GameBlock i : colliders){
			if (i.getBoundingRect().contains(px, py)){
				return i;
			}
		}
		return null;
	}
}
