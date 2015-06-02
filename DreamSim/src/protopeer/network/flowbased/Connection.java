package protopeer.network.flowbased;

import java.text.*;
import java.util.*;

import org.apache.log4j.*;

import protopeer.network.*;
import protopeer.util.quantities.*;

/**
 * For every transmission of a <code>Message</code>, a <code>Connection</code>
 * is established. It contains the used links and keeps track of how many data
 * is already transmitted as well as of the event which terminates the
 * connection.
 * 
 */
public class Connection {
	
	private static final Logger logger = Logger.getLogger(Connection.class);
	
	private enum Status {Pending, Transmitting, Finished, Cancelled}
	private Status status;
	
	/**
	 * Links involved in this connection
	 */
	private List<Link> links;
	
	/**
	 * Message transfered by this connection
	 */
	private Message message;
	
	/**
	 * Bandwidth of the connection, may vary over time
	 */
	private Bandwidth rate;

	/**
	 * The sending interface
	 */
	private FlowBasedNetworkInterface sourceInterface;
	
	/**
	 * The receiving interface
	 */
	private FlowBasedNetworkInterface destinationInterface;

	/**
	 * Amount of data which is already transmitted
	 */
	private Data dataTransmitted;
	
	/**
	 * Time of the most recent adjustment of the traffic rate 
	 * Used to calculate <code>dataTransmittedInByte</code>
	 */
	private Time lastAdjustmentTime;

	/**
	 * Time when the connection was established
	 */
	private Time establishmentTime;
	
	/**
	 * Constructs a new <code>Connection</code>
	 * 
	 * @param message 
	 *          the message which will be transmitted
	 *          throuhg this connection
	 * @param establishmentTime 
	 *          the time when this connection
	 *          is established, normally <code>currentTime</code>
	 * @param sourceInterface
	 *          the <code>NetworkInterface</code> where the
	 *          connection originates
	 * @param destinationInterface
	 *          the <code>NetworkInterface</code> where the
	 *          connection ends
	 */
	public Connection(Message message,
			FlowBasedNetworkInterface sourceInterface,
			FlowBasedNetworkInterface destinationInterface) {
		
		this.message = message;
		this.sourceInterface = sourceInterface;
		this.destinationInterface = destinationInterface;
		dataTransmitted = Data.ZERO;
		rate = Bandwidth.ZERO;
		status = Status.Pending;
	}
	
	/**
	 * Keeps a reference to the links and notifies all links
	 * that this connections starts to use them
	 * 
	 * @param links links involved in this connection
	 * @param now the current time
	 */
	public void startTransmission(List<Link> links, Time now) {
		this.links = links;
		establishmentTime = now;
		lastAdjustmentTime = now;
		status = Status.Transmitting;

		if (links != null) {
			Iterator<Link> it = links.iterator();
			while (it.hasNext()) {
				Link link = it.next();
				if (link != null) {
					link.addConnection(this);
				} else {
					it.remove();
					if (logger.isEnabledFor(Level.WARN)) {
						logger.warn("List of links to use contains \"null\"");
					}
				}
			}
		}

		if (links == null || links.size() == 0) {
			/*
			 * if no link is present, it does not make sense at all to calculate
			 * a bandwidth for that connection nor to compute the required time
			 * to transmit this message. 
			 */
			throw new NetworkRuntimeException("List of links to use is empty.");
		}

		if (logger.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer();
			NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
			((DecimalFormat) nf).setMaximumFractionDigits(4);
			sb.append(nf.format(Time.inSeconds(establishmentTime)));
			sb.append("s: Established connection from ");
			sb.append(sourceInterface.getNetworkAddress());
			sb.append(" to ");
			sb.append(destinationInterface.getNetworkAddress());
			logger.debug(sb);
		}
	}
	
	/**
	 * Notifies all links of this connection that
	 * it is terminated.
	 * 
	 * @param now the current time
	 */
	public void teardown(Time now) {
		assert status == Status.Transmitting;
		shutdown(now, Status.Finished);
	}

	public void cancel(Time now) {
		assert status == Status.Transmitting || status == Status.Pending;
		shutdown(now, Status.Cancelled);
	}
	
