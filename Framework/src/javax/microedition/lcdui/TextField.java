package javax.microedition.lcdui;

/**
 * 
 * @author Andre Nijholt
 */
public class TextField extends Item {
	public static final int ANY 					= 0x0001;
	public static final int EMAILADDR 				= 0x0002;
	public static final int NUMERIC 				= 0x0003;
	public static final int PHONENUMBER 			= 0x0004;
	public static final int URL		 				= 0x0005;
	public static final int DECIMAL 				= 0x0006;
	
	public static final int PASSWORD				= 0x0100;
	public static final int UNEDITABLE				= 0x0200;
	public static final int SENSITIVE				= 0x0300;
	public static final int NON_PREDICTIVE			= 0x0400;
	public static final int INITIAL_CAPS_WORD		= 0x0500;
	public static final int INITIAL_CAPS_SENTENCE	= 0x0600;

	private String text;
	private int maxSize;
	private int constraints;

	public TextField(String label, String text, int maxSize, int constraints) {
		this.label = label;
		this.text = text;
		this.maxSize = maxSize;
		this.constraints = constraints;
		this.interactive = true;
		
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

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
	public int getMaxSize() {
		return maxSize;
	}
	
	public int getConstraints() {
		return constraints;
	}

	public void paint(Graphics g, int x, int y, int w, int h, boolean selected) {
		if (label != null) {
			g.drawString(label, x, y, 0);
			if (h <= Display.CHAR_HEIGHT) {
				x += label.length() * Display.CHAR_WIDTH;
			} else {
				y += Display.CHAR_HEIGHT;
			}
		}

		g.drawString(text, x, y, 0, selected);
	}
}
