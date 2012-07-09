package javax.microedition.location;

import java.io.*;
import javax.bluetooth.*;
import javax.microedition.io.*;


import lejos.addon.gps.*;

/**
 * This class is not visible to users and should not be instantiated directly. Instead it
 * is retrieved from the factory method LocationProvider.getInstance().
 * @author BB
 *
 */
class BTGPSLocationProvider extends LocationProvider implements DiscoveryListener, GPSListener {

	SimpleGPS gps = null;
	DiscoveryAgent da;
	RemoteDevice btDevice = null;
	
	// LocationListener variables:
	private Thread listyThread = null;
	private boolean listenerRunning = true;
	private GPSListener gpsl = null; // JSR-179 only allows one LocationListener at a time.
	
	/**
	 * doneInq is used to ensure the code doesn't try to connect to the GPS device
	 * before the Bluecore chip is done the inquiry. If you try to connect before the inquiry is
	 * done it will cause a malfunction. This is due to our Bluecove code in leJOS, which requires
	 * the programmer to be very careful. 
	 */
	boolean doneInq = false;
	
	// I think this indicates the BT device is a GPS unit: TODO: Wrong. Keyboard has same signature.
	private static final int GPS_MAJOR = 0x1F00;
	
	protected BTGPSLocationProvider() throws LocationException {
		
		// TODO: Move this to searchConnect method?
		// TODO: The problem here is that it searches every time. Slow. Need to try Properties?
		// TODO: BIG ONE: Should only connect to GPS that isPaired() (from menu). Will
		// allow some degree of control over which GPS is connects to in classroom.
		try {
			da = LocalDevice.getLocalDevice().getDiscoveryAgent();
			da.startInquiry(DiscoveryAgent.GIAC, this);
		} catch (BluetoothStateException e) {
			throw new LocationException(e.getMessage());
		}
		
		while(!doneInq) {Thread.yield();}
		
		if(btDevice == null) throw new LocationException("No device found");
		
		String address = btDevice.getBluetoothAddress();
		String btaddy = "btspp://" + address;
		
		try {
			StreamConnectionNotifier scn = (StreamConnectionNotifier)Connector.open(btaddy);
			
			if(scn == null)	throw new LocationException("Bad BT address");
			StreamConnection c = scn.acceptAndOpen();
			
			/* This problem below occurred one time for my Holux GPS. The solution was to
			 * remove the device from the Bluetooth menu, then find and pair again.
			 */
			if(c == null)throw new LocationException("Failed. Try pairing at menu again");
			InputStream in = c.openInputStream();
			
			if(in != null) {
				gps = new SimpleGPS(in);
				// c.close(); // TODO: Clean up when done. HOW TO HANDLE IN LOCATION?
			}
		} catch(IOException e) {
			throw new LocationException(e.getMessage());	
		}
		// Add itself to SimpleGPS as listener
		SimpleGPS.addListener(this);
	}
	
	public Location getLocation(int timeout) throws LocationException,
			InterruptedException {
		/* TODO The timeout might play to the fact that it is still acquiring satellites?
		 * I was wondering about that before. Maybe it makes sense to have timeout in SimpleGPS?
		 * TODO: Solution! Keep asking for altitude until is positive? (longitude can be negative)
		 * Or perhaps just until speed positive? (set those after)
		 * TODO: -1 in timeout is supposed to represent the default timeout (GPSListener?)
		 * TODO: I don't know if this is supposed to wait for the GPS to provide a new
		 * coordinate data or if it is okay to pass the latest cached GPS coordinates.
		 * Is the purpose of the timeout that it gets a new updated location that
		 * is not the previously returned or cached one? 
		*/
		
		if(timeout == 0)
			throw new IllegalArgumentException("timeout cannot equal 0");
		
		// Timeout results in LocationException:
		long startTime = System.currentTimeMillis();
		
		// TODO: Perhaps initialize and test for NaN instead. 
		while(gps.getLatitude() == 0 & gps.getLongitude() == 0) {
			if(timeout != -1 & System.currentTimeMillis() - startTime > (timeout * 1000))
				throw new LocationException("GPS timed out");
			Thread.sleep(100); /* NOTE: This might very occasionally cause an error because
			* Thread.yield() seems to cause sentence parsing to start too soon. 
			* (try changing sleep() to yield() to see what happens)
			* Perhaps something needs to be synchronized? */ 
		}
		
		QualifiedCoordinates qc = new QualifiedCoordinates(gps.getLatitude(), gps.getLongitude(), gps.getAltitude(), (gps.getHDOP() * 6), (gps.getVDOP() * 6));
		Location loc = new Location(qc, gps.getSpeed(), gps.getCourse(), gps.getTimeStamp(),
				0,null); // TODO: Implement location method and extraInfo (0 and null for now)
		
		return loc;
	}
	

