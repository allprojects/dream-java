package javareact.common.types;

import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.ValueType;

public class RemoteVar<T> extends Proxy {
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

  @Override
  protected final void processEvent(Event ev) {
    if (ev.hasAttribute(method)) {
      Attribute attr = ev.getAttributeFor(method);
      val = (T)attr.getValue();
    }
  }

}
