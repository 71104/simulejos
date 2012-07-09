package javax.microedition.sensor;

/**
 * ChannelInfo for tilt channels rot_x, rot_y and rot_z
 * as implemented by the Mindsensors range of acceleration sensors.
 * 
 * @author Lawrie Griffiths
 */
public class TiltChannelInfo extends NXTChannelInfo {
	public static final String[] axes = {"x", "y", "z"};
	private String axis;
	private int axis_number;
	
	public TiltChannelInfo(String axis) {	
		this.axis = axis;
		axis_number = getAxisNumber(axis);
	}
	
	private int getAxisNumber(String axis) {
		for(int i=0;i<axes.length;i++) {
			if (axis.equals(axes[i])) return i;
		}
		return -1;
	}

	public int getRegister() {
		return 0x42 + axis_number;
	}

	public MeasurementRange[] getMeasurementRanges() {
		return new MeasurementRange[] {new MeasurementRange(0,255,1)};
	}

	public String getName() {
		return "rot_" + axis;
	}

	public Unit getUnit() {
		return Unit.getUnit("degree");
	}
	
	public int getOffset() {
		return 128;
	}
}
