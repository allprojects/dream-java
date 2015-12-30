package javareact.common.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javareact.client.ClientEventForwarder;
import javareact.client.QueueManager;
import javareact.common.Consts;
import javareact.common.packets.EventPacket;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Attribute;
import javareact.common.packets.content.Constraint;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.Subscription;

abstract class AbstractReactive<T> implements Reactive<T>, ProxyChangeListener {
  private final Set<ReactiveChangeListener<T>> listeners = new HashSet<ReactiveChangeListener<T>>();
  private final ClientEventForwarder clientEventForwarder;
  private final QueueManager queueManager = new QueueManager();
  protected final String name;
  private final List<Proxy> dependentProxies = new ArrayList<Proxy>();

  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  protected T val;

  public AbstractReactive(String name, Proxy... proxies) {
    this.name = name;
    clientEventForwarder = ClientEventForwarder.get();
    for (final Proxy proxy : proxies) {
      dependentProxies.add(proxy);
      proxy.addProxyChangeListener(this);
    }
    sentAdvertisement();
  }

  @Override
  public void update(EventProxyPair eventProxyPair) {
    logger.finest("Update method invoked with " + eventProxyPair);
    final List<EventProxyPair> pairs = queueManager.processEventPacket(eventProxyPair, Consts.hostName + "." + name);
    logger.finest("The queueManager returned the following pairs " + pairs);
    if (!pairs.isEmpty()) {
      System.out.println(name + ": actual update");
      // Compute the new value
      val = evaluate();
      logger.finest("New value computed for the reactive object: " + val);

      // Notify depending reactive objects
      final EventProxyPair templatePair = pairs.get(0);
      final EventPacket templatePkt = templatePair.getEventPacket();
      final UUID id = templatePkt.getId();
      final Set<String> computedFrom = getComputedFrom(pairs);
      final Set<String> finalExpressions = templatePkt.getFinalExpressions();
      Event ev = null;
      try {
        // TODO consider methods other than get()!!!
        ev = new Event(Consts.hostName, name, Attribute.of("get", val));
      } catch (final Exception e) {
        e.printStackTrace();
      }
      logger.finest("Sending event to dependent reactive objects.");
      clientEventForwarder.sendEvent(id, ev, computedFrom, finalExpressions, true);

      // Notify listeners
      logger.finest("Notifying registered listeners of the change.");
      notifyListeners();

      // Acknowledge the proxy
      pairs.forEach(pair -> pair.getProxy().notifyEventProcessed(this, pair.getEventPacket()));
    } else {
      System.out.println(name + ": update call but waiting: " + eventProxyPair.toString());
    }
  }

  @Override
  public void addReactiveChangeListener(ReactiveChangeListener<T> listener) {
    listeners.add(listener);
  }

  @Override
  public void removeReactiveChangeListener(ReactiveChangeListener<T> listener) {
    listeners.remove(listener);
  }

  private final void notifyListeners() {
    listeners.forEach(l -> l.notifyReactiveChanged(val));
  }

  private final void sentAdvertisement() {
    final Set<Subscription> subs = new HashSet<Subscription>();
    dependentProxies.forEach(p -> subs.add(new Subscription(p.getHost(), p.getObject(), p.getProxyID(), new Constraint(p.getMethod()))));
    final Advertisement adv = new Advertisement(Consts.hostName, name);
    clientEventForwarder.advertise(adv, subs, true);
  }

  private final Set<String> getComputedFrom(Collection<EventProxyPair> pairs) {
    final Set<String> results = new HashSet<String>();
    pairs.forEach(pair -> results.addAll(pair.getEventPacket().getComputedFrom()));
    results.add(Consts.hostName + "." + name);
    return results;
  }

  public T get() {
    return val;
  }

}
