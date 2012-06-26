package javax.microedition.lcdui;

import java.util.ArrayList;

/**
 * 
 * @author Andre Nijholt
 */
public class Displayable {
	public static final int KEY_LEFT 	= 37;	// Left key
	public static final int KEY_ENTER 	= 38;	// Up key
	public static final int KEY_RIGHT 	= 39;	// Right key
	public static final int KEY_BACK 	= 40;	// Down key

	private boolean paintRequest;

	protected ArrayList<Command> commands = new ArrayList<Command>();
	protected CommandListener cmdListener;

	protected Ticker ticker;
	protected String title;
	protected int height;
	protected int width;
	protected boolean shown;
		
	public int getHeight() {
		return height;
	}
	
	public void setTicker(Ticker ticker) {
		this.ticker = ticker;
	}

	public Ticker getTicker() {
		return ticker;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getWidth() {
		return width;
	}
	
	public boolean isShown() {
		return shown;
	}
	
	public void addCommand(Command cmd) {
		commands.add(cmd);
	}

	public void removeCommand(Command cmd) {
		commands.remove(commands.indexOf(cmd));
	}
	
	public void setCommandListener(CommandListener l) {
		cmdListener = l;
	}
	
	protected void callCommandListener() {
		for (int i = 0; (i < commands.size()) && (cmdListener != null); i++) {
			cmdListener.commandAction(commands.get(i), this);
		}
	}
	
	public void setTicker(int ticker) {
		
	}
	
	public void setTitle(String s) {
		this.title = s;
	}
	
	protected void sizeChanged(int w, int h) {
		width = w;
		height = h;
	}
	
	public boolean getPaintRequest() {
		return paintRequest;
	}
	
	public void clearPaintRequest() {
		paintRequest = false;
	}
	
	public void repaint() {
		paintRequest = true;
	}
}
