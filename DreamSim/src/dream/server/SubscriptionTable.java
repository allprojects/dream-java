package dream.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dream.common.packets.content.Event;
import dream.common.packets.content.Subscription;
import protopeer.network.NetworkAddress;

final class SubscriptionTable {
  private final Map<NetworkAddress, Collection<Subscription>> subs = new HashMap<>();

  final void addSubscription(NetworkAddress node, Subscription sub) {
    Collection<Subscription> subsList = subs.get(node);
    if (subsList == null) {
      subsList = new ArrayList<Subscription>();
      subs.put(node, subsList);
    }
    subsList.add(sub);
  }

  final void removeSubscription(NetworkAddress node, Subscription sub) {
    final Collection<Subscription> subsList = subs.get(node);
    if (subsList == null) {
      return;
    }
    subsList.remove(sub);
    if (subsList.isEmpty()) {
      subs.remove(node);
    }
  }

  final Map<NetworkAddress, Integer> getMatchingNodes(Event ev) {
    final Map<NetworkAddress, Integer> result = new HashMap<NetworkAddress, Integer>();
    for (final NetworkAddress node : subs.keySet()) {
      int count = 0;
      for (final Subscription sub : subs.get(node)) {
        if (sub.isSatisfiedBy(ev)) {
          count++;
        }
      }
      if (count != 0) {
        result.put(node, count);
      }
    }
    return result;
  }

  final void removeAllSubscriptionsFor(NetworkAddress node) {
    subs.remove(node);
  }

  @Override
  public String toString() {
    return "SubscriptionTable [subs=" + subs + "]";
  }

}
