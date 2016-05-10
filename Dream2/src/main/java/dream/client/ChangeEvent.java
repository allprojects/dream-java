package dream.client;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import dream.common.packets.EventPacket;

public class ChangeEvent<T> implements UpdateConsumer {

	private T latest;
	private UUID latestId;

	private Set<ChangeEventHandler<T>> handlers = new HashSet<ChangeEventHandler<T>>();

	public ChangeEvent(UpdateProducer<T> p) {
		p.registerUpdateConsumer(this, p.getConstraints());
	}

	public void addHandler(ChangeEventHandler<T> handler) {
		handlers.add(handler);
	}

	public void removeHandler(ChangeEventHandler<T> handler) {
		handlers.remove(handler);
	}

	private void notifyHandler(T oldValue) {
		handlers.forEach(h -> h.handle(oldValue, this.latest));
	}

	@Override
	public void updateFromProducer(EventPacket packet, UpdateProducer<?> producer) {
		T newVal = (T) packet.getEvent().getVal();
		if (latest == null || !packet.getId().equals(latestId)) {
			T old = latest;
			latestId = packet.getId();
			this.latest = newVal;
			notifyHandler(old);
		}
		producer.notifyUpdateFinished();
	}

}