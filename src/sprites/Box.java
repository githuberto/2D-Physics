package sprites;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import physics.Vec;

public class Box extends Sprite{
	double width;
	double height;
	
	/*** Constructors ***/
	
	public Box(double x0, double y0, double width, double height, Color c){
		pos = new Vec(x0, y0);
		vel = new Vec(0, 0);
		this.width = width;
		this.height = height;
		restitution = .5;
		color = c;
	}
	public Box(int x0, int y0, int width, int height, Color c){
		pos = new Vec(x0, y0);
		vel = new Vec(0, 0);
		this.width = width;
		this.height = height;
		restitution = .50;
		color = c;
	}
	
	/*** Accessors ***/
	
	public double width(){
		return width;
	}
	
	public double height(){
		return height;
	}
	
	// return top left corner, bottom left numerically
	public Vec tlCorner(){
		return new Vec(pos.x() - width/2, pos.y() - height/2);
	}
	
	// return bottom right corner, top right numerically
	public Vec brCorner(){
		return new Vec(pos.x() + width/2, pos.y() + height/2);
	}
	
	/*** Simulation ***/
	
	public void draw(Graphics g){
		Color temp = g.getColor();
		g.setColor(color);
		g.fillRect(d2I(pos.x() - width/2), d2I(pos.y() - height/2), d2I(width), d2I(height));
		drawVecText(g);
		g.setColor(temp);
	}
	
	public void drawVecText(Graphics g){
		g.setColor(Color.WHITE);
		g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 12));
		g.drawString("V: " + vel, d2I(pos.x() - width/4), d2I(pos.y()));
	}
	
//	public boolean inBounds(double w, double h){
//		double x = pos.x();
//		double y = pos.y();
//		double hW = width / 2;
//		double hH = height / 2;
//		
//		return x - hW < width
//				&& x + hW > 0
//				&& y - hH < height
//				&& y + hH > 0;
//	}
	
	/*** Utilities ***/
	
	
	public String toString(){
		return color2String(color) + " box";
	}
	
}
