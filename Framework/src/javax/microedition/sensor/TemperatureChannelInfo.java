package javax.microedition.sensor;

/**
 * ChannelInfo for Lego light sensor
 * 
 * @author Lawrie Griffiths
 */
public class TemperatureChannelInfo extends NXTChannelInfo {
	public MeasurementRange[] getMeasurementRanges() {
		return new MeasurementRange[] {new MeasurementRange(-30, 70, 1)};
	}

	public String getName() {
		return "temperature";
	}
	
	public Unit getUnit() {
		return Unit.getUnit("Celsius");
	}
}
