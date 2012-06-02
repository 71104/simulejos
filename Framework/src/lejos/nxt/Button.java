package lejos.nxt;

/**
 * Abstraction for an NXT button.
 * Example:
 * <pre>
 *   Button.ENTER.waitForPressAndRelease();
 *   Sound.playTone (1000, 1);
 * </pre>
 * 
 * <b>Notions:</b>
 * The API is designed around two notions: states (up / down) and events (press / release).
 * It is said that a button is pressed (press event), if its state changes from up to down.
 * Similarly, it is said that a button is released (release event), if its states changed from down to up.
 * 
 * <b>Thread Safety</b>:
 * All methods that return buttons states can be used safely from multiple threads, even while
 * a call to one of the waitFor* methods active. However, it is not safe to invoke waitFor* methods
 * in parallel from different threads. This includes the waitFor* methods of different buttons.
 * For example Button.ENTER.waitForPress() must not be invoked in parallel to Button.ESCAPE.waitForPress()
 * or the static Button.waitForAnyEvent(). In case this is needed, it is strongly recommended that you write
 * your own Thread, which waits for button events and dispatches the events to anyone who's interested.
 */
public class Button implements ListenerCaller
{
  public static final int ID_ENTER = 0x1;
  public static final int ID_LEFT = 0x2;
  public static final int ID_RIGHT = 0x4;
  public static final int ID_ESCAPE = 0x8;
  private static final int ID_ALL = 0xf;
  
  private static final int PRESS_EVENT_SHIFT = 0;
  private static final int RELEASE_EVENT_SHIFT = 8;
  private static final int WAITFOR_RELEASE_SHIFT = 8;
  
  public static final String VOL_SETTING = "lejos.keyclick_volume";
  
  /**
   * The Enter button.
   */
  public static final Button ENTER = new Button (ID_ENTER);
  /**
   * The Left button.
   */
  public static final Button LEFT = new Button (ID_LEFT);
  /**
   * The Right button.
   */
  public static final Button RIGHT = new Button (ID_RIGHT);
  /**
   * The Escape button.
   */
  public static final Button ESCAPE = new Button (ID_ESCAPE);
  
	
  /**
   * Array containing ENTER, LEFT, RIGHT, ESCAPE, in that order.
   * @deprecated this array will be removed
   */
  @Deprecated
  public static final Button[] BUTTONS = { Button.ENTER, Button.LEFT, Button.RIGHT, Button.ESCAPE };

  // protected by Button.class monitor
  private static int [] clickFreq;
  private static int clickVol;
  private static int clickLen;
  private static int curButtonsS;
  // not protected by any monitor
  private static int curButtonsE;

  
  private final int iCode;
  private ButtonListener[] iListeners;
  private int iNumListeners;

	/**
	 * Static constructor to force loading of system settings.
	 */
	static
	{
		loadSystemSettings();
		// Initialize with state of buttons instead of zero. That prevents false
		// press events at program start
		curButtonsE = curButtonsS = getButtons();
	}

	private Button(int aCode)
	{
		iCode = aCode;
	}

  /**
   * Return the ID of the button. One of 1, 2, 4 or 8.
   * @return the button Id
   */
  public final int getId()
  {
    return iCode;
  }
    
	/**
	 * @deprecated use {@link #isDown()} instead.
	 */
	@Deprecated
	public final boolean isPressed()
	{
		return isDown();
	}

	/**
	 * Check if the current state of the button is down.
	 * 
	 * @return <code>true</code> if button is down, <code>false</code> if up.
	 */
	public final boolean isDown()
	{
		return (readButtons() & iCode) != 0;
	}

	/**
	 * Check if the current state of the button is up.
	 * 
	 * @return <code>true</code> if button is down, <code>false</code> if up.
	 */
	public final boolean isUp()
	{
		return (readButtons() & iCode) == 0;
	}

	/**
	 * Wait until the button is released.
	 */
	public final void waitForPress() {
		while ((Button.waitForAnyPress(0) & iCode) == 0)
		{
			// wait for next press
		}
	}

