package graphics;

import java.awt.Graphics;
import java.util.Observable;

public class Dispatcher extends Observable{
	public void notifyAll(Graphics g){
		setChanged();
		notifyObservers(g);
	}
}
