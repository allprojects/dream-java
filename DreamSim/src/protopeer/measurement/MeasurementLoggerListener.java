package protopeer.measurement;

/**
 * Called from the <code>MeasurementLogger</code> whenever a measurement epoch
 * starts or ends.
 * 
 * 
 */
public interface MeasurementLoggerListener {

	public abstract void measurementEpochEnded(MeasurementLog log, int epochNumber);

}