	/**
	 * Wait until the button is released.
	 */
	public final void waitForPressAndRelease() {
		this.waitForPress();
		int tmp = iCode << WAITFOR_RELEASE_SHIFT;
		while ((Button.waitForAnyEvent(0) & tmp) == 0)
		{
			// wait for next event
		}
	}

	/**
	 * This method discards and events.
	 * In contrast to {@link #readButtons()}, this method doesn't beep if a button is pressed.
	 */
	public static void discardEvents()
	{
		curButtonsE = getButtons();
	}
  
	/**
	 * Waits for some button to be pressed or released.
	 * Which buttons have been released or pressed is returned as a bitmask.
	 * The lower eight bits (bits 0 to 7) indicate, which buttons have been pressed.
	 * Bits 8 to 15 indicate which buttons have neem released.
	 * 
	 * @param timeout The maximum number of milliseconds to wait
	 * @return the bitmask 
	 * @see #ID_ENTER
	 * @see #ID_LEFT
	 * @see #ID_RIGHT
	 * @see #ID_ESCAPE
	 */
	public static int waitForAnyEvent(int timeout) {
		long end = (timeout == 0 ? 0x7fffffffffffffffL : System.currentTimeMillis() + timeout);
		NXTEvent event = NXTEvent.allocate(NXTEvent.BUTTONS, 0, 10);
		try
		{
			int oldDown = curButtonsE;
			while (true)
			{
				long curTime = System.currentTimeMillis();
				if (curTime >= end)
					return 0;
				
				event.waitEvent((oldDown << RELEASE_EVENT_SHIFT) | ((ID_ALL ^ oldDown) << PRESS_EVENT_SHIFT),
						end - curTime);
				int newDown = curButtonsE = readButtons();
				if (newDown != oldDown)
					return ((oldDown & (~newDown)) << WAITFOR_RELEASE_SHIFT) | (newDown & (~oldDown)); 
			}
		}
		catch(InterruptedException e)
		{
		    // TODO: Need to decide how to handle this properly
            // preserve state of interrupt flag
            Thread.currentThread().interrupt();
            return 0;
		}
		finally
		{
			event.free();
		}
	}

	/**
	 * Waits for some button to be pressed. If a button is already pressed, it
	 * must be released and pressed again.
	 * 
	 * @param timeout The maximum number of milliseconds to wait
	 * @return the ID of the button that has been pressed or in rare cases a bitmask of button IDs,
	 *         0 if the given timeout is reached 
	 */
	public static int waitForAnyPress(int timeout) {
		long end = (timeout == 0 ? 0x7fffffffffffffffL : System.currentTimeMillis() + timeout);
		NXTEvent event = NXTEvent.allocate(NXTEvent.BUTTONS, 0, 10);
		try
		{
			int oldDown = curButtonsE;
			while (true)
			{
				long curTime = System.currentTimeMillis();
				if (curTime >= end)
					return 0;
				
				event.waitEvent((oldDown << RELEASE_EVENT_SHIFT) | ((ID_ALL ^ oldDown) << PRESS_EVENT_SHIFT),
						end - curTime);
				int newDown = curButtonsE = readButtons();
				int pressed = newDown & (~oldDown);
				if (pressed != 0)
					return pressed;
				
				oldDown = newDown;
			}
		}
		catch(InterruptedException e)
		{
		    // TODO: Need to decide how to handle this properly
            // preserve state of interrupt flag
            Thread.currentThread().interrupt();
            return 0;
		}
		finally
		{
			event.free();
		}
	}

	/**
	 * Waits for some button to be pressed. If a button is already pressed, it
	 * must be released and pressed again.
	 * 
	 * @return the ID of the button that has been pressed or in rare cases a bitmask of button IDs
	 */
	public static int waitForAnyPress() {
		return waitForAnyPress(0);
	}
  
  /**
   * Adds a listener of button events. Each button can serve at most
   * 4 listeners.
   * @param aListener The new listener
   */
  public synchronized void addButtonListener (ButtonListener aListener)
  {
    if (iListeners == null)
    {
      iListeners = new ButtonListener[4];
      ListenerThread.get().addListener(NXTEvent.BUTTONS, iCode << (isDown() ? RELEASE_EVENT_SHIFT : PRESS_EVENT_SHIFT), 10, this);
      
    }
    iListeners[iNumListeners++] = aListener;
  }
  
