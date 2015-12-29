package javareact.common.types;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javareact.client.ClientEventForwarder;
import javareact.common.Consts;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;

abstract class Observable implements ProxyGenerator {
  protected final String observableId;
  private final ClientEventForwarder forwarder;

  protected Observable(String observableId) {
    forwarder = ClientEventForwarder.get();
    this.observableId = observableId;
    sendAdvertisement();
  }

  private final void sendAdvertisement() {
    final Advertisement adv = new Advertisement(Consts.hostName, observableId);
    forwarder.advertise(adv, true);
  }

  protected synchronized void sendEvent(Attribute[] attributes) {
    final Event ev = new Event(Consts.hostName, observableId, attributes);
    final Set<String> computedFrom = new HashSet<String>();
    computedFrom.add(ev.getSignature());
    forwarder.sendEvent(UUID.randomUUID(), ev, computedFrom, false);
  }

}
