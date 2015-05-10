package com.nk.breakemup;

public class Vector2 {
	public static final Vector2 right = new Vector2(1, 0);
	public static final Vector2 left = new Vector2(-1, 0);
	public static final Vector2 up = new Vector2(0, -1);
	public static final Vector2 down = new Vector2(0, 1);
	public static final Vector2 zero = new Vector2(0, 0);
	
	public static final Vector2 upLeft = new Vector2(-1, -1);
	public static final Vector2 downLeft = new Vector2(-1, 1);
	public static final Vector2 upRight = new Vector2(1, -1);
	
	protected float x, y;
	
	public Vector2(){
		this(0, 0);
	}
	
	public Vector2(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public void add(Vector2 i){
		this.x += i.x;
		this.y += i.y;
	}
	
	public void sub(Vector2 i){
		this.x -= i.x;
		this.y -= i.y;
	}
	
	public static Vector2 add(Vector2 i, Vector2 j){
		return new Vector2(i.x + j.x, i.y + j.y);
	}

	public static float distanceSqr(Vector2 i, Vector2 j){
		float dx = j.x - i.x;
		float dy = j.y - i.y;
		return dx * dx + dy * dy;
	}
	
	public static float distance(Vector2 i, Vector2 j){
		return (float)Math.sqrt(distanceSqr(i, j));
	}
	
	@Override
	public String toString() {
		return String.format("Vector2[x:%f, y:%f] ", x, y);
	}
}
