package javareact.common.types;

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
  private final Proxy[] dependentProxies;

  private final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  protected T val;

  public AbstractReactive(String name, Proxy... proxies) {
    this.name = name;
    dependentProxies = proxies;
    clientEventForwarder = ClientEventForwarder.get();
    for (Proxy proxy : proxies) {
      proxy.addProxyChangeListener(this);
    }
    sentAdvertisement();
  }

  @Override
  public void update(EventProxyPair eventProxyPair) {
    logger.finest("Update method invoked with " + eventProxyPair);
    List<EventProxyPair> pairs = queueManager.processEventPacket(eventProxyPair, Consts.hostName + "." + name);
    logger.finest("The queueManager returned the following pairs " + pairs);
    if (!pairs.isEmpty()) {
      // Compute the new value
      val = evaluate();
      logger.finest("New value computed for the reactive object: " + val);

      // Notify depending reactive objects
      EventProxyPair templatePair = pairs.get(0);
      EventPacket templatePkt = templatePair.getEventPacket();
      UUID id = templatePkt.getId();
      Set<String> computedFrom = getComputedFrom(pairs);
      Set<String> finalExpressions = templatePkt.getFinalExpressions();
      Event ev = null;
      try {
        // TODO consider methods other than get()!!!
        ev = new Event(Consts.hostName, name, Attribute.of("get", val));
      } catch (Exception e) {
        e.printStackTrace();
      }
      logger.finest("Sending event to dependent reactive objects.");
      clientEventForwarder.sendEvent(id, ev, computedFrom, finalExpressions, true);

      // Notify listeners
      logger.finest("Notifying registered listeners of the change.");
      notifyListeners();

      // Acknowledge the proxy
      for (EventProxyPair pair : pairs) {
        EventPacket evPkt = pair.getEventPacket();
        Proxy proxy = pair.getProxy();
        proxy.notifyEventProcessed(this, evPkt);
      }
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
    for (ReactiveChangeListener<T> listener : listeners) {
      listener.notifyReactiveChanged(val);
    }
  }

  private final void sentAdvertisement() {
    Set<Subscription> subs = new HashSet<Subscription>();
    for (Proxy proxy : dependentProxies) {
      Subscription sub = new Subscription(proxy.getHost(), proxy.getObject(), proxy.getProxyID(), new Constraint(proxy.getMethod()));
      subs.add(sub);
    }
    Advertisement adv = new Advertisement(Consts.hostName, name);
    clientEventForwarder.advertise(adv, subs, true);
  }

  private final Set<String> getComputedFrom(Collection<EventProxyPair> pairs) {
    Set<String> results = new HashSet<String>();
    for (EventProxyPair pair : pairs) {
      results.addAll(pair.getEventPacket().getComputedFrom());
    }
    return results;
  }

}