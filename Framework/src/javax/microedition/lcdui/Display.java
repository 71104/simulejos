package javax.microedition.lcdui;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.util.Timer;
import lejos.util.TimerListener;
import lejos.util.Delay;
import lejos.nxt.LCD;

/**
 * 
 * @author Andre Nijholt
 */
public class Display {
	private static final int TICKER_INTERVAL_MSEC = 100;

	public static final int SCREEN_WIDTH 	= 100;
	public static final int SCREEN_HEIGHT 	= 64;

	public static final int CHAR_WIDTH 		= 6;
	public static final int CHAR_HEIGHT 	= 8;

	public static final int SCREEN_CHAR_WIDTH = SCREEN_WIDTH / CHAR_WIDTH;
	public static final int SCREEN_CHAR_DEPTH = CHAR_HEIGHT;

	private static Display display;
	
	private Screen current;
	private Screen alertBackup;
	private int alertTimeout;
	
	private Timer tickerTimer;
	private int tickerOffset = SCREEN_WIDTH;
	
	protected Graphics graphics;
	private boolean quit;
	
	private Display() {
		graphics = new Graphics();
		// We control when the screen gets updated explicitly
		LCD.setAutoRefresh(false);
	}
	
	public static Display getDisplay() {
		if (display == null) {
			display = new Display();
		}
		
		return display;
	}
	
	public void setCurrent(Screen nextDisplayable) {
		if (nextDisplayable != null) {
			if (nextDisplayable instanceof Alert) {
				// Only allow one level of alert
				if (alertBackup == null)
					alertBackup = current;				
				alertTimeout = (((Alert) nextDisplayable).getTimeout() == Alert.FOREVER) ? Alert.FOREVER
					: ((int) System.currentTimeMillis()) + ((Alert) nextDisplayable).getTimeout();
			}
			if (current != null) {
				current.hideNotify();
			}
			
			tickerOffset = SCREEN_WIDTH;
			current = nextDisplayable;
			current.showNotify();
			current.repaint();
		}
	}
	
	public void setCurrent(Alert alert, Screen nextDisplayable) {
		if ((alert != null) && (nextDisplayable != null)) {
			alertBackup = nextDisplayable;
			if (current != null) {
				current.hideNotify();
			}

			// Store timeout for alert screen
			alertTimeout = (alert.getTimeout() == Alert.FOREVER) ? Alert.FOREVER
					: ((int) System.currentTimeMillis()) + alert.getTimeout();
			
			tickerOffset = SCREEN_WIDTH;
			current = alert;
			current.showNotify();
			current.repaint();
		}
	}
	
	public Displayable getCurrent() {
		return current;
	}
	
	/**
	 * Start displaying the installed menus
	 * 
	 * @param polling true to poll for button updates (recommended)
	 */
	public void show(boolean polling) {
		if (!polling) {
			// Use non-polling implementation
			showUsingListeners();
			return;
		}

		int btnPressed;
		int tsTickerUpdate = (int) System.currentTimeMillis();
		while (!quit) {
			// Wait for button release
			while (!quit && (Button.readButtons() > 0)) {
				Thread.yield();
			}
			// Wait for button pressed and handle main timer checks
			while (!quit && (Button.readButtons() == 0)) {
				if (msecPassed(tsTickerUpdate)) {
		    		int tickerLen = updateTicker(tickerOffset);
		    		if (tickerLen > 0) {
		    			tickerOffset--;
		    			if (tickerOffset < -tickerLen) {
		    				tickerOffset = SCREEN_WIDTH;
		    			}
		    		}
		    		tsTickerUpdate = (int) System.currentTimeMillis() + TICKER_INTERVAL_MSEC;
				}
				
				if ((current != null) && (current instanceof Alert)
						&& (alertTimeout != Alert.FOREVER) && msecPassed(alertTimeout)) {
    				// Hide alert screen and replace backup without notify
    				current.hideNotify();
    				current = alertBackup;
					alertBackup = null;
				}

				// Handle repaint requests from outside
				update();
				Thread.yield();
			}
			// Check if menu to handle
			if (current == null) {
				continue;
			}
			
			// Debounce button press
            Delay.msDelay(20);
			
			// Handle debounced button press
			btnPressed = Button.readButtons();
			if (btnPressed == 1) {
				// ENTER button pressed
    			if (current instanceof Alert) {
    				// Hide alert screen and replace backup without notify
					Screen saved = current;
     				current.keyPressed(Displayable.KEY_ENTER);
					// Make sure that we are still current!
					if (saved == current)
					{
						current.hideNotify();
						current = alertBackup;
						alertBackup = null;
					}
    			} else {
    				current.keyPressed(Displayable.KEY_ENTER);
    			}
    			update();
			} else if (btnPressed == 2) {
				// LEFT button pressed
    			current.keyPressed(Displayable.KEY_LEFT);
    			update();
			} else if (btnPressed == 4) {
				// RIGHT button pressed
    			current.keyPressed(Displayable.KEY_RIGHT);
    			update();
			} else if (btnPressed == 8) {
				// ESCAPE button pressed
    			current.keyPressed(Displayable.KEY_BACK);
    			update();
			}
		}
		
		// End application
		System.exit(0);
	}
	
