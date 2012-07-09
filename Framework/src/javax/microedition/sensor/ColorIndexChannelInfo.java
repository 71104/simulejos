package javax.microedition.sensor;

/**
 * ChannelInfo for color indexnumber from HiTechnic Color Sensor.
 * 
 * @author Lawrie Griffiths
 */
public class ColorIndexChannelInfo extends NXTChannelInfo {
	public int getRegister() {
		return 0x4c;
	}

	public MeasurementRange[] getMeasurementRanges() {
		return new MeasurementRange[] {new MeasurementRange(0,63,1)};
	}

	public String getName() {
		return "com.hitechnic.color_index_number";
	}

	public int getDataLength() {
		return 6;
	}
}
