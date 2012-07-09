package javax.microedition.sensor;

import java.util.StringTokenizer;

/**
 * Parse a sensor URL
 * 
 * @author Lawrie Griffiths
 */
public class SensorURL {
	private String quantity;
	private String model;
	private String context;
	private String location;
	private int portNumber;
	
	private SensorURL(String quantity, String context, String model, 
			          String location, int portNumber) {
		this.quantity = quantity;
		this.context = context;
		this.model = model;
		this.location = location;
		this.portNumber = portNumber;
	}
	
	public static SensorURL parseURL(String url) {
		String model = null, location = null, context = null;
		int portNumber = 0;
		int colon = url.indexOf(':');
		if (colon < 0) return null;
		
		if (!url.substring(0,colon).equals("sensor")) return null;
		
		String sensorId = url.substring(colon+1);
		
		StringTokenizer tokenizer = new StringTokenizer(sensorId, ";");
		
		String quantity = tokenizer.nextToken();
		
		while(tokenizer.hasMoreTokens()) {
			String parameter = tokenizer.nextToken();
			int equalsSign = parameter.indexOf('=');
			if (equalsSign < 0) return null;
			String parameterName = parameter.substring(0,equalsSign);
			String parameterValue = parameter.substring(equalsSign+1);
			if (parameterName.equals("model")) model = parameterValue;
			else if (parameterName.equals("contextType")) context = parameterValue;
			else if (parameterName.equals("location")) location = parameterValue;
			else if (parameterName.equals("port")) {
				try {
					portNumber = Integer.parseInt(parameterValue)-1;
				} catch (NumberFormatException e) {
					return null;
				}
			}
			
		}
		
		return new SensorURL(quantity, context, model, location, portNumber);
	}
	
	public String getQuantity() {
		return quantity;
	}
	
	public String getModel() {
		return model;
	}
	
	public String getContextType() {
		return context;
	}
	
	public String getLocation() {
		return location;
	}
	
	public int getPortNumber() {
		return portNumber;
	}
	
	public void printURL() {
		System.out.println("quantity = " + quantity);
		if (model != null) System.out.println("model = " + model);
		if (context != null) System.out.println("contextType = " + context);
		if (location != null) System.out.println("location = " + location);
		if (portNumber >= 0) System.out.println("port = " + portNumber);
	}
	
	public boolean matches(SensorURL url) {
		if (!quantity.equals(url.getQuantity())) return false;
		if (model != null && !model.equals(url.getModel())) return false;
		if (context != null && !context.equals(url.getContextType())) return false;
		if (location != null && !location.equals(url.getLocation())) return false;
		if (portNumber >= 0 && portNumber != url.getPortNumber()) return false;		
		return true;
	}
}
