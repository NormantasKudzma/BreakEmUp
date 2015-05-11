package com.nk.breakemup;


public class Paddle extends GameBlock{
	float moveSpeed = 12;
	float nextMove;
	
	public Paddle(){
		super();
	}
	
	public Paddle(Vector2 position) {
		super(position);
	}

	public void setSpeed(float speed){
		this.moveSpeed = speed;
	}
	
	public float getSpeed(){
		return moveSpeed;
	}
	
	public void queueNextMove(float dir){
		nextMove = dir;
	}
	
	public void movePaddle(){
		if (nextMove != 0){
			position.x += nextMove;
		}
	}
	
	@Override
	public void hit(GameEngine e) {
		Ball b = e.getBall();
		b.position.add(Vector2.up);	// ball-paddle collision fix
		if (nextMove != 0){
			Vector2 dir = b.getMovementDirection();
			float angle = (float) Math.asin(dir.y) * GameEngine.rad2deg;
			float eps = 10;
			if (Math.signum(dir.x) == Math.signum(nextMove)){
				angle += eps;
			}
			else {
				// priesinga kryptis - kampas dideja
				angle -= eps;
				if (angle < 15){
					angle = 15;
				}
			}
			angle *= GameEngine.deg2rad;
			b.setMovementDirection(new Vector2((float)Math.cos(angle) * Math.signum(dir.x), (float)Math.sin(angle) * Math.signum(dir.y)));
		}
	}
	
	@Override
	public String toString() {
		return super.toString() + String.format("[Speed:%3.2f, NextMove:%3.2f]", moveSpeed, nextMove);
	}
}
