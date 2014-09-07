package javareact.common.types;

import javareact.common.packets.EventPacket;

public class EventProxyPair {
  private final EventPacket eventPacket;
  private final Proxy proxy;

  public EventProxyPair(EventPacket eventPacket, Proxy proxy) {
    super();
    this.eventPacket = eventPacket;
    this.proxy = proxy;
  }

  public final EventPacket getEventPacket() {
    return eventPacket;
  }

  public final Proxy getProxy() {
    return proxy;
  }

  @Override
  public String toString() {
    return "EventProxyPair [eventPacket=" + eventPacket + ", proxy=" + proxy + "]";
  }

}
