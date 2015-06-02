package javareact.common.types;

import protopeer.Peer;
import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;

public class RemoteVar<T> extends Proxy {
  private T val;

  public RemoteVar(Peer peer, String host, String object) {
    super(peer, host, object);
  }

  public RemoteVar(Peer peer, String object) {
    super(peer, object);
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
