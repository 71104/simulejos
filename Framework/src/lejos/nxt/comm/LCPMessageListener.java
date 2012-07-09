package lejos.nxt.comm;

public interface LCPMessageListener {
	
	public void messageReceived(byte inBox, String message);
}
