package javax.microedition.location;

import lejos.nxt.SensorPort;
import lejos.nxt.addon.GPSSensor;

/**
 * <p>LocationProvider implementation for Dexter Industries dGPS.</p>
 * <p>NOTE: The sensor can not return altitude, so Location will contain 0 for altitude.</p> 
 * @author BB
 *
 */
class GPSLocationProvider extends LocationProvider {
		
		private GPSSensor gps;
		Thread listyThread;
		private boolean listenerRunning;
		
		GPSLocationProvider(SensorPort port) {
			gps = new GPSSensor(port);
		}
		
		@Override
		public Location getLocation(int timeout) throws LocationException,
				InterruptedException {
			
			// Convert from ddmmmmmm to degrees in WGS84 datum 
			double latitude = gps.getLatitude()/1000000.0;
			// Convert from dddmmmmmm to degrees in WGS84 datum
			double longitude = gps.getLongitude()/1000000.0;
			float altitude = 0; // TODO:
			float horizontalAccuracy = 0, verticalAccuracy = 0; // TODO:
			float speed = gps.getVelocity()/100; // Convert from cm/s to m/s
			float course = gps.getHeading();
			long timestamp = gps.getUTC();
			int locationMethod = 0; // TODO:
			String extraInfo = null; // TODO:
			
			QualifiedCoordinates coors = new QualifiedCoordinates(latitude, longitude, altitude, horizontalAccuracy, verticalAccuracy);
			Location location = new Location(coors, speed, course, timestamp, locationMethod, extraInfo); 
			return location;
		}

		@Override
		public int getState() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void reset() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setLocationListener(final LocationListener listener,
				final int interval, int timeout, int maxAge) {
			final LocationProvider parent = this;
			final int delay;
			if(interval<0) {
				delay = 500;
			} else {
				delay = interval;
			}
			
			listyThread = new Thread() {
				public void run() {
					while(listenerRunning) {
						try {
							listener.locationUpdated(parent, parent.getLocation(0));
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
			
		}
    	 
     }
