package Main;

import physics.Vec;
import sprites.Sprite;

/*** a wrapper class for collision objects ***/

class Wrapper{
	public Sprite a;
	public Sprite b;
	public Vec n;
	public double penetration;

	public Wrapper(Sprite a, Sprite b){
		this.a = a;
		this.b = b;
		n = new Vec(0, 0);
		penetration = 0;
	}
}