package javax.microedition.sensor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;

/** 
 * JSR256 SensorManager implementation for leJOS NXJ I2C sensors
 * 
 * @author Lawrie Griffiths
 *
 */
public class SensorManager {
	// Registry of all known sensors
	private static final NXTSensorInfo[] sensors = {
		new BatterySensorInfo(),
		new LightSensorInfo(),
		new RCXLightSensorInfo(),
		new SoundSensorInfo(),
		new TouchSensorInfo(),
		new RCXTemperatureSensorInfo(),
		new UltrasonicSensorInfo(),
		new MindsensorsAccelerationSensorInfo(),
		new HiTechnicCompassSensorInfo(),
		new HiTechnicColorSensorInfo(),
		new HiTechnicGyroSensorInfo()
	};
	// Hashtable of listeners and a quantity or SensorInfo object
	private static Hashtable<SensorListener, ArrayList<Object>> listeners = new Hashtable<SensorListener, ArrayList<Object>>();
	// List of currently attached sensors
	private static ArrayList<NXTSensorInfo> availableSensors;
	private static ArrayList<NXTActiveCondition> conditions = new ArrayList<NXTActiveCondition>();
	private static ArrayList<NXTActiveData> dataListeners = new ArrayList<NXTActiveData>();
	static {
		availableSensors = getSensors();
		Thread listener = new Thread(new Listener());
		listener.setDaemon(true);
		listener.start();
		Thread conditionListener = new Thread(new CondListener());
		conditionListener.setDaemon(true);
		conditionListener.start();
	}
	
	/**
	 * Register a listener to monitor the change of availability of a specific sensor
	 * 
	 * @param listener the sensor listener
	 * @param info a SensorListener object returned from findSensors or SensorConnection.getSensorInfo
	 */
	public static void addSensorListener(SensorListener listener, SensorInfo info) {
		addSensorListenerObject(listener, info);
	}
	
	/**
	 * Register a listener to monitor the change of availability of a sensor for a specific quantity
	 * 
	 * @param listener the sensor listener
	 * @param quantity the required quantity
	 */
	public static void addSensorListener(SensorListener listener, String quantity) {
		addSensorListenerObject(listener, quantity);
	}
	
	/**
	 * Find all available sensors that match a specific URL.
	 * Note that this only finds I2C sensors.
	 * 
	 * @param url the specified URL
	 * @return an array of SensorInfo objects
	 */
	public static synchronized SensorInfo[] findSensors(String url) {
		SensorURL sensorURL = SensorURL.parseURL(url);		
		if (sensorURL == null) throw new IllegalArgumentException();
		
		checkSensors();
		return getSensors(sensorURL);
	}
	
	/**
	 * Find all available sensors that match a specific quantity and/or context.
	 * Note that this only finds I2C sensors.
	 * 
	 * @param quantity the required quantity or null for any
	 * @param contextType the required context type or null for any
	 * @return an array of SensorInfo objects
	 */
	public static synchronized SensorInfo[] findSensors(String quantity, String contextType) {			
		checkSensors();
		return getSensors(quantity, contextType);
	}
	
	/**
	 * Remove the specified sensor listener
	 * 
	 * @param listener the sensor listener
	 */
	public static synchronized void removeSensorListener(SensorListener listener) {
		if (listeners.get(listener) != null) {
			listeners.put(listener, null);
		}
	}
	
	// Add either a SensorInfo or a quantity to a listener
	private synchronized static void addSensorListenerObject(SensorListener listener, Object obj) {
		// Get existing objects that the listener is monitoring
		ArrayList<Object> value = listeners.get(listener);
		
		// If no entry for the listener, create an ArrayList for the entries
		if (value == null) {
			value = new ArrayList<Object>();
			
			// Add the ArrayList to the listeners
			listeners.put(listener, value);
		}
		
		if (!value.contains(obj)) { // Don't add the same object twice
			// Add the object to the ArrayList
			value.add(obj);
		}	
	}
	
	// Get the available sensors that match a specific URL
	static synchronized NXTSensorInfo[] getSensors(SensorURL searchURL) {
		int count = 0;
		//searchURL.printURL();
		
		// Count matching sensors
		for(NXTSensorInfo avail: availableSensors) {
			SensorURL targetURL = SensorURL.parseURL((avail.getUrl()));
			//targetURL.printURL();
			if (searchURL.matches(targetURL)) count++;
		}
		
		// Put them in an array
		NXTSensorInfo[] infoArray = new NXTSensorInfo[count];	
		int i=0;
		for(NXTSensorInfo avail: availableSensors) {
			SensorURL targetURL = SensorURL.parseURL((avail.getUrl()));
			if (searchURL.matches(targetURL)) {
				infoArray[i++] = avail;
			}
		}	
		//System.out.println("Found " + infoArray.length + " sensors");
		return infoArray;
	}
	
