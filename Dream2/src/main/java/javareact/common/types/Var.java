package javareact.common.types;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javareact.client.ClientEventForwarder;
import javareact.common.ConsistencyType;
import javareact.common.Consts;
import javareact.common.packets.EventPacket;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Event;
import javareact.common.utils.DependencyDetector;
import javareact.common.utils.SerializablePredicate;
import javareact.common.utils.WaitRecommendations;

public class Var<T extends Serializable> implements UpdateProducer<T> {
  protected final ClientEventForwarder forwarder;

  protected final String host;
  protected final String object;
  private final List<SerializablePredicate> constraints = new ArrayList<SerializablePredicate>();

  private final Set<UpdateConsumer> consumers = new HashSet<>();
  private final Queue<Object> waitingModifications = new ArrayDeque<>();
  private int pendingAcks = 0;

  protected T val;

  public Var(String object, T val) {
    this.forwarder = ClientEventForwarder.get();
    this.host = Consts.hostName;
    this.object = object;
    this.val = val;
    forwarder.advertise(new Advertisement(Consts.hostName, object), true);
  }

  /**
   * Private constructor used only for filters. Does not send advertisements and
   * does not retain any value.
   */
  private Var(Var<T> copy, SerializablePredicate<T> constraint) {
    this.forwarder = ClientEventForwarder.get();
    this.host = copy.host;
    this.object = copy.object;
    this.val = null;
    constraints.addAll(copy.constraints);
    constraints.add(constraint);
  }

  public final synchronized void set(T val) {
    final Supplier<T> supplier = () -> val;
    waitingModifications.add(supplier);
    if (waitingModifications.size() == 1) {
      processNextUpdate();
    }
  }

  public final synchronized void modify(Consumer<T> modification) {
    waitingModifications.add(modification);
    if (waitingModifications.size() == 1) {
      processNextUpdate();
    }
  }

  public final synchronized T get() {
    assert val != null;
    return val;
  }

  private final void processNextUpdate() {
    if (pendingAcks == 0 && !waitingModifications.isEmpty()) {
      final Object mod = waitingModifications.poll();
      // Apply modification
      if (mod instanceof Consumer) {
        final Consumer<T> consumer = (Consumer<T>) mod;
        consumer.accept(val);
      } else if (mod instanceof Supplier) {
        final Supplier<T> supplier = (Supplier<T>) mod;
        val = supplier.get();
      }

      // Propagate modification to local and remote subscribers
      final Event<? extends Serializable> ev = new Event(Consts.hostName, object, val);
      final UUID eventId = UUID.randomUUID();
      final EventPacket packet = new EventPacket(ev, eventId, ev.getSignature(), false);
      final Set<WaitRecommendations> recommendations = //
      Consts.consistencyType == ConsistencyType.GLITCH_FREE || Consts.consistencyType == ConsistencyType.ATOMIC //
          ? //
          DependencyDetector.instance.getWaitRecommendations(ev, ev.getSignature())
          : //
          new HashSet<>();
      recommendations.forEach(packet::addWaitRecommendations);

      pendingAcks = consumers.size();
      consumers.forEach(c -> c.updateFromProducer(packet, this));
      

      forwarder.sendEvent(eventId, ev, ev.getSignature(), recommendations, false);
    }

  }

  @Override
  public UpdateProducer<T> filter(SerializablePredicate<T> constraint) {
    return new Var<T>(this, constraint);
  }

  @Override
  public final void notifyUpdateFinished() {
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

}
