package sprites;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import physics.Vec;

public class Circle extends Sprite{
	double radius;

	public Circle(double x0, double y0, double r, Color c){
		pos = new Vec(x0, y0);
		radius = r;
		color = c;
		restitution = 0.5;
	}
	
	@Override
	public void move(double t) {
		pos.add(vel.times(t));
	}

	@Override
	public void draw(Graphics g) {
		Color temp = g.getColor();
		g.setColor(color);
		g.fillOval(d2I(pos.x() - radius), d2I(pos.y() - radius), d2I(radius * 2), d2I(radius * 2));
		drawVecText(g);
		g.setColor(temp);
	}
	
	public void drawVecText(Graphics g){
		g.setColor(Color.BLACK);
		g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 12));
		g.drawString("V: " + vel, d2I(pos.x() - radius/2), d2I(pos.y()));
	}
	
	public double r(){
		return radius;
	}
	
	public String toString(){
		return color2String(color) + " circle";
	}
	

}
