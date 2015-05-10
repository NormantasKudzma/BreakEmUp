package com.nk.breakemup;

import android.graphics.Rect;
import android.graphics.RectF;

public abstract class GameBlock implements Collidable{
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
		return new Rect(posX, posY, posX + (int)size.x, posY + (int)size.y);
	}
	
	@Override
	public String toString() {
		return "Size:" + size + ", Position:" + position + String.format("[Color:%d]", color);
	}
}
