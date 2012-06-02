/**
 * NXT access classes.
 */
package lejos.nxt;

/**
 * Provides access to Battery.
 */
public class Battery
{
  private static final int RECHARGEABLE = 0x8000;
  private static final int VOLTAGE_MASK = 0x3fff;

  private Battery()
  {
	  //nothing
  }
  
  /**
   * Returns the battery status. 
   * Low bits are the voltage in mV, bit 0x8000 is set if the rechargeable
   * battery pack is in use.
   * @return
   */
  private static native int getBatteryStatus();

  /**
   * Returns the battery voltage in millivolts.
   * 
   * @return Battery voltage in mV.
   */
  public static int getVoltageMilliVolt()
  {
      return getBatteryStatus() & VOLTAGE_MASK;
  }

  public static boolean isRechargeable()
  {
      return (getBatteryStatus() & RECHARGEABLE) != 0;
  }
  
  /**
   * Returns the battery voltage in volts.
   * 
   * @return Battery voltage in Volt.
   */
  public static float getVoltage()
  {
    return Battery.getVoltageMilliVolt() * 0.001f;
  }
}
