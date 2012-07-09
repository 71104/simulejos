package javax.microedition.location;

/**
 * The LocationListener represents a listener that receives events associated with a
 * particular LocationProvider. Applications implement this interface and register it with
 * a LocationProvider to obtain regular position updates.
 * <p>
 * When the listener is registered with a LocationProvider with some update period, the
 * implementation shall attempt to provide updates at the defined interval. If it isn't
 * possible to determine the location, e.g. because of the LocationProvider being
 * TEMPORARILY_UNAVAILABLE or OUT_OF_SERVICE or because the update period is too frequent
 * for the location method to provide updates, the implementation can send an update to
 * the listener that contains an 'invalid' Location instance.
 * <p>
 * The implementation shall use best effort to post the location updates at the specified
 * interval, but this timing is not guaranteed to be very exact (i.e. this is not an exact
 * timer facility for an application).
 * <p>
 * The application is responsible for any possible synchronization needed in the listener
 * methods.
 * <p>
 * The listener methods MUST return quickly and should not perform any extensive
 * processing. The method calls are intended as triggers to the application. Application
 * should do any necessary extensive processing in a separate thread and only use these
 * methods to initiate the processing.
 * @author BB
 */
public interface LocationListener {

	/**
	 * Called by the LocationProvider to which this listener is registered. This method
	 * will be called periodically according to the interval defined when registering the
	 * listener to provide updates of the current location.
	 *
	 * @param provider
	 *            the source of the event
	 * @param location
	 *            the location to which the event relates, i.e. the new position
	 *            providerStateChanged
	 */
	public void locationUpdated(LocationProvider provider, Location location);

	/**
	 * Called by the LocationProvider to which this listener is registered if the state of
	 * the LocationProvider has changed. These provider state changes are delivered to the
	 * application as soon as possible after the state of a provider changes. The timing
	 * of these events is not related to the period of the location updates.
	 * <p>
	 * If the application is subscribed to receive periodic location updates, it will
	 * continue to receive these regardless of the state of the LocationProvider. If the
	 * application wishes to stop receiving location updates for an unavailable provider,
	 * it should de-register itself from the provider.
	 *
	 * @param provider
	 *            the source of the event
	 * @param newState
	 *            the new state of the LocationProvider. This value is one of the
	 *            constants for the state defined in the LocationProvider class.
	 */
	public void providerStateChanged(LocationProvider provider, int newState);
}
