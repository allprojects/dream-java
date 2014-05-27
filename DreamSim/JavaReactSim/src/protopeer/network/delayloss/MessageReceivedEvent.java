package protopeer.network.delayloss;

import protopeer.network.*;
import protopeer.time.*;

class MessageReceivedEvent extends Event {

	private Message message;

	private DelayLossNetworkInterface sourceInterface;

	private DelayLossNetworkInterface destinationInterface;
	
	private byte[] serializedMessage;

	public MessageReceivedEvent(DelayLossNetworkInterface sourceInterface,
			DelayLossNetworkInterface destinationInterface,
			Message message,
			byte[] serializedMessage) {
		super();
		this.message = message;
		this.sourceInterface= sourceInterface;
		this.destinationInterface = destinationInterface;
		this.serializedMessage=serializedMessage;
	}

	@Override
	public void execute() {
		destinationInterface.messageReceived(sourceInterface, message, serializedMessage);
	}
}
