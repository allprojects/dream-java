package javareact.common.types;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;

import javareact.client.ClientEventForwarder;
import javareact.client.Subscriber;
import javareact.common.Consts;
import javareact.common.packets.EventPacket;
import javareact.common.packets.content.Subscription;
import javareact.common.utils.SerializablePredicate;

public class RemoteVar<T> implements Subscriber, UpdateProducer<T> {
  private T val;

  private final ClientEventForwarder forwarder;
  private final Set<UpdateConsumer> consumers = new HashSet<>();

  private final Queue<EventPacket> eventsQueue = new ArrayDeque<>();
  private int pendingAcks = 0;

  private final List<SerializablePredicate> constraints = new ArrayList<>();

  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  private final String host;
  private final String object;

  public RemoteVar(String host, String object, List<SerializablePredicate> constraints) {
    this.host = host;
    this.object = object;

    final Subscription<?> sub = new Subscription(host, object, constraints);
    forwarder = ClientEventForwarder.get();
    forwarder.addSubscription(this, sub);
  }

  public RemoteVar(String object, List<SerializablePredicate> constraints) {
    this(Consts.hostName, object, constraints);
  }

  public RemoteVar(String host, String object) {
    this(host, object, new ArrayList<SerializablePredicate>());
  }

  public RemoteVar(String object) {
    this(Consts.hostName, object);
  }

  public final synchronized T get() {
    return val;
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

  @Override
  public final synchronized void notifyUpdateFinished() {
    pendingAcks--;
    processNextEvent();
  }

  private void processNextEvent() {
    if (pendingAcks == 0 && !eventsQueue.isEmpty()) {
      final EventPacket nextPkt = eventsQueue.poll();
      val = (T) nextPkt.getEvent().getVal();
      sendEventPacketToListeners(nextPkt);
    }
  }

  private final void sendEventPacketToListeners(EventPacket evPkt) {
    if (!consumers.isEmpty()) {
      pendingAcks = consumers.size();
      consumers.forEach(c -> c.updateFromProducer(evPkt, this));
    } else {
      processNextEvent();
    }
  }

  @Override
  public final String getHost() {
    return host;
  }

  @Override
  public final String getObject() {
    return object;
  }

  @Override
  public final List<SerializablePredicate> getConstraints() {
    return constraints;
  }

  @Override
  public final void registerUpdateConsumer(UpdateConsumer consumer) {
    consumers.add(consumer);
  }

  @Override
  public final void unregisterUpdateConsumer(UpdateConsumer consumer) {
    consumers.remove(consumer);
  }

  @Override
  public UpdateProducer<T> filter(SerializablePredicate constraint) {
    return this; // TODO
  }

}
