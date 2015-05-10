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
		e.getBall().position.add(Vector2.up);	// temporary collider bugfix
	}
	
	@Override
	public String toString() {
		return super.toString() + String.format("[Speed:%3.2f, NextMove:%3.2f]", moveSpeed, nextMove);
	}
}
