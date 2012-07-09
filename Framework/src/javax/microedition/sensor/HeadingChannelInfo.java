package javax.microedition.sensor;

/**
 * Heading channel for HiTechnic compass
 * 
 * @author Lawrie Griffiths
 */
public class HeadingChannelInfo extends NXTChannelInfo {
	public int getDataLength() {
		return 9; // in bits
	}

	public MeasurementRange[] getMeasurementRanges() {
		return new MeasurementRange[]{new MeasurementRange(0,359,1)};
	}

	public String getName() {
		return "heading";
	}
	
	public Unit getUnit() {
		return Unit.getUnit("degree");
	}
}
