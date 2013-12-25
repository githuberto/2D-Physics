package Main;

import physics.Vec;
import sprites.Sprite;

/*** a container for collision objects ***/

class Manifold{
	public Sprite a;
	public Sprite b;
	public Vec n;
	public double penetration;

	public Manifold(Sprite a, Sprite b){
		this.a = a;
		this.b = b;
		n = new Vec(0, 0);
		penetration = 0;
	}
}