package javax.microedition.lcdui;

/**
 * 
 * @author Andre Nijholt
 */
public abstract class Screen extends Displayable {
	protected void keyPressed(int keyCode) {}
	protected void keyReleased(int keyCode) {}
	
	protected  void showNotify() {
		repaint();
	}
	protected  void hideNotify() {
		repaint();
	}
	 
	protected abstract void paint(Graphics g);
}
