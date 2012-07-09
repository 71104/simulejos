package javax.microedition.sensor;

/**
 * ChannelInfo for LEGO sound sensor
 * 
 * @author Lawrie Griffiths
 */
public class SoundChannelInfo extends NXTChannelInfo {

	public MeasurementRange[] getMeasurementRanges() {
		return new MeasurementRange[] {new MeasurementRange(0, 100, 1)};
	}

	public String getName() {
		return "sound_intensity";
	}
	
	public Unit getUnit() {
		return Unit.getUnit("db");
	}
}
