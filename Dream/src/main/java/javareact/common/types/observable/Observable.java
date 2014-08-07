package javareact.common.types.observable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javareact.client.ClientEventForwarder;
import javareact.common.Consts;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;

public abstract class Observable {
  private final String observableId;
  private final ClientEventForwarder forwarder;
  private final boolean persistent;

  protected Observable(String observableId, boolean persistent) {
    forwarder = ClientEventForwarder.get();
    this.observableId = observableId;
    this.persistent = persistent;
    sendAdvertisement();
  }

  protected Observable(String observableId) {
    this(observableId, false);
  }

  private final void sendAdvertisement() {
    Advertisement adv = new Advertisement(observableId, Consts.hostName);
    forwarder.advertise(adv, true);
  }

  protected synchronized void sendEvent(Attribute[] attributes) {
    Event ev = new Event(observableId, Consts.hostName, persistent, attributes);
    Set<String> computedFrom = new HashSet<String>();
    computedFrom.add(ev.getSignature());
    forwarder.sendEvent(UUID.randomUUID(), ev, computedFrom, false);
  }

}
