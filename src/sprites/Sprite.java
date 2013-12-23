package sprites;

import graphics.PhysicsWindow;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Observable;
import java.util.Observer;

import Main.PhysicsMain;
import physics.Vec;

public abstract class Sprite implements Observer {
	public static final double P_SCALE = PhysicsMain.PIXEL_SCALE;
	
	Vec pos;
	Vec vel = new Vec(0,0);
	double invMass = 0.01;
	double restitution;
	Color color;

	/*** Simulation ***/
	
	// move the sprite using velocity given timestep
	public void move(double t){
		pos.add(vel.times(t * P_SCALE));
	}
	
	// respond to a call from the observer
	public void update(Observable o, Object g){
		draw((Graphics) g);
	}
	
	// draw the sprite to the panel
	public abstract void draw(Graphics g);

	/*** Accessors ***/
	
	public void setColor(Color c){
		color = c;
	}
	
	public Vec pos(){
		return pos;
	}
	
	public double x(){
		return pos().x();
	}
	
	public double y(){
		return pos().y();
	}
	
	public Vec vel(){
		return vel;
	}
	
	public double restitution(){
		return restitution;
	}
	
	public double invMass(){
		return invMass;
	}
	
	public void setVel(double x0, double y0){
		vel.set(x0 * P_SCALE, y0 * P_SCALE);
	}
	
	public void setMass(double m){
		if(m > 0)
			invMass = 1/m;
		else
			invMass = 0;
	}
	
	/*** Utilities ***/
	
	public static int d2I(double d){
		return (int)(0.5 + d);
	}
	
	static String color2String(Color c){
		if(c.equals(Color.RED))
			return "red";		
		if(c.equals(Color.BLUE))
			return "blue";
		if(c.equals(Color.GREEN))
			return "green";
		if(c.equals(Color.YELLOW))
			return "yellow";
		if(c.equals(Color.ORANGE))
			return "yellow";
		if(c.equals(Color.BLACK))
			return "black";
		if(c.equals(Color.MAGENTA))
			return "magenta";
		return "" + c;
	}
}
