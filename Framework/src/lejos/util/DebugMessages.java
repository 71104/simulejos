package lejos.util;

import it.uniroma1.di.simulejos.bridge.Bridge;

public class DebugMessages {
	private int delay = 250;
	private boolean delayEnabled = false;

	public DebugMessages() {
	}

	public DebugMessages(int init) {
	}

	public void setLCDLines(int lines) {
	}

	public void setDelayEnabled(boolean enabled) {
		delayEnabled = enabled;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	public void echo(String message) {
		Bridge.getLog().println(message);
		if (delayEnabled) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void echo(int message) {
		Bridge.getLog().println(message);
		if (delayEnabled) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void clear() {
	}
}
