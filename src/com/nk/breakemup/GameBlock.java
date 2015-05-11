package com.nk.breakemup;

import android.graphics.Rect;
import android.graphics.RectF;

public abstract class GameBlock{
	protected Vector2 size;
	protected Vector2 position;
	int color = 0xffffffff;
	
	public GameBlock(){
		this(new Vector2());
	}
	
	public GameBlock(Vector2 position){
		this.position = position;
	}
	
	public void setSize(Vector2 size){
		this.size = size;
	}
	
	public Vector2 getSize(){
		return size;
	}
	
	public void setColor(int color){
		this.color = color;
	}
	
	public int getColor(){
		return color;
	}
	
	public void setPosition(Vector2 pos){
		this.position = pos;
	}
	
	public Vector2 getPosition(){
		return position;
	}
	
	public Rect getBoundingRect(){
		int posX = (int)position.x;
		int posY = (int)position.y;
		return new Rect(posX + 2, posY + 2, posX + (int)size.x - 2, posY + (int)size.y - 2);
	}
	
	public Vector2 getMovementInversionMask(Vector2 point){
		point.sub(position);
		float treshold = 2;
		if (Vector2.distance(point, Vector2.zero) < treshold ||
			Vector2.distance(point, size) < treshold ||
			Vector2.distance(point, new Vector2(0, size.y)) < treshold ||
			Vector2.distance(point, new Vector2(size.x, 0)) < treshold){
			return Vector2.upLeft;
		}
		else {
			if (point.x < treshold || point.y > size.x - treshold){
				return Vector2.downLeft;
			}
			else {
				return Vector2.upRight;
			}
		}
	}
	
	public abstract void hit(GameEngine e);
	
	@Override
	public String toString() {
		return String.format("[Size: %s; Position: %s; Color:%d]", size, position, color);
	}
}
