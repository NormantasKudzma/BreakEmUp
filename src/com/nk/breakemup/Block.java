package com.nk.breakemup;

import android.graphics.Rect;

public class Block extends GameBlock{
	public static final int [] ROW_COLORS = new int[]{0xffff0033, 0xff90ee90, 0xffad98e6, 0xffee82ee}; 
	public static final int BASE_POINTS = 120;
	
	protected int points;
	protected Rect boundingRect;

	public Block(){
		super();
	}
	
	public Block(Vector2 position){
		super(position);
	}
	
	public void setPoints(int pts){
		this.points = pts;
	}
	
	public int getPoints(){
		return points;
	}
	
	@Override
	public void hit(GameEngine e) {
		e.addScore(points);
		e.getBlocks().remove(this);
		e.getColliders().remove(this);
		e.checkLevelComplete();
	}
	
	@Override
	public void setPosition(Vector2 pos) {
		super.setPosition(pos);
		boundingRect = super.getBoundingRect();
	}
	
	@Override
	public Rect getBoundingRect() {
		return boundingRect;
	}
	
	@Override
	public String toString() {
		return String.format("[%s; Points: %d]", super.toString(), points);
	}
}
