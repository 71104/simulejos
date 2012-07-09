package javax.microedition.lcdui;

/**
 * 
 * @author Andre Nijholt
 */
public class Gauge extends Item {
	private int maxValue;
	private int curValue;
	
	public Gauge(String label, boolean interactive, int maxValue, int initialValue) {
		this.label = label;
		this.interactive = interactive;
		this.maxValue = maxValue;
		this.curValue = initialValue;
		
		if (label != null) {
			minWidth = (label.length() * Display.CHAR_WIDTH) + (2 * maxValue);
			minHeight = ((minWidth < Display.SCREEN_WIDTH) && (maxValue <= Display.CHAR_HEIGHT))
				? Display.CHAR_HEIGHT : (Display.CHAR_HEIGHT + maxValue);
		} else {
			minWidth = 2 * maxValue;
			minHeight = maxValue;
		}
	}
	
	public int getMaxValue() {
		return maxValue;
	}
	
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}
	
	public int getValue() {
		return curValue;
	}
	
	public void setValue(int value) {
		this.curValue = value;
		
		// Check value bounds
		if (curValue < 0) {
			curValue = 0;
		} else if (curValue > maxValue) {
			curValue = maxValue;
		}
		
		repaint();
	}
	
	protected void keyPressed(int keyCode) {
		if (keyCode == Screen.KEY_RIGHT) {
			if (interactive && (curValue < maxValue)) {
				curValue++;
			}
			repaint();
		} else if (keyCode == Screen.KEY_LEFT) {
			if (interactive && (curValue > 0)) {
				curValue--;
			}
			repaint();
		} else if ((keyCode == Screen.KEY_BACK) || (keyCode == Screen.KEY_ENTER)) {
			notifyStateChanged();
		}
	}

	public void paint(Graphics g, int x, int y, int w, int h, boolean selected) {
		int barWidth = w / maxValue;
		int barIncr = h / maxValue;
		int barOffset = 0;
		
		if (label != null) {
			g.drawString(label, x, y, 0, selected);
			
			if (h > Display.CHAR_HEIGHT) {
				barIncr = (h - Display.CHAR_HEIGHT) / maxValue;
			} else {
				barOffset = (label.length() * Display.CHAR_WIDTH);
				barWidth = (w - barOffset) / maxValue;
			}
		}

		for (int i = 0; i < curValue; i++) {
			int barHeight = (i + 1) * barIncr;
			g.fillRect(barOffset + (i * barWidth), y + h - barHeight, (barWidth >> 1), barHeight);
		}
	}
}
