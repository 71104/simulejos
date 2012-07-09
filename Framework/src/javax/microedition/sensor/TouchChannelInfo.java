package javax.microedition.sensor;

/**
 * ChannelInfo for LEGO Touch sensor
 * 
 * @author Lawrie Griffiths
 */
public class TouchChannelInfo extends NXTChannelInfo {

	public MeasurementRange[] getMeasurementRanges() {
		return new MeasurementRange[] {new MeasurementRange(0, 1, 1)};
	}

	public String getName() {
		return "touch";
	}
}
