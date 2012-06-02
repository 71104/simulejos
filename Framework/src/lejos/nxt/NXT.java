package lejos.nxt;

/**
 * Abstraction for the local NXT Device.
 * Supports methods that are non specific to any particular sub-system.
 * 
 * @author Lawrie Griffiths
 *
 */
public class NXT {	
		
	/**
	 * Shutdown the brick
	 */
	public static native void shutDown();
		
	/**
	 * Boot into firmware update mode.
	 */
	public static native void boot();
		 
	 /**
	  * Get the number of times a Java program (including the menu)
	  * has executed since the brick was switched on
	  * 
	  * @return the count
	  */
	 public static native int getProgramExecutionsCount();
	 
	 /**
	  * Return major and minor version and the patch level within a single integer.
	  * The format is not specified. Please use {@link #getFirmwareMajorVersion()},
	  * {@link #getFirmwareMinorVersion()}, and {@link #getFirmwarePatchLevel()}
	  * instead of this method.
	  * 
	  * @return a single version int
	  * @see #getFirmwareMajorVersion()
	  * @see #getFirmwareMinorVersion()
	  * @see #getFirmwarePatchLevel()
	  */
	 public static native int getFirmwareRawVersion();

	 /**
	  * Get the leJOS NXJ firmware major version
	  * 
	  * @return the major version number
	  */
	 public static int getFirmwareMajorVersion()
	 {
		 return getFirmwareRawVersion() >>> 16;
	 }

	 /**
	  * Get the leJOS NXJ firmware minor version
	  * 
	  * @return the minor version number
	  */
	 public static int getFirmwareMinorVersion()
	 {
		 return (getFirmwareRawVersion() >>> 8) & 0xFF;
	 }

	 /**
	  * Get the leJOS NXJ firmware patch level
	  * 
	  * @return the patch level number
	  */
	 public static int getFirmwarePatchLevel()
	 {
		 return getFirmwareRawVersion() & 0xFF;
	 }

	 /**
	  * Get the leJOS NXJ firmware revision number
	  * 
	  * @return the revision number
	  */
	 public static native int getFirmwareRevision();
     
     /**
      * Return the number of flash pages available to user programs.
      * Normally these pages are used to hold the leJOS file system.
      * 
      * @return The number of user pages.
      */
     public static native int getUserPages();
}
