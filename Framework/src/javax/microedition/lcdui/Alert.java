package javax.microedition.lcdui;

import lejos.nxt.Sound;

/**
 * 
 * @author Andre Nijholt
 */
public class Alert extends Screen {
	/** Default command for alert */
	private final Command DISMISS_COMMAND = new Command(0, Command.SCREEN, 0);

	public static final int FOREVER					= -2;

	public static final int ALERT_TYPE_INFO			= 0;
	public static final int ALERT_TYPE_WARNING		= 1;
	public static final int ALERT_TYPE_ERROR		= 2;
	public static final int ALERT_TYPE_ALARM		= 3;
	public static final int ALERT_TYPE_CONFIRMATION	= 4;
	
	public static final String STR_CONFIRM = "Yes";
	public static final String STR_DENY = "No";
	
	public static final Image IMG_INFO = new Image(16, 16, new byte[] {
		(byte) 0xe0, (byte) 0x10, (byte) 0x0c, (byte) 0x04, (byte) 0x02, (byte) 0x01, (byte) 0x49, (byte) 0xdd,
		(byte) 0xdd, (byte) 0x49, (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x0c, (byte) 0x10, (byte) 0xe0,	
		(byte) 0x07, (byte) 0x08, (byte) 0x30, (byte) 0x20, (byte) 0x40, (byte) 0x80, (byte) 0xa0, (byte) 0xbf,
		(byte) 0xbf, (byte) 0xa0, (byte) 0x80, (byte) 0x40, (byte) 0x20, (byte) 0x30, (byte) 0x08, (byte) 0x07
	});
	
	public static final Image IMG_WARNING = new Image(16, 16, new byte[] {
		(byte) 0xe0, (byte) 0x10, (byte) 0x0c, (byte) 0x04, (byte) 0x02, (byte) 0x01, (byte) 0x7d, (byte) 0xfd,
		(byte) 0xfd, (byte) 0x7d, (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x0c, (byte) 0x10, (byte) 0xe0,
		(byte) 0x07, (byte) 0x08, (byte) 0x30, (byte) 0x20, (byte) 0x40, (byte) 0x80, (byte) 0x90, (byte) 0xbb,
		(byte) 0xbb, (byte) 0x90, (byte) 0x80, (byte) 0x40, (byte) 0x20, (byte) 0x30, (byte) 0x08, (byte) 0x07
	});

	public static final Image IMG_ERROR = new Image(16, 16, new byte[] {
		(byte) 0xe0, (byte) 0x10, (byte) 0x0c, (byte) 0x04, (byte) 0x12, (byte) 0x39, (byte) 0x61, (byte) 0xc1,
		(byte) 0xc1, (byte) 0x61, (byte) 0x39, (byte) 0x12, (byte) 0x04, (byte) 0x0c, (byte) 0x10, (byte) 0xe0,
		(byte) 0x07, (byte) 0x08, (byte) 0x30, (byte) 0x20, (byte) 0x48, (byte) 0x9c, (byte) 0x86, (byte) 0x83,
		(byte) 0x83, (byte) 0x86, (byte) 0x9c, (byte) 0x48, (byte) 0x20, (byte) 0x30, (byte) 0x08, (byte) 0x07
	});
	
	public static final Image IMG_ALARM = new Image(16, 16, new byte[] {
		(byte) 0xe0, (byte) 0x10, (byte) 0x0c, (byte) 0x04, (byte) 0xf2, (byte) 0xf9, (byte) 0xfd, (byte) 0xfd,
		(byte) 0xfd, (byte) 0xfd, (byte) 0xf9, (byte) 0xf2, (byte) 0x04, (byte) 0x0c, (byte) 0x10, (byte) 0xe0,	
		(byte) 0x07, (byte) 0x08, (byte) 0x30, (byte) 0x24, (byte) 0x47, (byte) 0x87, (byte) 0x8f, (byte) 0x9f,
		(byte) 0x9f, (byte) 0x8f, (byte) 0x87, (byte) 0x47, (byte) 0x24, (byte) 0x30, (byte) 0x08, (byte) 0x07
	});

	public static final Image IMG_CONFIRM = new Image(16, 16, new byte[] {
		(byte) 0xe0, (byte) 0x10, (byte) 0x0c, (byte) 0x04, (byte) 0x12, (byte) 0x39, (byte) 0x0d, (byte) 0x0d,
		(byte) 0x8d, (byte) 0xcd, (byte) 0x79, (byte) 0x32, (byte) 0x04, (byte) 0x0c, (byte) 0x10, (byte) 0xe0,
		(byte) 0x07, (byte) 0x08, (byte) 0x30, (byte) 0x20, (byte) 0x40, (byte) 0x80, (byte) 0x90, (byte) 0xbb,
		(byte) 0xbb, (byte) 0x90, (byte) 0x80, (byte) 0x40, (byte) 0x20, (byte) 0x30, (byte) 0x08, (byte) 0x07
	});
	static Image[] icons = new Image [] {IMG_INFO, IMG_WARNING, IMG_ERROR, IMG_ALARM, IMG_CONFIRM};
	String text;
	Image image;
	Gauge gauge;
	int type;
	int time;
	boolean confirm = false;
	
	public Alert(String title) {
		this.title = title;
		this.time = FOREVER;
		commands.add(DISMISS_COMMAND);
	}

	public Alert(String title, String alertText, Image alertImage, int alertType) {
		this.title = title;
		this.text = alertText;
		this.image = alertImage;
		this.type = alertType;
		this.time = FOREVER;
		commands.add(DISMISS_COMMAND);
	}
	
	public void setType(int alertType) {
		this.type = alertType;
	}
	
	public void setString(String alertText) {
		this.text = alertText;
	}

	public void setTimeout(int time) {
		this.time = time;
	}
	
	public int getTimeout() {
		return time;
	}
	
	public boolean getConfirmation() {
		return confirm;
	}
	
	public void setIndicator(Gauge indicator) {
		// Check conditions for use of indicator
		if (indicator.isInteractive() || (indicator.label != null)) {
			throw new IllegalArgumentException();
		}
		this.gauge = indicator;
	}
	
	public Gauge getIndicator() {
		return gauge;
	}
	
	protected void keyPressed(int keyCode) {
		if ((keyCode == KEY_ENTER) && (cmdListener != null)) {
			cmdListener.commandAction(DISMISS_COMMAND, this);
		} else if (type == ALERT_TYPE_CONFIRMATION) {
			if (keyCode == KEY_LEFT) {
				confirm = false;
			} else if (keyCode == KEY_RIGHT) {
				confirm = true;
			}
			repaint();
		}
	}

	protected void showNotify() {
		// Play notification sound
		new Thread() {
			public void run() {
				Sound.beepSequenceUp();
			}
		}.start();
		
		// Start painting alert screen
		repaint();
	}

	public void paint(Graphics g) {
		// Draw frame with title (roundrect is very slow)
//		g.drawRoundRect(0, 0, 98, 63, 45, 45);
//		g.fillArc(0, 0, 34, 34, 90, 90);
//		g.fillArc(64, 0, 34, 34, 0, 90);
//		g.fillRect(16, 0, 66, 18);
		int line = Display.CHAR_HEIGHT;
		int ch = Display.CHAR_WIDTH;
		g.fillRect(0, 0, 100, 16);
		// Use special rop to create a "gray" banner.
		//g.drawImage(null, 0, 0, 0, 0, 100, 16, 0x55);
		g.drawString(title, Display.SCREEN_WIDTH/2, line/2, Graphics.HCENTER, true);
		if (this.image != null) {
			// Draw user defined image
			g.drawImage(image, 0, 20, 0);
		} else {
			g.drawImage(icons[this.type], 0, 20, 0);
		}
		
		
		// Draw centered text
		g.drawString(text, 3*ch, 3*line, 0);
		if (type == ALERT_TYPE_CONFIRMATION) {
			g.drawString(confirm ? STR_CONFIRM : STR_DENY, Display.SCREEN_WIDTH/2, 4*line, Graphics.HCENTER, true);
		} else if (gauge != null) {
			gauge.paint(g, 0, 32, 100, 32, false);
		}
	}
}
