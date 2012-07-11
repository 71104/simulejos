package lejos.nxt;

import java.util.prefs.Preferences;

/**
 * This class is designed for use by other lejos classes to read persistent
 * settings. User programs should use the Settings class
 * 
 * @author Lawrie Griffiths
 * 
 */
public class SystemSettings {
	public static final int MAX_SETTING_SIZE = 21;
	private static final String VERSION_NAME = "settings.version";

	// Add to this String array to define new persistent settings.
	// There is a maximum of (256 / MAX_SETTING_SIZE) including the version.
	private static final String[] NAMES = { VERSION_NAME, "lejos.volume",
			"lejos.default_program", "lejos.keyclick_volume",
			"lejos.default_autoRun", "lejos.sleep_time", "lejos.usb_serno",
			"lejos.usb_name", "lejos.bluetooth_pin" };

	/**
	 * Get the value for a leJOS NXJ persistent setting as a String
	 * 
	 * @param key
	 *            the name of the setting
	 * @param defaultValue
	 *            the default value
	 * @return the value
	 */
	public static String getStringSetting(String key, String defaultValue) {
		return Preferences.userNodeForPackage(SystemSettings.class).get(key,
				defaultValue);
	}

	/**
	 * Get the value for a leJOS NXJ persistent setting as an Integer
	 * 
	 * @param key
	 *            the name of the setting
	 * @param defaultValue
	 *            the default value
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
	 * @param key
	 *            the name of the setting
	 * @param value
	 *            the value to set it to
	 */
	public static void setSetting(String key, String value) {
		Preferences.userNodeForPackage(SystemSettings.class).put(key, value);
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
