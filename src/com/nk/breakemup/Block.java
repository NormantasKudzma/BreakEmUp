package com.nk.breakemup;

public class Block extends GameBlock{
	public static final int [] rowColors = new int[]{0xffff0033, 0xff90ee90, 0xffad98e6, 0xffee82ee}; 
	
	protected int points = 150;

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
	}
}
