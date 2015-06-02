package protopeer.network.flowbased;

import protopeer.time.*;

/**
 * The <code>TransmissionCompletedEvent</code> is executed when the
 * transmission of a message is finished. It notifies the network
 * interfaces of the sender and the receiver via the callback 
 * methods <code>messageSent()<code> and <code>messageReceived()</code>.
 * 
 */
public class TransmissionCompletedEvent extends Event {
	
	private FlowBasedNetworkModel networkModel;
	private Connection connection;
	
	public TransmissionCompletedEvent(
			FlowBasedNetworkModel networkModel,
			Connection connection) {
		
		this.networkModel = networkModel;
		this.connection = connection;
	}

	@Override
	public void execute() {
		networkModel.connectionCompleted(connection);
	}
	
	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
