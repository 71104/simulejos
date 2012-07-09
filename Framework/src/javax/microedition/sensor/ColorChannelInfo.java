package javax.microedition.sensor;

/**
 * ChannelInfo for color number from HiTechnic Color Sensor.
 * 
 * @author Lawrie Griffiths
 */
public class ColorChannelInfo extends NXTChannelInfo {
	public MeasurementRange[] getMeasurementRanges() {
		return new MeasurementRange[] {new MeasurementRange(0,17,1)};
	}

	public String getName() {
		return "com.hitechnic.color_number";
	}
}
