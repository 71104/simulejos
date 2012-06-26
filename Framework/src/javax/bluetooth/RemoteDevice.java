package javax.bluetooth;

import java.io.IOException;
import javax.microedition.io.Connection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.BTConnection;

/**
 * Represents a remote Bluetooth device.
 * 
 * @author Lawrie Griffiths
 *
 */
public class RemoteDevice {

	private String addr;
	
	private String friendlyName;
	
	private int deviceClass;
	
	/**
	 * Note: The standard JSR 82 method for obtaining a RemoteDevice
	 * uses a String rather than byte[]. Protected so shouldn't matter.
	 * @param addr
	 */
	protected RemoteDevice(String addr) {
		// Set Address:
		this.addr = addr;
		
		// !! Set device class: Is this going to be used?
		// (Not part of JSR 82)
		
		// Set Friendly name:
		this.getFriendlyName(true); // Refresh name
	}
	
	// !! DEV NOTES: Remove this whole method eventually.
	public RemoteDevice(String name, String deviceAddr, int devclass) {
		setFriendlyName(name);
		setDeviceAddr(deviceAddr);
		this.deviceClass = devclass;
	}

	/*
	 * TODO: UNIMPLEMENTED
	 * DEV NOTES: Internally this just casts the Connection object 
	 * into a BTConnection object. However, how to pull information 
	 * from it, such as friendly name, address, etc... ??
	 * Solution: Add address to BTConnection class?
	 */
	public static RemoteDevice getRemoteDevice(Connection conn) throws IOException {
		BTConnection btc = (BTConnection)conn;
		return new RemoteDevice(btc.getAddress());
	}
	
	public void setDeviceAddr(String deviceAddr) {
        addr = deviceAddr;
	}
	
	public String getDeviceAddr() {
		return addr;
	}
	
	/*
	 * TODO: DELETE THIS. Not part of official API.
	 */
	public void setFriendlyName(String fName) {
		this.friendlyName = fName;

	}
	
	/**
	 * Indicates if the remote device has been properly paired (authenticated).
	 * @return true iff the device has been paired
	 */
	public boolean isAuthenticated() {
		// When a RemoteDevice is retrieved, the name is blank unless it has been previously
		// added to the BC4 chip cache. This method exploits this quirk to do calculation.
		return !this.friendlyName.equals("");
	}
	
	/**
	 * Always returns false because NXT BC4 chip doesn't encrypt.
	 * @return false means it is not encrypted
	 */
	// TODO: No encrypt possibility with BC4 chip on NXT?
	public boolean isEncrypted() {
		return false;
	}
	
	/**
	 * 
	 * @param alwaysAsk true causes the method to contact the remote device for the name. false and it will use the known name. 
	 * @return the friendly name
	 */
	public String getFriendlyName(boolean alwaysAsk) {
		
		if(alwaysAsk) {
			friendlyName = Bluetooth.lookupName(addr);
		}
		return friendlyName;
	}
	
	
	public String getBluetoothAddress() {
		return addr;
	}
	
	/**
	 * Determines if two RemoteDevices are equal. If they both have the same BT address
	 * then they are considered equal.
	 */
	public boolean equals(Object obj) {
		return obj != null && obj instanceof RemoteDevice && ((RemoteDevice) obj).getBluetoothAddress().equals(getBluetoothAddress());
	}
	
	/*
	 * TODO: REMOVE EVENTUALLY
	 * DEV NOTES: This is not a standard JSR 82 method.
	 */
	public int getDeviceClass() {
		return deviceClass;
	}
}