package javareact.common.types;

import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.ValueType;

public class BooleanProxy extends RemoteVar<Boolean> {
	public BooleanProxy(String host, String object) {
		super(host, object);
	}

	public BooleanProxy(String object) {
		super(object);
	}
}
