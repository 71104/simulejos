package javax.microedition.lcdui;

/**
 * 
 * @author Andre Nijholt
 */
public class Command {
	public static final int SCREEN 	= 1;
	public static final int BACK 	= 2;
	public static final int CANCEL 	= 3;
	public static final int OK 		= 4;
	public static final int HELP	= 5;
	public static final int STOP	= 6;
	public static final int EXIT	= 7;
	public static final int ITEM	= 8;

	private int commandId;
	private String longLabel;
	private int commandType;
	private int priority;
	
	public Command(int commandId, int commandType, int priority) {
		this.commandId = commandId;
		this.commandType = commandType;
		this.priority = priority;
	}
	
	public Command(int commandId, String longLabel, int commandType, int priority) {
		this.commandId = commandId;
		this.longLabel = longLabel;
		this.commandType = commandType;
		this.priority = priority;
	}
	
	public int getCommandType() {
		return commandType;
	}
	
	public int getCommandId() {
		return commandId;
	}
	
	public String getLongLabel() {
		return longLabel;
	}
	
	public int getPriority() {
		return priority;
	}
}
