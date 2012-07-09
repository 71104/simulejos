package javax.microedition.sensor;

/**
 * ChannelInfo for color channels rgb_r, rgb_g and rgb_b
 * as implemented by the MHiTechnic color sensor
 * 
 * @author Lawrie Griffiths
 */
public class ColorRGBChannelInfo extends NXTChannelInfo {
	public static final String[] rgb = {"r", "g", "b"};
	private String color;
	private int rgb_number;
	
	public ColorRGBChannelInfo(String color) {	
		this.color = color;
		rgb_number = getRGBNumber(color);
	}
	
	private int getRGBNumber(String color) {
		for(int i=0;i<rgb.length;i++) {
			if (color.equals(rgb[i])) return i;
		}
		return -1;
	}
	
	public int getRegister() {
		return 0x43 + rgb_number;
	}

	public MeasurementRange[] getMeasurementRanges() {
		return new MeasurementRange[] {new MeasurementRange(0,255,1)};
	}

	public String getName() {
		return "rgb_" + color;
	}
}
