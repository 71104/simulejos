package javax.microedition.sensor;

/**
 * ChannelInfo for acceleration channels axis_x, axis_y and axis_z
 * as implemented by the Mindsensors range of acceleration sensors.
 * 
 * @author Lawrie Griffiths
 */
public class AccelerationChannelInfo extends NXTChannelInfo {
	public static final String[] axes = {"x", "y", "z"};
	private String axis;
	private int axis_number;
	
	public AccelerationChannelInfo(String axis) {	
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
		return 0x45 + (axis_number * 2);
	}

	public MeasurementRange[] getMeasurementRanges() {
		// TODO: Check measurement range
		return new MeasurementRange[]{new MeasurementRange(-3000,3000,1)};
	}

	public String getName() {
		return "axis_" + axis;
	}

	public Unit getUnit() {
		return Unit.getUnit("g");
	}

	public int getDataLength() {
		return 16;
	}
	
	public int getScale() {
		return -3; // Value is in mg
	}
}
