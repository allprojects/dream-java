package javareact.common.types;

import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.ValueType;

public class IntegerProxy extends RemoteVar<Integer> {
  public IntegerProxy(String host, String object) {
    super(host, object);
  }

  public IntegerProxy(String object) {
    super(object);
  }
}
