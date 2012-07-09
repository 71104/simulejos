package javax.microedition.lcdui;

import java.util.ArrayList;

/**
 * 
 * @author Andre Nijholt
 */
public abstract class Item {
	protected String label;
	protected int minWidth;
	protected int minHeight;
	protected int prefWidth;
	protected int prefHeight;
	
	protected boolean interactive = false;
	
	protected ItemCommandListener cmdListener;
	protected ArrayList<Command> commands = new ArrayList<Command>();

	private boolean paintRequest;

	void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	public int getMinimumHeight() {
		return minHeight;
	}
	
	public int getMinimumWidth() {
		return minWidth;
	}
	
	public int getPreferredHeight() {
		return prefHeight;
	}
	
	public int getPreferredWidth() {
		return prefWidth;
	}
	
	public void setPreferredSize(int width, int height) {
		this.prefWidth = width;
		this.prefHeight = height;
	}
	
	public boolean isInteractive() {
		return interactive;
	}
	
	public void notifyStateChanged() {
		// Notify ItemStateListener
		for (int i = 0; (i < commands.size()) && (cmdListener != null); i++) {
			cmdListener.commandAction(commands.get(i), this);
		}
	}

	public void addCommand(Command cmd) {
		commands.add(cmd);
	}

	public void removeCommand(Command cmd) {
		commands.remove(commands.indexOf(cmd));
	}
	
	public void setItemCommandListener(ItemCommandListener l) {
		cmdListener = l;
	}
	
//	protected void callItemCommandListener() {
//		for (int i = 0; (i < commands.size()) && (cmdListener != null); i++) {
//			cmdListener.commandAction((Command) commands.get(i), this);
//		}
//	}
	
	public boolean getPaintRequest() {
		return paintRequest;
	}

	protected void repaint() {
		paintRequest = true;
	}

	protected void keyPressed(int keyCode) {}
	protected void keyReleased(int keyCode) {}
	
	protected  void showNotify() {}
	protected  void hideNotify() {}
	 
	protected abstract void paint(Graphics g, int x, int y, int w, int h, boolean selected);
}
