package protopeer.queues;

import protopeer.network.*;

/**
 * 
 * A simple Queue for injecting message passing failures according to some
 * {@link MessageFailureModel}, preserves FIFO ordering.
 * 
 */
public class FailureInjectionQueue extends FIFOQueue {

	private MessageFailureModel messageFailureModel;

	public FailureInjectionQueue(MessageFailureModel messageFailureModel) {
		super();
		this.messageFailureModel = messageFailureModel;
	}

	@Override
	public void enqueue(Message message) {
		Message replacedMessage = messageFailureModel.replaceMessage(message);
		if (replacedMessage == null) {
			fireMessageDropped(message);
		} else {
			super.enqueue(message);
		}
	}

	public MessageFailureModel getMessageFailureModel() {
		return messageFailureModel;
	}

	public void setMessageFailureModel(MessageFailureModel messageFailureModel) {
		this.messageFailureModel = messageFailureModel;
	}
}
