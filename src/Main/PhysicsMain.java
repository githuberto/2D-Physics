package Main;
import graphics.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;


import physics.Vec;
import sprites.*;


public class PhysicsMain {
	/*** Physics Constants***/
	/**
	 * all calculations will be made in SI units (e.g. velocity in meters per second)
	 * TIME_STEP - the number of calculations per second
	 * TIME_SCALE - the speed of the simulation (1 being normal speed)
	 * PIXEL_SCALE - the size of a pixel in the simulation
	 **/
	
	public static final int WIDTH = 1000;
	public static final int HEIGHT = 600;
	public static final double TIME_STEP = 0.01; // in seconds
	public static final double TIME_SCALE = 1;
	public static final double GRAVITY = 9.81;
	public static final double PIXEL_SCALE = 0.01; // in meters
	public static ArrayList<Sprite> sprites;
	public static ArrayList<Sprite> spriteQueue = new ArrayList<Sprite>();
	public static Dispatcher disp;
	public static PhysicsWindow frame;
	public static final PhysicsWindow[] FRAME_REF = new PhysicsWindow[1]; // holds frame reference for anonymous classes
	
	public static long last = System.currentTimeMillis();
	
	
	/*** Initialization ***/
	
	public static void main(String[] args){
		// make sprites
		sprites = new ArrayList<Sprite>();
//		makeCircles(sprites);
		makeBoxes(sprites);
		
		// add all sprites to dispatcher
		disp = new Dispatcher();
		for(Sprite s : sprites)
			disp.addObserver(s);
		
		// make frame
		frame = makeFrame();
		FRAME_REF[0] = frame;
		
		// make timer
		Timer drawTimer = makeTimer(TIME_STEP, TIME_SCALE);
		System.out.println(drawTimer.getDelay());
		// start simualtion
		drawTimer.start();
	}
	
	public static void makeCircles(ArrayList<Sprite> sprites){
		sprites.add(new Circle(300, 400, 100, Color.MAGENTA));
		sprites.get(0).setVel(-20, 0);
		sprites.get(0).setMass(500);
		sprites.add(new Circle(700, 400, 30, Color.ORANGE));
		sprites.get(1).setVel(-80, 0);
	}
	
	public static void makeBoxes(ArrayList<Sprite> sprites){
		sprites.add(new Box(WIDTH / 2, HEIGHT - 1, WIDTH, 100, Color.BLACK));
		sprites.get(0).setMass(0);
//		sprites.add(new Box(100, 100, 120, 120, Color.RED));
//		sprites.add(new Box(230, 100, 100, 100, Color.BLUE));
//		sprites.get(sprites.size() - 1).setVel(-500, 15);
		sprites.add(new Box(400, 340, 70, 200, Color.GREEN));
	}
	
	public static PhysicsWindow makeFrame(){
		PhysicsWindow newFrame = new PhysicsWindow(WIDTH, HEIGHT, disp);
		newFrame.setSprites(sprites);
		newFrame.setDisp(disp);
		newFrame.setVisible(true);
		return newFrame;
	}
	
	public static javax.swing.Timer makeTimer(double t, double tScale){
		int timeSlice = (int)(t * 1000);
		ActionListener refreshGraphics = new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				countTicks();
				callRepaint();
				addSprites();
				doPhysics();
			}
		};
		return new javax.swing.Timer(timeSlice, refreshGraphics);
	}
	
	public static void callRepaint(){
		frame.panel().repaint();
	}
	
	public static void countTicks(){
		System.out.println(System.currentTimeMillis() - last);
		last = System.currentTimeMillis();
	}
	
	/*** Simulation ***/
	
	// run the simulation
	public static void doPhysics(){
		int size = sprites.size();
		for(int i = size - 1; i >= 0; i--){
			Sprite a = sprites.get(i);
			if(inBounds(a)){
				applyGravity(a);
				a.move(TIME_STEP, PIXEL_SCALE);
				for(int j = i - 1; j >= 0; j--){
					Sprite b = sprites.get(j);
					//collision code here
				}
			}
			else{
				disp.deleteObserver(a);
				sprites.remove(i);
				System.out.println("ded");
			}
		}
	}
	
	// apply gravity
	private static void applyGravity(Sprite s){
		if(s.invMass() > 0){
			Vec v = s.vel();
			v.add(0, GRAVITY * TIME_STEP * TIME_SCALE);
		}
	}

	/*** Collision ***/
	
	private static boolean objectsCollided(Sprite a, Sprite b, Vec n){
		if(a == b){
			return false;
		}
		if(a instanceof Box && b instanceof Box){
			return AABBvsAABB((Box) a, (Box) b, n);
		}
		if(a instanceof Circle && b instanceof Circle){
			n = b.pos().minus(a.pos()).toNormal();
			return circleVSCircle((Circle) a, (Circle) b);
		}
		return false;
	}
	// check collision for two boxes 
	private static boolean AABBvsAABB(Box a, Box b, Vec n){
		Vec minA = a.tlCorner();
		Vec minB = b.tlCorner();
		Vec maxA= a.brCorner();
		Vec maxB = b.brCorner();

		if(maxA.x() < minB.x()){
			n = new Vec(-1, 0);
			return false;
		}
		if(maxA.x() < minB.x()){
			n = new Vec(1, 0);
			return false;
		}
		if(maxA.y() < minB.y()){
			
			n = new Vec(0, -1);
			return false;
		}
		if(minA.y() > maxB.y()){
			n = new Vec(0, 1);
			return false;
		}
//		System.out.println(a + " collided with " + b);
		return true;
	}
	// check collision for two circles
	private static boolean circleVSCircle(Circle a, Circle b){
		double r = a.r() + b.r();
		r *= r;
		double dX = b.x() - a.x();
		double dY = b.y() - a.y();
		return r > dX*dX + dY*dY;
	}
	// resolve the collision
	private static void resolveCollision(Sprite a, Sprite b, Vec n){
		System.out.println("resolving...");
		// calculate relative velocty
		Vec relVel = b.vel().minus(a.vel());
		System.out.println("relative velocity: " + relVel);
		
		// calculate scalar projection of velocity relative to the normal
		double velNorm = relVel.dot(n);
		System.out.println("normal component of velocity" + velNorm);
		
		if(velNorm < 0){
			// set the restitution to the minimum of the two sprites
			double e = Math.min(a.restitution(), b.restitution());
			
			// calculate impulse scalar
			double j = (velNorm * -(1 + e)) / (a.invMass() + b.invMass());
			
			// apply impulse
			Vec impulse = n.times(j);
			System.out.println("impulse: " + impulse);
			a.vel().sub(impulse.times(a.invMass()));
			b.vel().add(impulse.times(b.invMass()));
			System.out.println("a vel: " + a.vel());
			System.out.println("b vel: " + b.vel());
		}
	}
	
	/*** Utilities ***/
	
	// check if the vector is inside the window
	private static boolean inBounds(Sprite s){
		return s.inBounds(WIDTH, HEIGHT);
	}
	
	public static void makeBox(double x0, double y0){
		spriteQueue.add(new Box(x0, y0, 100, 100, Color.RED));
	}
	public static void makeCircle(double x0, double y0){
		spriteQueue.add(new Circle(x0, y0, 50, Color.BLUE));
	}
	public static void addSprites(){
		sprites.addAll(spriteQueue);
		for(Sprite s : spriteQueue){
			disp.addObserver(s);
		}
		spriteQueue.clear();
	}
	
}
