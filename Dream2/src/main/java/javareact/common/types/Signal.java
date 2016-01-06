package javareact.common.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javareact.client.ClientEventForwarder;
import javareact.client.QueueManager;
import javareact.common.Consts;
import javareact.common.SerializablePredicate;
import javareact.common.ValueChangeListener;
import javareact.common.packets.EventPacket;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.Subscription;

public class Signal<T extends Serializable> implements ProxyGenerator<T>, TimeChangingValue<T>, ProxyChangeListener {
  private final Set<ValueChangeListener<T>> valueChangeListeners = new HashSet<ValueChangeListener<T>>();
  private final ClientEventForwarder clientEventForwarder;
  private final QueueManager queueManager = new QueueManager();
  private final String objectId;
  private final Supplier<T> evaluation;
  private final List<Proxy> dependentProxies = new ArrayList<Proxy>();

  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  protected RemoteVar<T> proxy;
  private final List<SerializablePredicate> constraints = new ArrayList<SerializablePredicate>();
  protected T val;

  public Signal(String objectId, Supplier<T> evaluation, ProxyGenerator... vars) {
    this.objectId = objectId;
    this.evaluation = evaluation;

    clientEventForwarder = ClientEventForwarder.get();

    for (final ProxyGenerator var : vars) {
      final Proxy varProxy = var.getProxy();
      dependentProxies.add(varProxy);
      varProxy.addProxyChangeListener(this);
    }

    sendAdvertisement();
  }

  /**
   * Private constructor used only for filters. Does not send advertisements and
   * does not retain any value.
   */
  private Signal(Signal<T> copy, SerializablePredicate constraint) {
    this.objectId = copy.objectId;
    this.evaluation = null;
    this.val = null;
    clientEventForwarder = ClientEventForwarder.get();

    constraints.addAll(copy.constraints);
    constraints.add(constraint);
  }

  @Override
  public void update(EventProxyPair eventProxyPair) {
    logger.finest("Update method invoked with " + eventProxyPair);
    final List<EventProxyPair> pairs = queueManager.processEventPacket(eventProxyPair, objectId + "@" + Consts.hostName);
    logger.finest("The queueManager returned the following pairs " + pairs);
    if (!pairs.isEmpty()) {
      logger.finest("Actual update");
      // Compute the new value
      try {
        val = evaluate();
        logger.finest("New value computed for the reactive object: " + val);
      } catch (final Exception e) {
        logger.info("Exception during the evaluation of the expression.");
        return;
      }

      // Notify local listeners
      logger.finest("Notifying registered listeners of the change.");
      valueChangeListeners.forEach(l -> l.notifyValueChanged(val));

      // Notify dependent objects
      logger.finest("Sending event to dependent reactive objects.");
      final Event<T> ev = new Event<T>(Consts.hostName, objectId, val);
      // Extract information from any of the packets received by the
      // QueueManager
      final EventPacket anyPkt = pairs.stream().findAny().get().getEventPacket();
      clientEventForwarder.sendEvent(anyPkt.getId(), ev, anyPkt.getInitialVar(), anyPkt.getFinalExpressions(), true);

      // Acknowledge the proxies
      logger.finest("Acknowledging the proxies.");
      pairs.forEach(pair -> pair.getProxy().notifyEventProcessed(this, pair.getEventPacket()));
    } else {
      logger.finest(objectId + ": update call but waiting: " + eventProxyPair.toString());
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

  private final void sendAdvertisement() {
    final Set<Subscription> subs = dependentProxies.stream().//
        map(p -> new Subscription(p.getHost(), p.getObject(), p.getProxyID(), p.getConstraints())).//
        collect(Collectors.toSet());
    clientEventForwarder.advertise(new Advertisement(Consts.hostName, objectId), subs, true);
  }

  private final String getInitialVar(Collection<EventProxyPair> pairs) {
    // All pairs are generated from the same initial var, so we can retrieve the
    // initial var from any event
    return pairs.stream().//
        findAny().//
        map(p -> p.getEventPacket().getInitialVar()).//
        get();
  }

  @Override
  public final synchronized T evaluate() {
    return evaluation.get();
  }

  public T get() {
    return val;
  }

  @Override
  public synchronized RemoteVar<T> getProxy() {
    if (proxy == null) {
      proxy = new RemoteVar<T>(objectId, constraints);
    }
    return proxy;
  }

  @Override
  public ProxyGenerator<T> filter(SerializablePredicate<T> constraint) {
    return new Signal<T>(this, constraint);
  }

}
