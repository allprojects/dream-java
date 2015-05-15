package javareact.common.types;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import protopeer.Peer;
import javareact.client.ClientEventForwarder;
import javareact.client.TrafficGeneratorPeerlet;
import javareact.common.Consts;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Event;

public abstract class Observable implements ProxyGenerator {
  protected Peer peer;
  protected final String observableId;
  private final ClientEventForwarder forwarder;
  private final boolean persistent;

  protected Observable(Peer peer, String observableId, boolean persistent) {
	this.peer = peer;
	forwarder = (ClientEventForwarder) peer.getPeerletOfType(ClientEventForwarder.class);
    this.observableId = observableId;
    this.persistent = persistent;
    sendAdvertisement();
  }

  protected Observable(Peer peer, String observableId) {
    this(peer, observableId, false);
  }

  private final void sendAdvertisement() {
    Advertisement adv = new Advertisement(Consts.hostPrefix + ((TrafficGeneratorPeerlet) peer.getPeerletOfType(TrafficGeneratorPeerlet.class)).getClientId(), observableId);
    forwarder.advertise(adv, true);
  }

  protected synchronized void sendEvent(Attribute[] attributes) {
    Event ev = new Event(Consts.hostPrefix + ((TrafficGeneratorPeerlet) peer.getPeerletOfType(TrafficGeneratorPeerlet.class)).getClientId(), observableId, persistent, attributes);
    Set<String> computedFrom = new HashSet<String>();
    computedFrom.add(ev.getSignature());
    forwarder.sendEvent(UUID.randomUUID(), ev, computedFrom, 0, false);
  }

}
