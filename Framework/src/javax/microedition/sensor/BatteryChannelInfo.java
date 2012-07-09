package javax.microedition.sensor;

public class BatteryChannelInfo extends NXTChannelInfo {

	public MeasurementRange[] getMeasurementRanges() {
		return new MeasurementRange[] {new MeasurementRange(0, 10000, 1)};
	}

	public String getName() {
		return "battery_voltage";
	}

	public int getScale() {
		return -3;
	}
	
	public Unit getUnit() {
		return Unit.getUnit("V");
	}
}
