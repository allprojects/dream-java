package dream.common.datatypes;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Logger;

import dream.client.ClientEventForwarder;
import dream.client.QueueManager;
import dream.common.ConsistencyType;
import dream.common.Consts;
import dream.common.packets.EventPacket;
import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Event;
import dream.common.packets.content.Subscription;
import dream.common.packets.locking.LockGrantPacket;

public class Signal<T extends Serializable> implements TimeChangingValue<T>, UpdateProducer<T>, UpdateConsumer, LockApplicant {
  private final Set<ValueChangeListener<T>> valueChangeListeners = new HashSet<>();

  // Management of local subscribers
  private final Set<UpdateConsumer> consumers = new HashSet<>();
  private final Queue<EventProducerPair> eventQueue = new ArrayDeque<>();
  private int pendingAcks = 0;
  private final Set<UpdateProducer> waitingProducers = new HashSet<>();

  private final ClientEventForwarder clientEventForwarder;
  private final QueueManager queueManager = new QueueManager();

  private final String host;
  private final String object;
  private final List<SerializablePredicate> constraints = new ArrayList<>();

  private final Supplier<T> evaluation;

  private UUID lockID = null;

  private T val;

  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  public Signal(String object, Supplier<T> evaluation, UpdateProducer... prods) {
    this.host = Consts.hostName;
    this.object = object;
    this.evaluation = evaluation;

    final Set<Subscription> subs = new HashSet<>();
    for (final UpdateProducer prod : prods) {
      prod.registerUpdateConsumer(this);
      subs.add(new Subscription(prod.getHost(), prod.getObject(), prod.getConstraints()));
    }

    clientEventForwarder = ClientEventForwarder.get();
    clientEventForwarder.advertise(new Advertisement(Consts.hostName, object), subs, true);
  }

  /**
   * Private constructor used only for filters. Does not send advertisements and
   * does not retain any value.
   */
  private Signal(Signal<T> copy, SerializablePredicate constraint) {
    this.host = copy.host;
    this.object = copy.object;
    this.evaluation = null;
    this.val = null;
    clientEventForwarder = ClientEventForwarder.get();

    constraints.addAll(copy.constraints);
    constraints.add(constraint);
  }

  private final synchronized void processNextUpdate() {
    if (pendingAcks == 0) {
      // Notify that the previous update has finished
      if (!waitingProducers.isEmpty()) {
        waitingProducers.forEach(prod -> prod.notifyUpdateFinished());
        waitingProducers.clear();
      }
      // Process the next packet, if any
      if (!eventQueue.isEmpty()) {
        processUpdate(eventQueue.poll());
      }
    }
  }

  private final void processUpdate(EventProducerPair update) {
    logger.finest("processTask method invoked with " + update);
    final List<EventProducerPair> pairs = queueManager.processEventPacket(update, object + "@" + Consts.hostName);
    logger.finest("The queueManager returned the following pairs " + pairs);

    if (!pairs.isEmpty()) {
      logger.finest("Actual update");
      // Extract information from any of the packets
      final EventPacket anyPkt = pairs.stream().findAny().get().getEventPacket();

      // Compute the new value
      try {
        val = evaluate();
        logger.finest("New value computed for the reactive object: " + val);
      } catch (final Exception e) {
        logger.info("Exception during the evaluation of the expression. Acknowledging the producers, releasing the locks, and returning.");
        pairs.forEach(pair -> pair.getUpdateProducer().notifyUpdateFinished());
        // Release locks, if needed
        if ((Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
            Consts.consistencyType == ConsistencyType.ATOMIC) && //
            anyPkt.getLockReleaseNodes().contains(object + "@" + host)) {
          clientEventForwarder.sendLockRelease(anyPkt.getId());
        }
        return;
      }

      // Notify value change listeners
      logger.finest("Notifying registered listeners of the change.");
      valueChangeListeners.forEach(l -> l.notifyValueChanged(val));

      // Notify local and remote dependent objects
      logger.finest("Sending event to dependent objects.");
      final Event<T> event = new Event<T>(Consts.hostName, object, val);
      // Notify remote subscribers
      clientEventForwarder.sendEvent(anyPkt.getId(), event, anyPkt.getSource());
      // Notify local subscribers
      if (!consumers.isEmpty()) {
        pairs.forEach(pair -> waitingProducers.add(pair.getUpdateProducer()));
        pendingAcks = consumers.size();
        final EventPacket newEvPkt = new EventPacket(event, anyPkt.getId(), anyPkt.getSource());
        newEvPkt.setLockReleaseNodes(anyPkt.getLockReleaseNodes());
        consumers.forEach(c -> c.updateFromProducer(newEvPkt, this));
      } else {
        // Acknowledge the producers if there are no pending acks
        logger.finest("Acknowledging the producers.");
        pairs.forEach(pair -> pair.getUpdateProducer().notifyUpdateFinished());
      }

      // Release locks, if needed
      if ((Consts.consistencyType == ConsistencyType.COMPLETE_GLITCH_FREE || //
          Consts.consistencyType == ConsistencyType.ATOMIC) && //
          anyPkt.getLockReleaseNodes().contains(object + "@" + host)) {
        clientEventForwarder.sendLockRelease(anyPkt.getId());
      }

    } else {
      logger.finest(object + ": update call but waiting: " + update);
    }
  }

  @Override
  public void addValueChangeListener(ValueChangeListener<T> listener) {
    valueChangeListeners.add(listener);
  }

  @Override
  public void removeValueChangeListener(ValueChangeListener<T> listener) {
    valueChangeListeners.remove(listener);
  }

  @Override
  public final synchronized T evaluate() {
    return evaluation.get();
  }

  public T get() {
    return val;
  }

  public T atomicGet() {
    acquireLock();
    // TODO: this should actually be a copy of the object
    final T currentVal = val;
    releaseLock();
    return currentVal;
  }

  private final synchronized void acquireLock() {
    if (Consts.consistencyType != ConsistencyType.ATOMIC) {
      return;
    }
    clientEventForwarder.sendReadOnlyLockRequest(object + "@" + host, this);
    while (lockID == null) {
      try {
        wait();
      } catch (final InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private final synchronized void releaseLock() {
    if (Consts.consistencyType != ConsistencyType.ATOMIC) {
      return;
    }
    clientEventForwarder.sendLockRelease(lockID);
    lockID = null;
  }

  @Override
  public UpdateProducer<T> filter(SerializablePredicate<T> constraint) {
    return new Signal<T>(this, constraint);
  }

  @Override
  public final synchronized void updateFromProducer(EventPacket packet, UpdateProducer producer) {
    final EventProducerPair pair = new EventProducerPair(packet, producer);
    eventQueue.add(pair);
    logger.finest("Method update called for event " + pair + ". Added to the queue.");
    if (eventQueue.size() == 1) {
      logger.finest("The element is the only one in the queue. Let's process it.");
      processNextUpdate();
    }

  }

  @Override
  public final synchronized void notifyUpdateFinished() {
    pendingAcks--;
    processNextUpdate();
  }

  @Override
  public void registerUpdateConsumer(UpdateConsumer consumer) {
    consumers.add(consumer);
  }

  @Override
  public void unregisterUpdateConsumer(UpdateConsumer consumer) {
    consumers.remove(consumer);
  }

  @Override
  public String getHost() {
    return host;
  }

  @Override
  public String getObject() {
    return object;
  }

  @Override
  public List<SerializablePredicate> getConstraints() {
    return constraints;
  }

  @Override
  public final synchronized void notifyLockGranted(LockGrantPacket lockGrant) {
    lockID = lockGrant.getLockID();
    notifyAll();
  }

}
