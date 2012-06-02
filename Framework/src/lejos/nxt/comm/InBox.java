package lejos.nxt.comm;

import java.util.ArrayList;

/**
 * Represents an LCP Inbox
 * 
 * @author Lawrie Griffiths
 *
 */
public class InBox extends ArrayList<String> {
	
	// Allow a message in the queue to be updated, or added 
	public synchronized void updateMessage(String key, String msg) {
		for (int i=0;i<this.size();i++) {
			String s = this.get(i);
			if (s.startsWith(key)) {
				this.set(i, msg);
				return;
			}
		}
		this.add(msg);
	}
}
