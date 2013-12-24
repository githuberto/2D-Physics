package physics;

import sprites.Box;
import sprites.Circle;
import sprites.Sprite;

public class Collision {
	Sprite spriteA;
	Sprite spriteB;
	ColType type;
	Vec n = new Vec(0, 0);
	double penetration = 0;
	
	public Collision(Sprite a, Sprite b){
		init(a, b);		
	}
	
	public Collision(Box a, Box b){
		type = ColType.BoxvsBox;
		init(a, b);
	}
	
	private void init(Sprite a, Sprite b){
		spriteA = a;
		spriteB = b;
		if(type == null)
			type = checkType(a, b);
	}
	
	/*** Accessors ***/
	
	public Vec n(){
		return n;
	}
	
	public double p(){
		return penetration;
	}
	
	/*** Collision Checks ***/
	
	public  Collision checkCollision(Sprite a, Sprite b){
		if(a == b){
			return null;
		}
		if(a instanceof Box && b instanceof Box){

			
		}
		if(a instanceof Circle && b instanceof Circle){
			n = b.pos().minus(a.pos()).toNormal();
		}
		return null;
	}
	
	
	// check collision for two boxes 
//	private static boolean AABBvsAABB(Box a, Box b, Vec n){
//		Vec minA = a.tlCorner();
//		Vec minB = b.tlCorner();
//		Vec maxA= a.brCorner();
//		Vec maxB = b.brCorner();
//
//		if(maxA.x() < minB.x()){
//			n = new Vec(-1, 0);
//			return false;
//		}
//		if(maxA.x() < minB.x()){
//			n = new Vec(1, 0);
//			return false;
//		}
//		if(maxA.y() < minB.y()){
//			
//			n = new Vec(0, -1);
//			return false;
//		}
//		if(minA.y() > maxB.y()){
//			n = new Vec(0, 1);
//			return false;
//		}
//		System.out.println(a + " collided with " + b);
//		return true;
//	}
	// check collision for two circles
	private static boolean circleVSCircle(Circle a, Circle b){
		double r = a.r() + b.r();
		r *= r;
		double dX = b.x() - a.x();
		double dY = b.y() - a.y();
		return r > dX*dX + dY*dY;
	}
	
	
	/*** Collisions ***/
	
	private void getNormalBoxvsCircle(){
		Box a;
		Circle b;
		
		if(spriteA instanceof Box){
			a = (Box) spriteA;
			b = (Circle) spriteB;
		}
		else{
			a = (Box) spriteA;
			b = (Circle) spriteB;
		}
		
		n = b.pos().minus(a.pos());
		
		// calculate the half x extents
		double aExtent = (a.max().x() - a.min().x()) / 2;
		double bExtent = b.r() / 2;
		
		double xPen = aExtent + bExtent - Math.abs(n.x());
		
		if(xPen > 0){
			// calculate the half y extents
			aExtent = (a.max().y() - a.min().y()) / 2;
			
			double yPen = aExtent + bExtent - Math.abs(n.x());
			
			if(xPen < yPen){
				Vec collisionPoint = a.pos().plus(xPen, 0);
			}
		}		
	}
	
	private void boxVsBox(){
		Box a = (Box) spriteA;
		Box b = (Box) spriteB;
		
		n = b.pos().minus(a.pos());
		
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
					penetration = xPen;
					if(n.x() > 0)
						n = new Vec(1, 0);
					else
						n = new Vec(-1, 0);
				}
				else{
					penetration = yPen;
					if(n.y() > 0)
						n = new Vec(0, 1);
					else
						n = new Vec(0, -1);
				}
			}
			else
				n = null;
		}
		else
			n = null;
	}
	
	
	/*** Utilities ***/
	
	// return the type of collision occurring
	private static ColType checkType(Sprite a, Sprite b){
		if(a instanceof Box && b instanceof Box)
			return ColType.BoxvsBox;
		if((a instanceof Box && b instanceof Circle) || (a instanceof Circle && b instanceof Box)){
			return ColType.BoxvsCircle;
		}
		if(a instanceof Circle && b instanceof Circle){
			return ColType.CirclevsCircle;
		}
		return null;
	}
	
	private static double clamp(double val, double min, double max){
		if(val < min)
			return min;
		if(val > max)
			return max;
		return val;
	}

}