	private void shutdown(Time now, Status status) {
		if(links != null){
			for (Link link : links) {
				link.removeConnection(this);
			}
		}
		updateDataTransmitted(now);
		this.status = status;
		
		if (logger.isDebugEnabled()) {
			StringBuffer sb = new StringBuffer();
			NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
			((DecimalFormat) nf).setMaximumFractionDigits(4);
			sb.append(nf.format(Time.inSeconds(now)));
			sb.append("s: Removed connection from ");
			sb.append(sourceInterface.getNetworkAddress());
			sb.append(" to ");
			sb.append(destinationInterface.getNetworkAddress());
			sb.append(". ");
			sb.append(Data.inByte(message.getSize()));
			sb.append(" bytes transfered in ");
			Time time = now.subtract(establishmentTime);
			sb.append(nf.format(Time.inMilliseconds(time)));
			sb.append("ms.");
			logger.debug(sb);
		}

	}
	

	/**
	 * Sets the rate of this connection to <code>newRate</code>. Adjusts the
	 * the amount of data which is already transmitted up to <code>now</code> and 
	 * returns the this value.
	 * 
	 * @param newRate the new transmission rate of this connection
	 * @param now the current time
	 * @return the amount of data which the connection has 
	 *         transmitted up to <code>now</code>.
	 */
	public Data updateRate(Bandwidth newRate, Time now) {
		assert status == Status.Transmitting;
		updateDataTransmitted(now);
		rate = newRate;
		return dataTransmitted;
	}
	
	private void updateDataTransmitted(Time now) {
		if (status == Status.Transmitting) {
			assert now.isGreaterOrEqualTo(lastAdjustmentTime);
			dataTransmitted = getTransmittedData(now);
			lastAdjustmentTime = now;
		}
	}
	
	/**
	 * 
	 * @return the time required to complete the data transmission
	 *         if the rate does not change. 
	 */
	public Time getRemainingTime(Time now) {
		return Converter.getTime(getRemainingData(now),rate);
	}
	
	/**
	 * 
	 * @param now the current time
	 * @return the amount of data which is not yet transmitted
	 */
	public Data getRemainingData(Time now) {
		return message.getSize().subtract(getTransmittedData(now));
	}

	/**
	 * 
	 * @param now the current time
	 * @return the amount of data transmitted by this connection 
	 *         up to <code>now</code>
	 */
	public Data getTransmittedData(Time now) {
		assert now.isGreaterOrEqualTo(lastAdjustmentTime); // cannot look into the past
		
		if (status == Status.Transmitting) {
			Data d = Converter.getData(rate, now.subtract(lastAdjustmentTime));
			d = d.add(dataTransmitted);
			if (d.isGreaterThan(message.getSize())) {
				d = message.getSize();
			}
			return d;
		} else if (status == Status.Cancelled || status == Status.Finished) {
			return dataTransmitted;
		} else if (status == Status.Pending) {
			return Data.ZERO;
		}
		assert false; // this point should never be reached
		return Data.ZERO; 
	}
	
	/**
	 * Returns that interface of the connection which is not
	 * equal to <code>networkInterface</code>, i.e., the
	 * other "end" of the connection.
	 * 
	 * @param networkInterface one interface of this connection
	 * @return the other interface
	 */
	public FlowBasedNetworkInterface getOtherInterface(
			FlowBasedNetworkInterface networkInterface) {
		if (networkInterface.equals(destinationInterface)) {
			return sourceInterface;
		} else {
			return destinationInterface;
		}
	}

	/*
	 * Getter and setter ...
	 */

	/**
	 * 
	 * @return the current rate of the connection in byte/sec 
	 */
	public Bandwidth getRate() {
		return rate;
	}
	
	public NetworkAddress getSourceAddress() {
		return sourceInterface.getNetworkAddress();
	}
	
	public NetworkAddress getDestinationAddress() {
		return destinationInterface.getNetworkAddress();
	}
	
	
	public FlowBasedNetworkInterface getSourceInterface() {
		return sourceInterface;
	}

	public void setSourceInterface(FlowBasedNetworkInterface sourceInterface) {
		this.sourceInterface = sourceInterface;
	}

	public FlowBasedNetworkInterface getDestinationInterface() {
		return destinationInterface;
	}

	public void setDestinationInterface(
			FlowBasedNetworkInterface destinationInterface) {
		this.destinationInterface = destinationInterface;
	}
	
	public List<Link> getLinks() {
		return links;
	}
	
	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	public Message getMessage() {
		return message;
	}
	
	/**
	 * Mainly used for debugging purpose.
	 * 
	 * @return a <code>String</code> representation of this object
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer("Connection from ");
		sb.append(sourceInterface.getNetworkAddress());
		sb.append(" to ");
		sb.append(destinationInterface.getNetworkAddress());
		return sb.toString();
	}

}