	public void quit() {
		quit = true;
	}
	
	private void showUsingListeners() {
	    Button.ENTER.addButtonListener(new ButtonListener() {
	    	public void buttonReleased (Button b) {}
	    	public void buttonPressed (Button b) {
	    		if (current != null) {
	    			if (current instanceof Alert) {
						// Hide alert screen and replace backup without notify
						Screen saved = current;
						current.keyPressed(Displayable.KEY_ENTER);
						// Make sure that we are still current!
						if (saved == current)
						{
							current.hideNotify();
							current = alertBackup;
							alertBackup = null;
						}
	    			} else {
	    				current.keyPressed(Displayable.KEY_ENTER);
	    			}
	    			update();
	    		}
	    	}
	    });
	    Button.ESCAPE.addButtonListener(new ButtonListener() {
	    	public void buttonReleased (Button b) {}
	    	public void buttonPressed (Button b) {
	    		if (current != null) {
	    			current.keyPressed(Displayable.KEY_BACK);
	    			update();
	    		}
	    	}
	    });
	    Button.LEFT.addButtonListener(new ButtonListener() {
	    	public void buttonReleased (Button b) {}
	    	public void buttonPressed (Button b) {
	    		if (current != null) {
	    			current.keyPressed(Displayable.KEY_LEFT);
	    			update();
	    		}
	    	}
	    });
	    Button.RIGHT.addButtonListener(new ButtonListener() {
	    	public void buttonReleased (Button b) {}
	    	public void buttonPressed (Button b) {
	    		if (current != null) {
	    			current.keyPressed(Displayable.KEY_RIGHT);
	    			update();
	    		}
	    	}
	    });

	    tickerTimer = new Timer(TICKER_INTERVAL_MSEC, new TimerListener() {
	    	public void timedOut() {
	    		int tickerLen = updateTicker(tickerOffset);
	    		if (tickerLen > 0) {
	    			tickerOffset--;
	    			if (tickerOffset < -tickerLen) {
	    				tickerOffset = SCREEN_WIDTH;
	    			}
	    		}
	    	}
	    });
	    tickerTimer.start();
	    
	    // Start update timer task
	    new Timer(50, new TimerListener() {
	    	public void timedOut() {
				if ((current != null) && (current instanceof Alert)
						&& (alertTimeout != Alert.FOREVER) && msecPassed(alertTimeout)) {
    				// Hide alert screen and replace backup without notify
    				current.hideNotify();
    				current = alertBackup;
					alertBackup = null;
				}
				
				if (quit) {
					System.exit(0);
				}

	    		update();
	    	}
	    }).start();
	}
	
	private synchronized void update() {
		if (current.getPaintRequest()) {
			LCD.clear();
			updateTicker(tickerOffset);
			
			current.paint(graphics);
			current.clearPaintRequest();
			LCD.refresh();
		}
	}
	
	private int updateTicker(int offset) {
		Ticker ticker = current.getTicker();
		if (ticker != null) {
			int old = graphics.getColor();
			graphics.setColor(Graphics.WHITE);
			graphics.fillRect(0, 0, SCREEN_WIDTH, CHAR_HEIGHT);
			graphics.setColor(old);
			graphics.drawString(ticker.getString(), offset, 0, 0);
			/*
			int tickerLen = ticker.getString().length();
			for (int i = 0; i < SCREEN_CHAR_WIDTH; i++) {
				if ((i >= offset) && ((i - offset) < tickerLen)) {
					graphics.drawChar(ticker.getString().charAt(i - offset), 
						i * Display.CHAR_WIDTH, 0, false);
				} else {
					graphics.drawChar(' ', i * Display.CHAR_WIDTH, 0, false);
				}
			}*/
			LCD.refresh();
			return ticker.getString().length()*CHAR_WIDTH;
		}
		
		return 0;
	}
	
	/**
	 * Returns true if the given timestamp passed
	 * 
	 * @param ts Timestamp in milliseconds
	 * @return true if ts already passed, otherwise false
	 */
	private boolean msecPassed(int ts) {
		return (((int) System.currentTimeMillis() - ts) > 0);
	}
}
