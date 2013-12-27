package physics;

public class Vec {
	double x;
	double y;
	
	/*** Constructors ***/
	
	public Vec(double x0, double y0){
		x = x0;
		y = y0;
	}
	
	public Vec(Vec v){
		x = v.x;
		y = v.y;
	}
	
	/*** Accessors ***/
	
	public double x(){
		return x;
	}
	
	public double y(){
		return y;
	}
		
	/*** Math ***/
	
	public Vec times(Double d){
		return new Vec(x*d, y*d);
	}
	
	public Vec plus(Vec v){
		return new Vec(x + v.x, y + v.y);
	}
	
	public Vec plus(double dx, double dy){
		return new Vec(x + dx, y + dy);
	}
	
	public Vec minus(Vec v){
		return new Vec(x - v.x, y - v.y);
	}
	
	public double dot(Vec v){
		return x * v.x + y * v.y;
	}
	
	public double magnitude(){
		return Math.sqrt(x*x + y*y);
	}
	
	public double magSquared(){
		return x*x + y*y;
	}
	
	public Vec toNormal(){
		return times(1 / magnitude());
	}
	
	/*** Operations ***/
	
	public void add(Vec v){
		x += v.x;
		y += v.y;
	}
	
	public void add(double dx, double dy){
		x += dx;
		y += dy;
	}
	
	public void sub(double dx, double dy){
		x -= dx;
		y -= dy;
	}
	
	public void sub(Vec v){
		x -= v.x;
		y -= v.y;
	}
	
	public void set(Vec v){
		x = v.x;
		y = v.y;
	}
	
	public void set(double x0, double y0){
		x = x0;
		y = y0;
	}
	
	public void setX(double x0){
		x = x0;
	}
	
	public void setY(double y0){
		y = y0;
	}
	
	/*** Misc ***/
	
	public String toString(){
		return "[" + x + ", " + y + "]";
//		return "[" + (int)(0.5+x) + ", " + (int)(0.5+y) + "]";
	}
	
	public boolean equals(Vec v){
		return x == v.x && y == v.y;
	}
}
