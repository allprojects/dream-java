package protopeer.network.flowbased;

import java.util.HashMap;
import java.util.Map;

import protopeer.time.EventScheduler;
import protopeer.util.quantities.Data;

/**
 * Measures the traffic that goes through the <code>link</code> given
 * in the constructor of this class. The method <code>evaluatePeriod()</code>
 * is intended to be called in regular intervals. Then, it returns the amount
 * of traffic transferred by this <code>link</code> during the last interval.
 * 
 */
public class TransmittedDataMeter implements ILinkListener {

	private Map<Connection, Data> perConnectionTraffic;
	private Data sumBytes;
	private Data sumBytesUntilLastInterval;
	private EventScheduler eventScheduler;

	public TransmittedDataMeter(EventScheduler scheduler, Link link) {
		this.eventScheduler = scheduler;
		link.addLinkListener(this);
		perConnectionTraffic = new HashMap<Connection, Data>();
		sumBytes = Data.ZERO;
		sumBytesUntilLastInterval = Data.ZERO;
	}
	
	@Override
	public void addedConnection(Connection connection) {
		perConnectionTraffic.put(connection, Data.ZERO);
	}

	@Override
	public void removedConnection(Connection connection) {
		addData(connection);
		perConnectionTraffic.remove(connection);
	}
	
	/**
	 * Calculates the traffic which was transmitted using this link since it
	 * was called the last time. This method is intended to be called in
	 * regular intervals. If so, it returns the amount of traffic which
	 * 
	 */
	public Data evaluatePeriod() {
		for (Connection connection : perConnectionTraffic.keySet()) {
			addData(connection);
		}
		Data bytesOfThisPeriod = sumBytes.subtract(sumBytesUntilLastInterval);

		sumBytesUntilLastInterval = sumBytes;
		return bytesOfThisPeriod;
	}
	
	/**
	 * Adds the data transmitted by the connection <code>connection</code>
	 * during the last interval to the counter of all bytes transmitted bytes
	 * <code>sumBytes</code>. Additionally, the entire data transmitted by
	 * that connection is saved to permit a calculation of the transmitted data
	 * during the following interval.
	 * 
	 * @param connection
	 */
	private void addData(Connection connection) {
		Data dataTransmitted = connection.getTransmittedData(eventScheduler.now());
		Data newTraffic = dataTransmitted.subtract(perConnectionTraffic.get(connection));
		sumBytes = sumBytes.add(newTraffic);
		perConnectionTraffic.put(connection, dataTransmitted);
	}
	
}
