package javax.bluetooth;

import lejos.nxt.comm.Bluetooth;

/**
 * Singleton class representing a local NXT Bluetooth device.
 * Most methods are standard, except you can also set the friendly
 * name with this class. 
 * @author BB
 *
 */
public class LocalDevice {
	
	private static LocalDevice localDevice;
	private DiscoveryAgent discoveryAgent;
		
	private LocalDevice() {
		discoveryAgent = new DiscoveryAgent();
	}

	/*
	 * DEVELOPER NOTES: Technically the constructor doesn't throw
	 * a BluetoothStateException like the Bluetooth API demands.
	 * Get rid of exception?
	 */
	public static LocalDevice getLocalDevice() throws BluetoothStateException {
		if(localDevice == null)
			localDevice = new LocalDevice();
		return localDevice;
	}
	
	/**
	 * Returns the discovery agent for this device. Multiple calls to this method will 
	 * return the same object. This method will never return null.
	 * @return the discovery agent for the local device
	 */
	public DiscoveryAgent getDiscoveryAgent() {
		return discoveryAgent;
	}

	/**
	 * Returns the friendly name of a Bluetooth device. 
	 * NOTE: If you want to set the friendly name, it can be done 
	 * through the LCP class or using the NXJExplorer program on 
	 * your personal computer.
	 * @return the friendly name
	 */
	public String getFriendlyName() {
		return Bluetooth.getFriendlyName();
	}
	
	/**
	 * Changes the friendly name of the NXT brick.
	 * NOTE: This method is not part of the standard JSR 82 API
	 * because not all Bluetooth devices can change their friendly name.
	 * This method does not work. Technically this should be done
	 * through LCP so USB can also change it.
     * @param name new friendly name
	 * @return true = success, false = failed
	 */
	public boolean setFriendlyName(String name) {
		return Bluetooth.setFriendlyName(name);
	}
	
		
	/**
	 * The Bluetooth device class for the LEGO NXT brick. 
	 * The Lego Bluecore code can't retrieve this from the chip.
	 * Always returns hardcoded 0x3e0100 DeviceClass
	 * Untested if this is correct device class or not.
	 * @return the device class
	 */
	public DeviceClass getDeviceClass() {
		return new DeviceClass(0x3e0100);
	}

	/**
	 * Normally the mode values are found in javax.bluetooth.DiscoveryAgent.
	 * We don't have this yet in NXJ so use 0 for invisible, any other
	 * value for visible.
	 * @param mode 0 = invisible, all other = visible.
	 * @return true if successful, false if unsuccessful
	 * @throws BluetoothStateException
	 */
	public boolean setDiscoverable(int mode) throws BluetoothStateException {
		/*
		 * DEVELOPER NOTES: javax.bluetooth.DiscoveryAgent.NOT_DISCOVERABLE = 0x00
		 */
		
		// 0x00 = invisible, 0x01=visible 
		int ret = Bluetooth.setVisibility((byte)(mode == 0 ? 0 : 1));
		return (ret >=0);
	}
	
	/**
	 * Power state of the Bluecore 4 chip in the NXT brick. 
	 * @return the power state
	 */
	public static boolean isPowerOn() {
		return Bluetooth.getPower(); 
	}
	
	/**
	 * Indicates whether the NXT brick is visible to other devices 
	 * @return 0 = not discoverable, all other = discoverable
	 */
	public int getDiscoverable() {
		return Bluetooth.getVisibility();
	}
	
	/**
	 * UNIMPLEMENTED! Returns null always.
	 * Returns various properties about the bluetooth implementation
	 * such as version, whether master/slave switch allowed, etc..
	 * Possibly use Properties class in implementation.
	 * @param property
	 * @return null
	 */
	public static String getProperty(String property) {
		return null;
	}
	/**
	 * Returns the local Bluetooth address of NXT brick.
	 * @return the address
	 */
	public String getBluetoothAddress() {
		return Bluetooth.getLocalAddress();
	}
}
