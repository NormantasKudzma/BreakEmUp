package com.nk.breakemup;

import android.graphics.RectF;
import android.util.Log;

public class Ball extends Paddle {
	protected Vector2 movementDirection = Vector2.zero;
	
	public Ball(){
		this(new Vector2());
	}
	
	public Ball(Vector2 position){
		super(position);
		size = new Vector2();
	}
	
	public float getRadius(){
		return this.size.x;
	}
	
	public void setRadius(float r){
		this.size.x = r;
	}
	
	public Vector2 [] getHotspots(){
		Vector2 [] hotspots = new Vector2[4];
		float radius = getRadius() * 0.9f;
		float deg2rad90 = GameEngine.deg2rad * 90;
		for (int i = 0; i < hotspots.length; i++){
			hotspots[i] = new Vector2((float)Math.cos(i * deg2rad90) * radius, (float)Math.sin(i * deg2rad90) * radius);
			hotspots[i].add(position);
		}
		return hotspots;
	}
	
	public void setMovementDirection(Vector2 dir){
		movementDirection = dir;
	}
	
	public void move(float dist){
		Vector2 v = new Vector2(dist * movementDirection.x, dist * movementDirection.y);
		position.add(v);
	}
	
	public void flipMovementDirectionX(){
		movementDirection.x = -movementDirection.x;
	}
	
	public void flipMovementDirectionY(){
		movementDirection.y = -movementDirection.y;
	}
	
	public Vector2 getMovementDirection(){
		return movementDirection;
	}
	
	@Override
	public String toString() {
		return super.toString() + String.format("[Radius:%3.2f; Direction:%s]", getRadius(), movementDirection);
	}
}
