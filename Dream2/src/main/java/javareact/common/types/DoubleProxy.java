package javareact.common.types;

import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.ValueType;

public class DoubleProxy extends RemoteVar<Double> {
  public DoubleProxy(String host, String object) {
    super(host, object);
  }
  
  public DoubleProxy(String object) {
    super(object);
  }
}
