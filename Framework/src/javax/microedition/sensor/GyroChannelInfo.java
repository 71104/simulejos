package javax.microedition.sensor;

/**
 * Heading channel for HiTechnic compass
 * 
 * @author Lawrie Griffiths
 */
public class GyroChannelInfo extends NXTChannelInfo {

	public MeasurementRange[] getMeasurementRanges() {
		return new MeasurementRange[]{new MeasurementRange(-360,369,1)};
	}

	public String getName() {
		return "angular_velocity";
	}
	
	public Unit getUnit() {
		return Unit.getUnit("degree/s");
	}
}
