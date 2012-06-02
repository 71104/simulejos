package lejos.nxt;

/**
 * This class is designed for use by other lejos classes to
 * read persistent settings. User programs should use the Settings class
 * 
 * @author Lawrie Griffiths
 *
 */
public class SystemSettings {

	public static final int MAX_SETTING_SIZE = 21; 	
	private static final int SETTINGS_PAGE = 0; 
	private static final String CURRENT_VERSION = "NXJ Settings 1.1"; 
	private static final String VERSION_NAME = "settings.version"; 
	private static final int VERSION_SLOT = 0;
	
	// Add to this String array to define new persistent settings.
	// There is a maximum of (256 / MAX_SETTING_SIZE) including the version.
	private static final String[] NAMES = {
		VERSION_NAME, "lejos.volume", "lejos.default_program", "lejos.keyclick_volume",
		"lejos.default_autoRun", "lejos.sleep_time", "lejos.usb_serno", "lejos.usb_name",
		"lejos.bluetooth_pin"
	};
	
	private static byte[] buf = new byte[256];
	
	/**
	 * Read the settings page
	 */
	static {
		Flash.readPage(buf, SETTINGS_PAGE);
		// Intialize page to all zeros and set version in slot 0,
		// if settings not already set up.
		if (!CURRENT_VERSION.equals(getSlotValue(VERSION_SLOT))) {
			for (int i = 0; i < buf.length; i++)
				buf[i] = 0;
			
			setSlotValue(VERSION_SLOT, CURRENT_VERSION);
		}
	}
	
	/**
	 * Get the slot number where a setting is stored
	 * 
	 * @param key the setting name
	 * @return the slot number (0 - 15)
	 *
	 */
	private static int getSlotIndex(String key)
	{
		for(int i= 0;i<NAMES.length;i++)
			if (NAMES[i].equals(key))
				return i;
		
		return -1;
	}
	
	/**
	 * Write the String value of a setting to a slot
	 * 
	 * @param slot the slot (0 - 15)
	 * @param value the String value
	 */
	private static void setSlotValue(int slot, String value) {
		int len = value.length();
		if (len > MAX_SETTING_SIZE)
			throw new IllegalArgumentException("value too large");
		
		for (int i = 0; i < len; i++)
			if (value.charAt(i) > 0xFF)
				throw new IllegalArgumentException("unsupported character");

		int off = slot * MAX_SETTING_SIZE;
		for (int i = 0; i < len; i++)
			buf[off + i] = (byte) value.charAt(i);

		for (int i = len; i < MAX_SETTING_SIZE; i++)
			buf[off + i] = 0;
		
		Flash.writePage(buf, SETTINGS_PAGE);
	}
	
	/**
	 * Get the String value from a slot
	 * 
	 * @param slot the slot number
	 * @return the contents of the slot as a String
	 */
	private static String getSlotValue(int slot) {
		int off = slot * MAX_SETTING_SIZE; 
		int len = 0;
		while (len < MAX_SETTING_SIZE && buf[off + len] != 0)
			len++;
		char[] chars = new char[len];
		for (int i = 0; i < len; i++)
			chars[i] = (char)(buf[off + i] & 0xFF);
		return new String(chars);
	}
	
	/**
	 * Get the value for a leJOS NXJ persistent setting as a String
	 * 
	 * @param key the name of the setting
	 * @param defaultValue the default value
	 * @return the value
	 */
	public static String getStringSetting(String key, String defaultValue) {
		int slot = getSlotIndex(key);
		if (slot < 0)
			return defaultValue;

		String s = getSlotValue(slot);
		if (s.length() == 0)
			return defaultValue;
		
		return s;
	}
	
	/**
	 * Get the value for a leJOS NXJ persistent setting as an Integer
	 * 
	 * @param key the name of the setting
	 * @param defaultValue the default value
	 * @return the value
	 */
	public static int getIntSetting(String key, int defaultValue) {
		String s = getStringSetting(key, null);
		if (s == null)
			return defaultValue;
			
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	/**
	 * Set a leJOS NXJ persistent setting. 
	 * 
	 * @param key the name of the setting
	 * @param value the value to set it to
	 */
	public static void setSetting(String key, String value) {
		int slot = getSlotIndex(key);
		if (slot < 0 || slot == VERSION_SLOT)
			throw new IllegalArgumentException("unsupported key");
		
		setSlotValue(slot, value);
	}	
	
	/**
	 * Get the names of the the leJOS NXJ Settings
	 * 
	 * @return a String array of the names
	 */
	static String[] getSettingNames() {
		return NAMES;
	}
}
