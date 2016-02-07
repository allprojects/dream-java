package dream.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import dream.common.packets.content.Event;
import dream.common.packets.content.Subscription;

final class ClientSubscriptionTable {
  private final Map<Subscriber, Collection<Subscription<?>>> subs = new HashMap<Subscriber, Collection<Subscription<?>>>();
  private final Collection<Subscription<?>> serverSubscriptions = new ArrayList<Subscription<?>>();

  final void addSubscription(Subscriber subscriber, Subscription<?> sub) {
    Collection<Subscription<?>> subsList = subs.get(subscriber);
    if (subsList == null) {
      subsList = new ArrayList<Subscription<?>>();
      subs.put(subscriber, subsList);
    }
    subsList.add(sub);
  }

  final void addSubscriptions(Subscriber subscriber, Collection<Subscription<?>> subs) {
    subs.forEach(sub -> addSubscription(subscriber, sub));
  }

  final void addServerSubscription(Subscription<?> sub) {
    serverSubscriptions.add(sub);
  }

  final void removeSubscription(Subscriber subscriber, Subscription<?> sub) {
    final Collection<Subscription<?>> subscriptions = subs.get(subscriber);
    if (subscriptions == null) {
      return;
    }
    subscriptions.remove(sub);
    if (subscriptions.isEmpty()) {
      subs.remove(subscriber);
    }
  }

  final void removeSubscriptions(Subscriber subscriber, Collection<Subscription<?>> subs) {
    subs.forEach(sub -> removeSubscription(subscriber, sub));
  }

  final void removeServerSubscription(Subscription<?> sub) {
    serverSubscriptions.remove(sub);
  }

  final Set<Subscriber> getMatchingSubscribers(Event<?> ev) {
    return getSubscribersWithAnySubscriptionMatching(sub -> sub.isSatisfiedBy(ev));
  }

  final Set<Subscriber> getSignatureOnlyMatchingSubscribers(Event<?> ev) {
    return getSubscribersWithAnySubscriptionMatching(sub -> sub.matchesOnlySignatureOf(ev) && !sub.isSatisfiedBy(ev));
  }

  private final Set<Subscriber> getSubscribersWithAnySubscriptionMatching(Predicate<Subscription<?>> predicate) {
    final Predicate<Subscriber> hasSubscriptionMatchingOnlySignature = subscriber -> subs.get(subscriber).stream().anyMatch(predicate);
    return subs.keySet().stream().filter(hasSubscriptionMatchingOnlySignature).collect(Collectors.toSet());
  }

  final boolean needsToDeliverToServer(Event<?> ev) {
    return serverSubscriptions.stream().anyMatch(sub -> sub.isSatisfiedBy(ev));
  }

  @Override
  public String toString() {
    return "ClientSubscriptionTable [\n subs=" + subs + "\n serverSubscriptions=" + serverSubscriptions + "\n]";
  }

}
