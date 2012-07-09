package javax.microedition.sensor;

/**
 * Implementation of the ChannelInfo interface for a proximity channel implemented by the
 * LEGO ultrasonic sensor.
 * 
 * @author Lawrie Griffiths
 */
public class UltrasonicChannelInfo extends NXTChannelInfo {
	public MeasurementRange[] getMeasurementRanges() {
		return new MeasurementRange[] {new MeasurementRange(1,200,1)};
	}

	public String getName() {
		return "proximity";
	}

	public int getScale() {
		return -2;
	}

	public Unit getUnit() {
		return Unit.getUnit("m");
	}
}
