package javax.microedition.sensor;

/**
 * Implementation of the JSR 256 Unit class for the units supported by
 * LEGO Mindstorms I2C sensors
 * 
 * @author Lawrie Griffiths
 */
public class Unit {
	// Supported measurements
	private static final Unit METRIC_LENGTH = new Unit("m");
	private static final Unit ACCELERATION = new Unit("m/s^2");
	private static final Unit PLANE_ANGLE = new Unit("degree");
	private static final Unit IMPERIAL_LENGTH = new Unit("in");
	private static final Unit GRAVITY = new Unit("g");
	private static final Unit NO_UNIT = new Unit("");
	private static final Unit VOLTS = new Unit("V");
	private static final Unit DECIBEL = new Unit("db");
	private static final Unit CELSIUS = new Unit("Celsius");
	private static final Unit DEGREES_PER_SEC = new Unit("degree/s");
	
	private static Unit[] units = {METRIC_LENGTH, ACCELERATION, 
								   PLANE_ANGLE, IMPERIAL_LENGTH,
								   GRAVITY, DECIBEL, CELSIUS, VOLTS, 
								   DEGREES_PER_SEC, NO_UNIT};
	
	private String symbol;
	
	public static Unit getUnit(String symbol) {
		for(int i=0;i<units.length;i++) {
			if (symbol.equals(units[i].toString())) return units[i];
		}
		return null;
	}
	
	private Unit(String symbol) {
		this.symbol = symbol;
	}
	
	public String toString() {
		return symbol;
	}
}
