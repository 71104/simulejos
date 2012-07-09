package javax.microedition.sensor;

/**
 * Basic implementation of the JSR256 MeasurementRange class
 * 
 * @author Lawrie Griffiths
 */
public class MeasurementRange {
	private double smallest, largest, resolution;
	
	public MeasurementRange(double smallest, double largest, double resolution) {
		this.smallest = smallest;
		this.largest = largest;
		this.resolution = resolution;
	}
	
	public double getLargestValue() {
		return largest;
	}
	
	public double getResolution() {
		return resolution;
	}
	
	public double getSmallestValue() {
		return smallest;
	}
}
