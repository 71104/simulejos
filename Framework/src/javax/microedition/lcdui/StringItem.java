package javax.microedition.lcdui;

/**
 * 
 * @author Andre Nijholt
 */
public class StringItem extends Item {
	private String text;
	
	public StringItem(String label, String text) {
		this.label = label;
		this.text = text;

		if (label != null) {
			minWidth = (label.length() * Display.CHAR_WIDTH);
			minHeight = Display.CHAR_HEIGHT;
		}
		
		if (text != null) {
			if ((minWidth + text.length() * Display.CHAR_WIDTH) < Display.SCREEN_WIDTH) {
				// Append to current line
				minWidth += (text.length() * Display.CHAR_WIDTH);
			} else {
				minWidth = Math.max(minWidth, text.length() * Display.CHAR_WIDTH);
				minHeight += Display.CHAR_HEIGHT;
			}
		}
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public void paint(Graphics g, int x, int y, int w, int h, boolean selected) {
		if (label != null) {
			g.drawString(label, x, y, 0 );
			if (h <= Display.CHAR_HEIGHT) {
				x += label.length() * Display.CHAR_WIDTH;
			} else {
				y += Display.CHAR_HEIGHT;
			}
		}

		g.drawString(text, x, y, 0, selected);
	}
}
