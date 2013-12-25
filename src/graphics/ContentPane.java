package graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import Main.PhysicsMain;

public class ContentPane extends JPanel implements MouseListener {
	private static final long serialVersionUID = -6488049508551841098L;
	Dispatcher disp;
	
	public ContentPane(Dispatcher d){
		disp = d;
		initPane();
	}
	
	private void initPane(){
		setBackground(Color.WHITE);
		setForeground(Color.WHITE);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		addMouseListener(this);
	}
	
	// alert all observing sprites upon paint
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		disp.notifyAll(g);
	}
	
	/*** Mouse Events ***/
	
	public void mouseClicked(MouseEvent e) {
	}
	public void mouseEntered(MouseEvent e) {
		// do nothing
	}
	public void mouseExited(MouseEvent e) {
		// do nothing
	}
	public void mousePressed(MouseEvent e) {
		// do nothing
	}
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){
			PhysicsMain.makeBox(e.getX(), e.getY());
		}
		if(e.getButton() == MouseEvent.BUTTON3){
			PhysicsMain.makeCircle(e.getX(), e.getY());
		}
		// do nothing
	}
}
