package physics;

import sprites.Box;
import sprites.Circle;
import sprites.Sprite;

public class Collision {
	Sprite spriteA;
	Sprite spriteB;
	ColType type;
	Vec n;
	double penetration;
	
	public Collision(Sprite a, Sprite b){
		spriteA = a;
		spriteB = b;
		type = checkType(a, b);
	}
	
	/*** Collisions ***/
	
	private void AABBvsAABB(){
		Box a = (Box) spriteA;
		Box b = (Box) spriteB;
		
		n = b.pos().minus(a.pos());
		
		double xExtent = Math.abs(a.x() + a.width() / 2);
		double yExtent = Math.abs(a.y() + a.width() / 2);
		
		
	}
	
	
	/*** Utilities ***/
	
	// return the type of collision occurring
	private static ColType checkType(Sprite a, Sprite b){
		if(a instanceof Box && b instanceof Box)
			return ColType.AABBvsAABB;
		if((a instanceof Box && b instanceof Circle) || (a instanceof Circle && b instanceof Box)){
			return ColType.CirclevsCircle;
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
