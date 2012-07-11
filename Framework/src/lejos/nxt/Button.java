package lejos.nxt;

import java.util.LinkedList;
import java.util.List;

import it.uniroma1.di.simulejos.bridge.BrickInterface;
import it.uniroma1.di.simulejos.bridge.Bridge;
import it.uniroma1.di.simulejos.bridge.BrickInterface.ButtonListener.Token;
import it.uniroma1.di.simulejos.util.AutoResetEvent;
import it.uniroma1.di.simulejos.util.BooleanAutoResetEvent;

public class Button implements ListenerCaller {
	public static final int ID_ENTER = 0x1;
	public static final int ID_LEFT = 0x2;
	public static final int ID_RIGHT = 0x4;
	public static final int ID_ESCAPE = 0x8;

	public static final String VOL_SETTING = "lejos.keyclick_volume";

	public static final Button ENTER = new Button(ID_ENTER);
	public static final Button LEFT = new Button(ID_LEFT);
	public static final Button RIGHT = new Button(ID_RIGHT);
	public static final Button ESCAPE = new Button(ID_ESCAPE);

	@Deprecated
	public static final Button[] BUTTONS = { Button.ENTER, Button.LEFT,
			Button.RIGHT, Button.ESCAPE };

	private static volatile int[] clickFreq;
	private static volatile int clickVol;
	private static volatile int clickLen;

	static {
		loadSystemSettings();
	}

	private static final AutoResetEvent<Integer> anyPress = new AutoResetEvent<Integer>();
	private static final AutoResetEvent<Integer> anyRelease = new AutoResetEvent<Integer>();
	private static final AutoResetEvent<Integer> anyEvent = new AutoResetEvent<Integer>();

	private final int id;
	private final BooleanAutoResetEvent pressEvent = new BooleanAutoResetEvent();
	private final BooleanAutoResetEvent releaseEvent = new BooleanAutoResetEvent();
	private final Token listenerToken = Bridge.getBrick().addButtonListener(
			new BrickInterface.ButtonListener() {
				@Override
				public void onPress(int buttonIndex) {
					if ((1 << buttonIndex) == id) {
						pressEvent.notifyEvent();
						for (ButtonListener listener : listeners) {
							listener.buttonPressed(Button.this);
						}
						anyPress.notifyEvent(1 << buttonIndex);
						anyEvent.notifyEvent(1 << buttonIndex);
					}
				}

				@Override
				public void onRelease(int buttonIndex) {
					if ((1 << buttonIndex) == id) {
						releaseEvent.notifyEvent();
						for (ButtonListener listener : listeners) {
							listener.buttonReleased(Button.this);
						}
						anyRelease.notifyEvent(1 << (buttonIndex + 8));
						anyEvent.notifyEvent(1 << (buttonIndex + 8));
					}
				}
			});

	private final List<ButtonListener> listeners = new LinkedList<ButtonListener>();

	private Button(int id) {
		this.id = id;
	}

	@Override
	protected void finalize() {
		Bridge.getBrick().removeButtonListener(listenerToken);
	}

	public final int getId() {
		return id;
	}

	@Deprecated
	public final boolean isPressed() {
		return isDown();
	}

	public final boolean isDown() {
		return (readButtons() & id) != 0;
	}

	public final boolean isUp() {
		return (readButtons() & id) == 0;
	}

	public final void waitForPress() {
		pressEvent.waitEvent();
	}

	public final void waitForPressAndRelease() {
		releaseEvent.waitEvent();
	}

	public static void discardEvents() {
	}

	public static int waitForAnyEvent(int timeout) {
		return anyEvent.waitEvent(timeout);
	}

	public static int waitForAnyPress(int timeout) {
		return anyPress.waitEvent(timeout);
	}

	public static int waitForAnyPress() {
		return anyPress.waitEvent();
	}

	public void addButtonListener(ButtonListener listener) {
		synchronized (listeners) {
			if (listeners.size() < 4) {
				listeners.add(listener);
			} else {
				throw new IllegalStateException(
						"Lejos buttons can handle at most 4 listeners");
			}
		}
	}

	public static int readButtons() {
		return Bridge.getBrick().readButtons();
	}

	public int callListeners() {
		if (isDown()) {
			for (ButtonListener listener : listeners) {
				listener.buttonPressed(this);
			}
			return id;
		} else {
			for (ButtonListener listener : listeners) {
				listener.buttonReleased(this);
			}
			return id << 8;
		}
	}

	public static void setKeyClickVolume(int vol) {
		clickVol = vol;
	}

	public static int getKeyClickVolume() {
		return clickVol;
	}

	public static void setKeyClickLength(int len) {
		clickLen = len;
	}

	public static int getKeyClickLength() {
		return clickLen;
	}

	public static void setKeyClickTone(int key, int freq) {
		clickFreq[key] = freq;
	}

	public static int getKeyClickTone(int key) {
		return clickFreq[key];
	}

	@Deprecated
	public static void loadSettings() {
		loadSystemSettings();
	}

	public static void loadSystemSettings() {
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
