package javareact.common.types.observable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javareact.client.ClientEventForwarder;
import javareact.client.TrafficGeneratorPeerlet;
import javareact.common.Consts;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;
import protopeer.Peer;

public abstract class Observable {
  private final String observableId;
  private final ClientEventForwarder forwarder;
  private final boolean persistent;
  private final String hostId;
  private final Peer peer;

  protected Observable(Peer peer, String observableId, boolean persistent) {
    forwarder = (ClientEventForwarder) peer.getPeerletOfType(ClientEventForwarder.class);
    this.observableId = observableId;
    this.persistent = persistent;
    this.peer = peer;
    hostId = Consts.hostPrefix + ((TrafficGeneratorPeerlet) peer.getPeerletOfType(TrafficGeneratorPeerlet.class)).getClientId();
    sendAdvertisement();
  }

  protected Observable(Peer peer, String observableId) {
    this(peer, observableId, false);
  }

  private final void sendAdvertisement() {
    Advertisement adv = new Advertisement(observableId, hostId);
    forwarder.advertise(adv, true);
  }

  protected synchronized void sendEvent(Attribute[] attributes) {
    Event ev = new Event(observableId, hostId, persistent, attributes);
    Set<String> computedFrom = new HashSet<String>();
    computedFrom.add(ev.getSignature());
    forwarder.sendEvent(UUID.randomUUID(), ev, computedFrom, peer.getClock().getCurrentTime(), false);
  }

}