	/**
	 * <i>Low-level API</i> that reads status of buttons.
	 * 
	 * @return An integer with possibly some bits set: {@link #ID_ENTER} (ENTER
	 *         button pressed) {@link #ID_LEFT} (LEFT button pressed),
	 *         {@link #ID_RIGHT} (RIGHT button pressed), {@link #ID_ESCAPE}
	 *         (ESCAPE button pressed). If all buttons are released, this method
	 *         returns 0.
	 */
	private static native int getButtons();

	/**
	 * <i>Low-level API</i> that reads status of buttons.
	 * 
	 * @return An integer with possibly some bits set: {@link #ID_ENTER} (ENTER
	 *         button pressed) {@link #ID_LEFT} (LEFT button pressed),
	 *         {@link #ID_RIGHT} (RIGHT button pressed), {@link #ID_ESCAPE}
	 *         (ESCAPE button pressed). If all buttons are released, this method
	 *         returns 0.
	 */
	public synchronized static int readButtons()
	{
		int newButtons = getButtons();
		int pressed = newButtons & (~curButtonsS);
		curButtonsS = newButtons;
		if (pressed != 0 && clickVol != 0)
		{
			int tone = clickFreq[pressed];
			if (tone != 0)
				Sound.playTone(tone, clickLen, -clickVol);
		}
		return newButtons;
	}

  /**
   * Call Button Listeners. Used by ListenerThread.
   * @return New event filter
   */
  public synchronized int callListeners()
  {
    boolean pressed = isDown();
    for( int i = 0; i < iNumListeners; i++) {
      if(pressed)
        iListeners[i].buttonPressed( this);
      else
        iListeners[i].buttonReleased( this);
    }
    return iCode << (pressed ? RELEASE_EVENT_SHIFT : PRESS_EVENT_SHIFT);
  }
  
  /**
   * Set the volume used for key clicks
   * @param vol
   */
  public static void setKeyClickVolume(int vol)
  {
      clickVol = vol;
  }
  
  /**
   * Return the current key click volume.
   * @return current click volume
   */
  public static synchronized int getKeyClickVolume()
  {
      return clickVol;
  }
  
  /**
   * Set the len used for key clicks
   * @param len the click duration
   */
  public static synchronized void setKeyClickLength(int len)
  {
      clickLen = len;
  }
  
  /**
   * Return the current key click length.
   * @return key click duration
   */
  public static synchronized int getKeyClickLength()
  {
      return clickLen;
  }
  
  /**
   * Set the frequency used for a particular key. Setting this to 0 disables
   * the click. Note that key may also be a corded set of keys.
   * @param key the NXT key
   * @param freq the frequency
   */
  public static synchronized void setKeyClickTone(int key, int freq)
  {
      clickFreq[key] = freq;
  }
  
  /**
   * Return the click freq for a particular key.
   * @param key The key to obtain the tone for
   * @return key click duration
   */
  public static synchronized int getKeyClickTone(int key)
  {
      return clickFreq[key];
  }
  

  /**
   * @deprecated replaced by {@link #loadSystemSettings()}.
   */
  @Deprecated
  public static void loadSettings()
  {
	  loadSystemSettings();
  }
  
  /**
   * Load the current system settings associated with this class. Called
   * automatically to initialize the class. May be called if it is required
   * to reload any settings.
  */
  public static synchronized void loadSystemSettings()
  {
      clickVol = SystemSettings.getIntSetting(VOL_SETTING, 20);
      clickLen = 50;
      // setup default tones for the keys and enter+key chords
      clickFreq = new int[16];
      clickFreq[ID_ENTER] = 209 + 697;
      clickFreq[ID_LEFT] = 209 + 770;
      clickFreq[ID_RIGHT] = 209 + 852;
      clickFreq[ID_ESCAPE] = 209 + 941;
      clickFreq[ID_ENTER | ID_LEFT] = 633 + 770;
      clickFreq[ID_ENTER | ID_RIGHT] = 633 + 852;
      clickFreq[ID_ENTER | ID_ESCAPE] = 633 + 941;
  }  
}
