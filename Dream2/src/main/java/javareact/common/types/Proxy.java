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
import javareact.common.SerializablePredicate;
import javareact.common.packets.EventPacket;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.Subscription;

public abstract class Proxy implements Subscriber {
  protected final ClientEventForwarder forwarder;

  private final Queue<EventPacket> eventsQueue = new ArrayDeque<EventPacket>();
  private final Set<ProxyChangeListener> pendingAcks = new HashSet<ProxyChangeListener>();

  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  final String host;
  final String object;
  final UUID proxyID;
  final Set<ProxyChangeListener> proxyChangeListeners = new HashSet<ProxyChangeListener>();
  final List<SerializablePredicate> constraints = new ArrayList<SerializablePredicate>();

  public Proxy(String host, String object, List<SerializablePredicate> constraints) {
    this.host = host;
    this.object = object;
    proxyID = UUID.randomUUID();

    final Subscription<?> sub = new Subscription(host, object, proxyID, constraints);
    forwarder = ClientEventForwarder.get();
    forwarder.addSubscription(this, sub);
  }

  @Override
  public synchronized void notifyEventReceived(EventPacket evPkt) {
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
    if (!proxyChangeListeners.isEmpty()) {
      pendingAcks.addAll(proxyChangeListeners);
      final EventProxyPair pair = new EventProxyPair(evPkt, this);
      proxyChangeListeners.forEach(l -> l.update(pair));
    } else {
      processNextEvent();
    }
  }

}
