package lejos.nxt;

import java.util.Properties;

/**
 * leJOS NXJ persistent settings.
 * 
 * @author Lawrie Griffiths
 *
 */
public class Settings {
	
	private static Properties props;
	
	/**
	 * Get the value for a leJOS NXJ persistent setting
	 * @param key the name of the setting
	 * @param defaultValue the default value
	 * @return the value
	 */
	public static String getProperty(String key, String defaultValue) {
		return SystemSettings.getStringSetting(key, defaultValue);

	}

	/**
	 * Set a leJOS NXJ persistent setting. 
	 * 
	 * @param key the name of the setting
	 * @param value the value to set it to
	 */
	public static void setProperty(String key, String value) {	
		SystemSettings.setSetting(key, value);
	}
	
	/**
	 * Get leJOS NXJ persistent settings as Java Properties.
	 * 
	 * Note that the returned Properties object is read-only: setting
	 * property values in this Properties object has no effect on the
	 * settings used by the current program and no effect on the persistent
	 * settings.
	 * 
	 * @return a Properties object containing all the leJOS NXJ settings.
	 */
	public static Properties getProperties() {
		String [] names = SystemSettings.getSettingNames();
		props = new Properties();
		
		for(int i=0;i<names.length;i++) {
			//System.out.println(names[i]);
			//System.out.println(Settings.getProperty(names[i], ""));
			props.setProperty(names[i], Settings.getProperty(names[i], ""));
		}
		return props;
	}
}