	// Get the available sensors that match the given quantity and context type
	static synchronized NXTSensorInfo[] getSensors(String quantity, String contextType) {		
		int count = 0;
		
		// Count matching sensors
		for(NXTSensorInfo avail: availableSensors) {
			if ((quantity == null || avail.getQuantity().equals(quantity) &&
			    (contextType == null || avail.getContextType().equals(contextType)))) count++;
		}
		
		// Put them in an array
		NXTSensorInfo[] infoArray = new NXTSensorInfo[count];	
		int i=0;
		for(NXTSensorInfo avail: availableSensors) {
			if ((quantity == null || avail.getQuantity().equals(quantity) &&
				    (contextType == null || avail.getContextType().equals(contextType)))) {
				infoArray[i++] = avail;
			}
		}			
		return infoArray;
	}
	
	// Poll for the currently attached sensors, compare with
	// previous set and generate available and unavailable events
	private synchronized static void checkSensors() {
		ArrayList<NXTSensorInfo> oldSensors = availableSensors;
		availableSensors = getSensors();
		
		// Check for missing sensors
		for(NXTSensorInfo old: oldSensors) {
			boolean stillThere = false;
			for(NXTSensorInfo current : availableSensors) {
				if (old.equals(current)) stillThere = true;
			}
			if (!stillThere) notify(old, false);
		}
		
		// Check for new sensors
		for(NXTSensorInfo current: availableSensors) {
			boolean wasThere = false;
			for(NXTSensorInfo old : oldSensors) {
				if (old.equals(current)) wasThere = true;
			}
			if (!wasThere) notify(current, true);
		}		
	}

	// Get the currently attached sensors and fill in SensorInfo structures with dynamic information
	private static synchronized ArrayList<NXTSensorInfo> getSensors() {
		ArrayList<NXTSensorInfo> current = new ArrayList<NXTSensorInfo>();
		
		for(int i=0;i<SensorPort.NUMBER_OF_PORTS;i++) {
			I2CSensor i2cSensor = new I2CSensor(SensorPort.getInstance(i));		
			String type = null;
			
			// Try a few times as Ultrasonic sensor is unreliable
			for(int j=0;j<10;j++) {
				type = i2cSensor.getProductID();
				if (type.length() > 0) break;
			}
			if (type.length()== 0) continue;
			
			NXTSensorInfo info = findSensorInfo(type);

			// Fill in details from the attached sensor
			if (info != null) {
				info.setPortNumber(i);
				info.setType(type);
				info.setVendor(i2cSensor.getVendorID());
				info.setVersion(i2cSensor.getVersion());
					
				current.add(info);
			}
		}
		return current;
	}
	
	/*
	 * Get the sensor information for a sensor of  given type
	 */
	private synchronized static NXTSensorInfo findSensorInfo(String type) {
		for(int i=0;i<sensors.length;i++) {
			String[] models = sensors[i].getModelNames();
			if (models != null) {
				for(int j=0;j<models.length;j++) {
					if (models[j].equals(type)) return sensors[i];
				}
			}
		}
		return null;
	}
	
	/*
	 * Get the sensor information for sensors that measure a specific quantity
	 */
	public synchronized static NXTSensorInfo[] findQuantity(String quantity) {
		for(int i=0;i<sensors.length;i++) {
			if (quantity.equals(sensors[i].getQuantity())) {
				//TODO: Return multiple matches
				return new NXTSensorInfo[]{sensors[i]};
			}
		}
		return null;
	}
	
	// Notify listeners of available or unavailable events
	private synchronized static void notify(NXTSensorInfo sensor, boolean available) {
		Enumeration<SensorListener> quantityKeys = listeners.keys();
		
		while(quantityKeys.hasMoreElements()) {
			SensorListener listener = quantityKeys.nextElement();		
			ArrayList<Object> values = listeners.get(listener);
			if (values != null) {
				for(Object obj: values) {
					if (obj instanceof String) {
						String quantity = (String) obj;
						if (sensor.getQuantity().equals(quantity)) {
							if (available) listener.sensorAvailable(sensor);
							else listener.sensorUnavailable(sensor);
							break; // Only inform sensor listener once
						}
					} else if (obj != null) {
						SensorInfo info = (SensorInfo) obj;
						if (sensor == info) {
							if (available) listener.sensorAvailable(sensor);
							else listener.sensorUnavailable(sensor);
							break;
						}
					}
				}
			}			
		}
	}
	
