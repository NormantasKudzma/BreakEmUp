package com.nk.breakemup;

public class Collision {
	private boolean hit;
	private Vector2 collisionPoint;
	
	public Collision(){
		this(false, new Vector2());
	}
	
	public Collision(boolean hit, Vector2 point){
		this.hit = hit;
		this.collisionPoint = point;
	}
	
	public boolean hasCollided(){
		return hit;
	}
	
	public void hasCollided(boolean hit){
		this.hit = hit;
	}
	
	public Vector2 getCollisionPoint(){
		return collisionPoint;
	}
	
	public void setCollisionPoint(Vector2 point){
		this.collisionPoint = point;
	}
	
	@Override
	public String toString() {
		return String.format("Collision[Hit:%b; Point:%s]", hit, collisionPoint);
	}
}
