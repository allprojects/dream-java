package javareact.common.types;

import java.util.HashSet;
import java.util.Set;

import javareact.common.packets.EventPacket;
import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.ValueType;

public class RemoteVar<T> extends Proxy implements Reactive<T> {
	private final Set<ReactiveChangeListener<T>> listeners = new HashSet<ReactiveChangeListener<T>>();
	private T val;

	public RemoteVar(String host, String object) {
		super(host, object);
	}

	public RemoteVar(String object) {
		super(object);
	}

	public final T get() {
		return val;
	}

	public final Proxy toProxyOfType(ValueType t) {
		switch (t) {
		case INT:
			return new IntegerProxy(this.host, this.object);
		case DOUBLE:
			return new DoubleProxy(this.host, this.object);
		case STRING:
			return new StringProxy(this.host, this.object);
		case BOOL:
			return new BooleanProxy(this.host, this.object);
		case LIST:
			return new ListProxy(this.host, this.object);
		default:
			return new Proxy(this.host, this.object) {
				@Override
				protected void processEvent(Event ev) {
					if (ev.hasAttribute(method)) {
						Attribute attr = ev.getAttributeFor(method);
						val = (T)attr.getValue();
					}
				}
			};
		}
	}

	@Override
	protected final void processEvent(Event ev) {
		if (ev.hasAttribute(method)) {
			Attribute attr = ev.getAttributeFor(method);
			val = (T)attr.getValue();
		}
	}

	@Override
	public T evaluate() {
		return this.get();
	}

	@Override
	public void addReactiveChangeListener(ReactiveChangeListener<T> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeReactiveChangeListener(ReactiveChangeListener<T> listener) {
		listeners.remove(listener);
	}

	private final void notifyListeners() {
		for (ReactiveChangeListener<T> listener : listeners) {
			listener.notifyReactiveChanged(val);
		}
	}
	
	@Override
	public final void notifyValueChanged(EventPacket evPkt) {
		super.notifyValueChanged(evPkt);
		notifyListeners();
	}

}
