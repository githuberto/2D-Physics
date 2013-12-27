package Main;
import graphics.*;

import java.awt.Color;
import java.awt.Graphics;
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
	
	public static long last = System.currentTimeMillis();
	
	
	/*** Initialization ***/
	
	public static void main(String[] args){
		// make sprites
		sprites = new ArrayList<Sprite>();
		makeCircles(sprites);
//		makeBoxes(sprites);
		
		// add all sprites to dispatcher
		disp = new Dispatcher();
		for(Sprite s : sprites)
			disp.addObserver(s);
		
		// make frame
		frame = makeFrame();
		
		// make timer
		Timer drawTimer = makeTimer(TIME_STEP, TIME_SCALE);
		// start simualtion
		drawTimer.start();
	}
	
	public static void makeCircles(ArrayList<Sprite> sprites){
		sprites.add(new Circle(300, 400, 100, Color.MAGENTA));
		sprites.get(0).setMass(0);
	}
	
	public static void makeBoxes(ArrayList<Sprite> sprites){
		sprites.add(new Box(WIDTH / 2, HEIGHT - 1, WIDTH, 100, Color.BLACK));
		sprites.get(0).setMass(0);
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
//				countTicks();
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
					Manifold manifold = new Manifold(a, b);
					Vec n = new Vec(0, 0);
					checkCollision(manifold);
					if(checkCollision(manifold)){
						resolveCollision(manifold);
						correctPosition(manifold);
					}
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
	
	/*** Collision Checks ***/
	
	public static boolean checkCollision(Manifold m){
		if(m.a instanceof Box && m.b instanceof Box){
			return checkBoxVsBox(m);
		}
		if(m.a instanceof Circle && m.b instanceof Circle){
			return checkCircleVsCircle(m);
		}
		if((m.a instanceof Box && m.b instanceof Circle)||(m.a instanceof Circle && m.b instanceof Box)){
			// always make sure a is a box
			if(m.a instanceof Circle){
				Sprite temp = m.a;
				m.a = m.b;
				m.b = temp;
				System.out.println(m.a == m.b);
			}
			return checkBoxVsCircle(m);
		}
		return false;
	}
	
	public static boolean checkBoxVsBox(Manifold m){
		Vec n = m.n;
		Box a = (Box) m.a;
		Box b = (Box) m.b;
		
		n.set(b.pos().minus(a.pos()));
		
		// calculate half x extents
		double aExtent = (a.max().x() - a.min().x()) / 2;
		double bExtent = (b.max().x() - b.min().x()) / 2;
		
		// calculate x-axis penetration
		double xPen = aExtent + bExtent - Math.abs(n.x());
		
		if(xPen > 0){
			// calculate half y extents
			aExtent = (a.max().y() - a.min().y()) / 2;
			bExtent = (b.max().y() - b.min().y()) / 2;
			
			// calculate y-axis penetration
			double yPen = aExtent + bExtent - Math.abs(n.y());
			
			if(yPen > 0){
				// find the axis of least penetration
				if (xPen < yPen){
					if(n.x() > 0)
						n.set(1, 0);
					else
						n.set(-1, 0);
					m.penetration = xPen;
				}
				else{
					if(n.y() > 0)
						n.set(0, 1);
					else
						n.set(0, -1);
					m.penetration = yPen;
				}
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkCircleVsCircle(Manifold m){
		Vec n = m.n;
		Circle a = (Circle) m.a;
		Circle b = (Circle) m.b;
		
		Vec disp = b.pos().minus(a.pos());
		double radius = a.r() + b.r();
		double mag2 = disp.magSquared();	// square of the displacement magnitude
		
		if(radius*radius > mag2){
			n.set(disp.toNormal());
			m.penetration = radius - disp.magnitude();

			System.out.println(m.n);
			return true;
		}
		return false;
	}
	
	public static boolean checkBoxVsCircle2(Manifold m){
		Vec n = m.n;
		Box a;
		Circle b;
		
		if(m.a instanceof Box){
			a = (Box) m.a;
			b = (Circle) m.b;
		}
		else{
			a = (Box) m.b;
			b = (Circle) m.a;
		}
		
		
		return false;
	}
	
	public static boolean checkBoxVsCircle(Manifold m){
		Vec n = m.n;
		Box a = (Box) m.a;
		Circle b = (Circle) m.b;
		
		Vec disp = b.pos().minus(a.pos());
		Vec closest = new Vec(disp);
		
		double xExtent = (a.max().x() - a.min().x()) / 2;
		double yExtent = (a.max().y() - a.min().y()) / 2;
		
		double xPos = clamp(closest.x(), -xExtent, xExtent);
		double yPos = clamp(closest.y(), -yExtent, yExtent);
		closest.set(xPos, yPos);
		
		boolean inside = false;
		
		if(closest.equals(disp)){
			inside = true;
			
			if(Math.abs(disp.x()) > Math.abs(disp.y())){
				if(closest.x() > 0)
					closest.setX(xExtent);
				else
					closest.setX(-xExtent);
			}
			else{
				if(closest.y() > 0)
					closest.setY(yExtent);
				else
					closest.setY(-yExtent);
			}
		}
		
		Vec normal = disp.minus(closest);
		double d = normal.magSquared();
		double radius = b.r();
		if(d > radius*radius && !inside)
			return false;
		
		d = Math.sqrt(d);

		m.penetration = radius - d;
		
		if(inside){
			m.n.set(disp.toNormal().times(-1.0));
		}
		else{
			m.n.set(disp.toNormal());
		}
		
		return true;
	}

	/*** Collision Resolution ***/
	
	private static void resolveCollision(Manifold m){
		Sprite a = m.a;
		Sprite b = m.b;
		Vec n = m.n;
		// calculate relative velocty
		Vec relVel = b.vel().minus(a.vel());
		
		// calculate scalar projection of velocity relative to the normal
		double velNorm = relVel.dot(n);
		
		if(velNorm < 0){
			// set the restitution to the minimum of the two sprites
			double e = Math.min(a.restitution(), b.restitution());
			
			// calculate impulse scalar
			double j = (velNorm * -(1 + e)) / (a.invMass() + b.invMass());
			
			// apply impulse
			Vec impulse = n.times(j);
			a.vel().sub(impulse.times(a.invMass()));
			b.vel().add(impulse.times(b.invMass()));
		}
	}
	
	// correct the position error created by floating points
	private static void correctPosition(Manifold m){
		
		Sprite a = m.a;
		Sprite b = m.b;
		
		double percent = 0.2;
		double slop = 0.01;
		double multiplier = Math.max(m.penetration - slop, 0.0) / (a.invMass() + b.invMass()) * percent;
		Vec correction = m.n.times(multiplier);
		
		a.pos().sub(correction.times(a.invMass()));
		b.pos().add(correction.times(b.invMass()));
	}
	
	/*** Utilities ***/
	
	// check if the vector is inside the window
	private static boolean inBounds(Sprite s){
		return s.inBounds(WIDTH, HEIGHT);
	}
	
	private static double clamp(double val, double min, double max){
		if(val < min)
			return min;
		if(val > max)
			return max;
		return val;
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
	
	public static void drawNormal(Sprite s, Vec v){
		Graphics g = frame.panel().getGraphics();
		g.setColor(Color.RED);
		int x0 = (int) (0.5 + s.pos().x());
		int y0 = (int) (0.5 + s.pos().y());
		int x1 = (int) (x0 + v.x());
		int y1 = (int) (y0 + v.y());
		g.drawLine(x0, y0, x1, y1);
	}
}


