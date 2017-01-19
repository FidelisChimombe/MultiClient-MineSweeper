package minesweeper;

public class Square {
	private int X;
	private int Y;
	private String state;
	private boolean bomb;
	
	public Square(int x,int y,String s,boolean b){
		this.X=x;
		this.Y=y;
		this.state=s;
		this.bomb=b;
	}
	
	public int getX(){
		return this.X;
	}
	
	public int getY(){
		return this.Y;
	}
	
	public String getState(){
		return this.state;
	}
	
	public boolean isBomb(){
		return this.bomb;
	}
	
	public void setX(int x){
		this.X=x;
	}
	
	public void setY(int y){
		this.Y=y;
	}
	
	public void setBomb(boolean b){
		this.bomb=b;
	}
	
	public void setState(String s){
		this.state=s;
	}
	
	
}
