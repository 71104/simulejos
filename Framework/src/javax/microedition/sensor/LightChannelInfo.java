package javax.microedition.sensor;

/**
 * ChannelInfo for Lego light sensor
 * 
 * @author Lawrie Griffiths
 */
public class LightChannelInfo extends NXTChannelInfo {
	public MeasurementRange[] getMeasurementRanges() {
		return new MeasurementRange[] {new MeasurementRange(0, 100, 1)};
	}

	public String getName() {
		return "luminance";
	}
}
