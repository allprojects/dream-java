package javareact.common.types;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import protopeer.Peer;
import javareact.client.ClientEventForwarder;
import javareact.client.Subscriber;
import javareact.client.TrafficGeneratorPeerlet;
import javareact.common.Consts;
import javareact.common.packets.EventPacket;
import javareact.common.packets.content.Constraint;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.Subscription;

public abstract class Proxy implements Subscriber {
  private final ClientEventForwarder forwarder;
  private final Set<ProxyChangeListener> listeners = new HashSet<ProxyChangeListener>();

  private final Queue<EventPacket> eventsQueue = new ArrayDeque<EventPacket>();
  private final Set<ProxyChangeListener> pendingAcks = new HashSet<ProxyChangeListener>();

  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  protected final Peer peer;
  protected final String host;
  protected final String object;
  protected final String method = "get";
  private final UUID proxyID;

  public Proxy(Peer peer, String object) {
    this(peer, Consts.hostPrefix + ((TrafficGeneratorPeerlet) peer.getPeerletOfType(TrafficGeneratorPeerlet.class)).getClientId(), object);
  }

  public Proxy(Peer peer, String host, String object) {
	this.peer = peer;
    this.host = host;
    this.object = object;
    forwarder = (ClientEventForwarder) peer.getPeerletOfType(ClientEventForwarder.class);
    proxyID = UUID.randomUUID();
    Subscription sub = new Subscription(host, object, proxyID, new Constraint(method));
    Set<Subscription> subs = new HashSet<>();
    subs.add(sub);
    forwarder.addSubscriptions(this, subs);
  }

  final void addProxyChangeListener(ProxyChangeListener listener) {
    listeners.add(listener);
  }

  final void removeProxyChangeListener(ProxyChangeListener listener) {
    listeners.remove(listener);
  }

  @Override
  public final void notifyValueChanged(EventPacket evPkt) {
    eventsQueue.add(evPkt);
    logger.finest("Received event packet " + evPkt + ". Added to the queue.");
    if (eventsQueue.size() == 1) {
      logger.finest("The element is the only one in the queue. Let's process it.");
      processEvent(evPkt.getEvent());
      sendEventPacketToListeners(evPkt);
    }
  }

  /**
   * Method invoked by a ProxyChangeListener to acknowledge that the eventPacket has been processed. After receiving
   * this acknowledgement from all registered ProxyChangeListeners, the Proxy can safely start processing the next
   * EventPacket received (if any).
   * 
   * @param proxyChangeListener the ProxyChangeListener.
   * @param event the event processed by the proxy.
   */
  final void notifyEventProcessed(ProxyChangeListener proxyChangeListener, EventPacket event) {
    pendingAcks.remove(proxyChangeListener);
    if (pendingAcks.isEmpty() && !eventsQueue.isEmpty()) {
      assert (!eventsQueue.isEmpty());
      eventsQueue.poll();
      EventPacket nextPkt = eventsQueue.peek();
      if (nextPkt != null) {
        processEvent(nextPkt.getEvent());
        sendEventPacketToListeners(nextPkt);
      }
    }
  }

  protected abstract void processEvent(Event ev);

  private final void sendEventPacketToListeners(EventPacket evPkt) {
    pendingAcks.addAll(listeners);
    EventProxyPair pair = new EventProxyPair(evPkt, this);
    for (ProxyChangeListener listener : listeners) {
      listener.update(pair);
    }
  }

  final String getHost() {
    return host;
  }

  final String getObject() {
    return object;
  }

  final String getMethod() {
    return method;
  }

  final UUID getProxyID() {
    return proxyID;
  }

}