	/*
	 * Add a condition for a specific condition listener on a channel
	 */
	static synchronized void addCondition(NXTChannel channel, ConditionListener conditionListener, Condition condition) {
		// Check if the condition already set
		for(NXTActiveCondition cond: conditions) {
			if (cond.getChannel()  == channel && cond.getCondition() == condition && 
				cond.getConditionListener() == conditionListener) return;
		}
		conditions.add(new NXTActiveCondition(channel, condition, conditionListener));
	}
	
	/*
	 * Remove a condition (on all condition listeners) on a channel
	 */
	static synchronized void removeCondition(NXTChannel channel, Condition condition) {
		 for (Iterator<NXTActiveCondition> it = conditions.iterator(); it.hasNext();) {
			 NXTActiveCondition cond = it.next();
				if (cond.getCondition() == condition && cond.getChannel() == channel) {
					it.remove();
				}
		 }
	}
	
	/*
	 * Remove a specific condition on a specific condition listener on a channel
	 */
	static synchronized void removeCondition(NXTChannel channel, ConditionListener listener, Condition condition) {
		 for (Iterator<NXTActiveCondition> it = conditions.iterator(); it.hasNext();) {
			 NXTActiveCondition cond = it.next();
				if (cond.getCondition() == condition && cond.getChannel() == channel && 
					cond.getConditionListener() == listener) {
					it.remove();
				}
		 }
	}
	
	/*
	 * Get all the conditions for a given condition listener on a channel.
	 * Note that there can be no duplicates
	 */
	static synchronized Condition[] getConditions(Channel channel, ConditionListener listener) {
		int count = 0;
		for (NXTActiveCondition cond: conditions) {
			if (cond.getChannel() == channel && cond.getConditionListener() == listener) {
				count++;
			}
		}
		Condition[] cc = new Condition[count];
		int i=0;
		for (NXTActiveCondition cond: conditions) {
			if (cond.getChannel() == channel && cond.getConditionListener() == listener) {
				cc[i++] = cond.getCondition();
			}
		}
		return cc;
	}
	
	/*
	 * Remove all conditions on a channel
	 */
	static synchronized void removeAllConditions(Channel channel) {
		 for (Iterator<NXTActiveCondition> it = conditions.iterator(); it.hasNext();) {
			 NXTActiveCondition cond = it.next();
				if (cond.getChannel() == channel) {
					it.remove();
				}
		 }
	}
	
	/*
	 * Remove a condition listener on a channel
	 */
	static synchronized void removeConditionListener(Channel channel, ConditionListener listener) {
		 for (Iterator<NXTActiveCondition> it = conditions.iterator(); it.hasNext();) {
			 NXTActiveCondition cond = it.next();
				if (cond.getChannel() == channel && cond.getConditionListener() == listener) {
					it.remove();
				}
		 }
	}
	
	/*
	 * Add a data listener for a sensor
	 */
	static void addDataListener(NXTSensorConnection sensor, int bufferSize, DataListener listener, int samplingInterval) {
		// Remove any existing data listener
		removeDataListener(sensor);
		dataListeners.add(new NXTActiveData(sensor, bufferSize, listener, samplingInterval));
	}
	
	/*
	 * Remove the data listeners for a given sensor
	 */
	static void removeDataListener(SensorConnection sensor) {
		 for (Iterator<NXTActiveData> it = dataListeners.iterator(); it.hasNext();) {
			 NXTActiveData active = it.next();
				if (active.getSensor() == sensor) {
					it.remove();
				}
		 }
	}
	
	/*
	 * Check which conditions are met. If they are met, generate the event and
	 * delete the condition.
	 */
	private static synchronized void checkConditions() {
		for (Iterator<NXTActiveCondition> it = conditions.iterator(); it.hasNext();) {
			NXTActiveCondition cond = it.next();
			NXTChannel channel = cond.getChannel();
			NXTSensorConnection sensor = channel.getSensor();
			int reading = sensor.getChannelData(channel.getChannelInfo());
			if(cond.getCondition().isMet(reading)) {
				NXTData data = new NXTData(channel.getChannelInfo(),1);
				data.setIntData(0, reading);
				cond.getConditionListener().conditionMet(sensor, data, cond.getCondition());
				it.remove();
			}
		}		
	}
	
	/*
	 * Process all active data listeners
	 */
	private static synchronized void processData() {
		for(NXTActiveData active: dataListeners) {
			active.process();
		}
	}
	
	/**
	 * Thread to monitor availability of I2C sensors
	 */
	static class Listener implements Runnable {
		public void run() {		
			for(;;) {	
				Delay.msDelay(1000);			
				checkSensors();
			}
		}
	}
	/**
	 * Thread to monitor conditions on channels
	 */
	static class CondListener implements Runnable {
		public void run() {
			for(;;) {
				// Check for conditions being met
				checkConditions();
				// Process data transfers
				processData();
				Delay.msDelay(10);
			}
		}
	}
}
