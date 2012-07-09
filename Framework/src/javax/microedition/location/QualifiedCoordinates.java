package javax.microedition.location;

public class QualifiedCoordinates extends Coordinates {
	
	private float horizontalAccuracy;
	private float verticalAccuracy;
	
	public QualifiedCoordinates(double latitude, double longitude,float altitude, float horizontalAccuracy, float verticalAccuracy) {
		super(latitude, longitude, altitude);
		this.horizontalAccuracy = horizontalAccuracy;
		this.verticalAccuracy = verticalAccuracy;
	}
	
	/**
	 * Returns the horizontal accuracy of the location in meters (1-sigma standard deviation).
	 * A value of Float.NaN means the horizontal accuracy could not be determined.
	 * The horizontal accuracy is the RMS (root mean square) of east accuracy (latitudinal error 
	 * in meters, 1-sigma standard deviation), north accuracy (longitudinal error in meters, 1-sigma).
	 * 
	 * @return the horizontal accuracy in meters. Float.NaN if this is not known
	 */
	public float getHorizontalAccuracy() {
		return this.horizontalAccuracy;
	}
	
	/**
	 * Returns the accuracy of the location in meters in vertical direction (orthogonal to ellipsoid surface, 
	 * 1-sigma standard deviation). A value of Float.NaN means the vertical accuracy could not be determined.
	 * 
	 * @return the vertical accuracy in meters. Float.NaN if this is not known.
	 */
	public float getVerticalAccuracy() {
		return this.verticalAccuracy;
	}
	
	/*
	 * TODO: This value is set in BTGPSLocationProvider. Unsure if it should be giving the radius
	 * in meters. Currently giving the whole thing without /2. 
	 */
	public void setHorizontalAccuracy(float horizontalAccuracy) {
		this.horizontalAccuracy = horizontalAccuracy;
	}
	
	/*
	 * TODO: This value is set in BTGPSLocationProvider. Unsure if it should be giving the radius
	 * in meters. Currently giving the whole thing without /2. 
	 */
	public void setVerticalAccuracy(float verticalAccuracy) {
		this.verticalAccuracy = verticalAccuracy;
	}
}