	public int getState() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void reset() {
		// TODO Auto-generated method stub

	}

	public void setLocationListener(LocationListener listener, int interval,
			int timeout, int maxAge) {
		
		// * Stop all previous listener threads *
		listenerRunning = false;
		if(listyThread != null) {
			while(listyThread.isAlive()) {Thread.yield();} // End old thread
			listyThread = null; // Discard the listener thread instance 
		}
		
		// * Remove any listeners from GPSListener *
		if (listener == null) {
			// Remove current listener from SimpleGPS
			SimpleGPS.removeListener(gpsl);
			gpsl = null;
			return; // No listener provided, so return now so it dosn't make a new one
		}
		
		// * Inner classes need final variables *
		final int to = timeout;
		final LocationListener l = listener;
		final LocationProvider lp = this;
		final int delay = interval * 1000; // Oddly interval is in seconds, and not float
		
		// Make new thread here and start it if interval > 0, else if -1 
		// then use the GPSListener interface.
		if (interval > 0) { // Notify according to interval by user
			listyThread = new Thread() {
				public void run() {
					while(listenerRunning) {
						try {
							// TODO: Probably only notify if location changed? Need to compare to old.
							// TODO: Make helper method since this is used below too. 
							l.locationUpdated(lp, lp.getLocation(to));
							Thread.sleep(delay);
						} catch (LocationException e) {
							// TODO Auto-generated catch block
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
						}
					}
				}
			};
			listyThread.setDaemon(true); // so JVM exits if thread is still running
			listenerRunning = true;
			listyThread.start();
		} else if(interval < 0) { // If interval is -1, use default update interval
			// In our case, update as soon as new coordinates are available from GPS (via GPSListener) 
			// TODO: Alternate method: Use GPSListener for ProximityListener and this.
			gpsl = new GPSListener() {
				public void sentenceReceived(NMEASentence sen) {
					// Check if GGASentence. Means that new location info is ready
					if(sen.getHeader().equals(GGASentence.HEADER)) {
						try {
							// TODO: Probably only notify if location changed? Need to compare to old.
							l.locationUpdated(lp, lp.getLocation(to));
						} catch (LocationException e) {
							// TODO Auto-generated catch block
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
						}
					}
				}
			};
			SimpleGPS.addListener(gpsl);
		}
		
		// TODO: Need to implement LocationListener.providerStateChanged()  
	}
	
	/**
	 * This method is from GPSListener, used to notify the ProximityListener. 
	 */
	public void sentenceReceived(NMEASentence sen) {
		// Check if this sentence has location info
		if(sen.getHeader().equals(GGASentence.HEADER)) {
			Coordinates cur = null;
			Location loc = null;
			try {
				
				loc = this.getLocation(-1);
				cur = loc.getQualifiedCoordinates();
			} catch(InterruptedException e) {
				// TODO: This method should bail properly if it fails.
				System.err.println("Fail 1");
			} catch (LocationException e) {
				// TODO: This method should bail properly if it fails.
				System.err.println("Fail 2");
			}
			for(int i=0; i<listeners.size();i++){
				Object [] array = listeners.get(i);
				ProximityListener pl = (ProximityListener)array[0];
				Coordinates to = (Coordinates)array[1];
				Float rad = (Float)array[2];
				// Now check radius against coordinate and notify listener.
				if(cur.distance(to) <= rad.floatValue()) {
					// Remove this ProximityListener because it should be notified only once.
					// I prefer to do this BEFORE notifying the pl because the user might try
					// to re-add the pl in proximityEvent().
					LocationProvider.removeProximityListener(pl);
					pl.proximityEvent(to, loc);
				}
			}
			
			// TODO: Handle LocationListeners here instead of inner?
		}
	}
	
	/* DiscoveryListener methods: */
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		//System.err.println(btDevice.getFriendlyName(false) + " discovered.");
		/*
		System.err.println("Major = " + cod.getMajorDeviceClass());
		System.err.println("Minor = " + cod.getMinorDeviceClass());
		System.err.println("Service = " + cod.getServiceClasses());
		System.err.println("GPS_MAJOR = " + GPS_MAJOR);
		System.err.println("Authenticated? " + btDevice.isAuthenticated());
		*/
		
		if((cod.getMajorDeviceClass() & GPS_MAJOR) == GPS_MAJOR) {
			if(btDevice.isAuthenticated()) { // Check if paired.
				this.btDevice = btDevice;
				da.cancelInquiry(this);
			}
		}	
	}

	public void inquiryCompleted(int discType) {
		doneInq = true;
	}
}
