package javareact.common.types;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javareact.client.ClientEventForwarder;
import javareact.client.Subscriber;
import javareact.common.Consts;
import javareact.common.SerializablePredicate;
import javareact.common.packets.EventPacket;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.Subscription;

public abstract class Proxy implements Subscriber {
  protected final ClientEventForwarder forwarder;
  private final Set<ProxyChangeListener> listeners = new HashSet<ProxyChangeListener>();

  private final Queue<EventPacket> eventsQueue = new ArrayDeque<EventPacket>();
  private final Set<ProxyChangeListener> pendingAcks = new HashSet<ProxyChangeListener>();
  private final List<SerializablePredicate> constraints = new ArrayList<SerializablePredicate>();

  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  protected final String host;
  protected final String object;
  private final UUID proxyID;

  public Proxy(String name, List<SerializablePredicate> constraints) {
    if (name.contains("@")) {
      final String[] s = name.split("@", 2);
      this.host = s[1];
      this.object = s[0];
    } else {
      this.host = Consts.hostName;
      this.object = name;
    }

    forwarder = ClientEventForwarder.get();
    proxyID = UUID.randomUUID();
    final Subscription<?> sub = new Subscription(host, object, proxyID, constraints);
    forwarder.addSubscription(this, sub);
  }

  public Proxy(String host, String object, List<SerializablePredicate> constraints) {
    this(object + "@" + host, constraints);
  }

  final void addProxyChangeListener(ProxyChangeListener listener) {
    listeners.add(listener);
  }

  final void removeProxyChangeListener(ProxyChangeListener listener) {
    listeners.remove(listener);
  }

  final List<SerializablePredicate> getConstraints() {
    return constraints;
  }

  @Override
  public synchronized void notifyValueChanged(EventPacket evPkt) {
    eventsQueue.add(evPkt);
    logger.finest("Received event packet " + evPkt + ". Added to the queue.");
    if (eventsQueue.size() == 1) {
      logger.finest("The element is the only one in the queue. Let's process it.");
      processNextEvent();
    }
  }

  /**
   * Method invoked by a ProxyChangeListener to acknowledge that the eventPacket
   * has been processed. After receiving this acknowledgement from all
   * registered ProxyChangeListeners, the Proxy can safely start processing the
   * next EventPacket received (if any).
   *
   * @param proxyChangeListener
   *          the ProxyChangeListener.
   * @param event
   *          the event processed by the proxy.
   */
  final synchronized void notifyEventProcessed(ProxyChangeListener proxyChangeListener, EventPacket event) {
    pendingAcks.remove(proxyChangeListener);
    processNextEvent();
  }

  private void processNextEvent() {
    if (pendingAcks.isEmpty() && !eventsQueue.isEmpty()) {
      final EventPacket nextPkt = eventsQueue.poll();
      processEvent(nextPkt.getEvent());
      sendEventPacketToListeners(nextPkt);
    }
  }

  protected abstract void processEvent(Event<?> ev);

  private final void sendEventPacketToListeners(EventPacket evPkt) {
    if (!listeners.isEmpty()) {
      pendingAcks.addAll(listeners);
      final EventProxyPair pair = new EventProxyPair(evPkt, this);
      listeners.forEach(l -> l.update(pair));
    } else {
      processNextEvent();
    }
  }

  final String getHost() {
    return host;
  }

  final String getObject() {
    return object;
  }

  final UUID getProxyID() {
    return proxyID;
  }

}
