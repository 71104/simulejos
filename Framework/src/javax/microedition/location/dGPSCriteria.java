package javax.microedition.location;

import lejos.nxt.SensorPort;

/**
 * 
 * 
 * <p>You can use the standard <code>javax.microedition.location</code> package with this class by
 * using a <code>dGPSCriteria</code> object to request a LocationProvider as follows:</p>
 * <p><code>dGPSCriteria criteria = new gGPSCriteria(SensorPort.S1);<br>
 * LocationProvider lp = LocationProvider.getInstance(criteria);
 * </p></code>
 
 * @author BB
 *
 */
public class dGPSCriteria extends Criteria {
	private SensorPort port;
	
	public dGPSCriteria(SensorPort port) {
		super();
		setPort(port);
	}
	
	/**
	 * Sets the port your dGPS is plugged into.
	 * @param port
	 */
	public void setPort(SensorPort port) {
		this.port = port;
	}
	
	/**
	 * The port your dGPS is plugged into.
	 * @returns The SensorPort for the dGPS.
	 */
	public SensorPort getPort() {
		return this.port;
	}
}
