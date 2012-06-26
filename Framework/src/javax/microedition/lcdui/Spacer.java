package javax.microedition.lcdui;

/**
 * 
 * @author Andre Nijholt
 */
public class Spacer extends Item {
	public Spacer(int minWidth, int minHeight) {
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}

	public void addCommand(Command cmd) {
		throw new IllegalStateException();
	}
	 
	public void setDefaultCommand(Command cmd) {
		throw new IllegalStateException();
	}
	
	public void setLabel(String label) {
		throw new IllegalStateException();
	}
	
	public void setMinimumSize(int minWidth, int minHeight){
		this.minWidth = minWidth;
		this.minHeight = minHeight;
	}
	
	public void paint(Graphics g, int x, int y, int w, int h, boolean selected) {}
}
