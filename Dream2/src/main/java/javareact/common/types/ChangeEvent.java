package javareact.common.types;

import java.util.HashSet;
import java.util.Set;

import javareact.common.packets.content.Attribute;

public class ChangeEvent<T> implements ProxyChangeListener {

	private T latest;

	private Set<ChangeEventHandler<T>> handlers = new HashSet<ChangeEventHandler<T>>();

	public ChangeEvent(ProxyGenerator p) {
		p.getProxy().addProxyChangeListener(this);
	}

	public void addHandler(ChangeEventHandler<T> handler) {
		handlers.add(handler);
	}

	public void removeHandler(ChangeEventHandler<T> handler) {
		handlers.remove(handler);
	}

	private void notifyHandler(T oldValue) {
		handlers.forEach(h -> h.handle(oldValue, latest));
	}

	@Override
	public void update(EventProxyPair pair) {
		String method = pair.getProxy().method;
		Attribute<T> temp = pair.getEventPacket().getEvent()
				.getAttributeFor(method);
		System.out.println("update called");
		if (latest == null || !latest.equals(temp.getValue())) {
			T old = latest;
			latest = temp.getValue();
			notifyHandler(old);
		}
		pair.getProxy().notifyEventProcessed(this, pair.getEventPacket());
	}

}