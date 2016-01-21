package dream.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dream.common.packets.content.Event;
import dream.common.packets.content.Subscription;
import polimi.reds.NodeDescriptor;

final class SubscriptionTable {
  private final Map<NodeDescriptor, Collection<Subscription>> subs = new HashMap<NodeDescriptor, Collection<Subscription>>();

  final void addSubscription(NodeDescriptor node, Subscription sub) {
    Collection<Subscription> subsList = subs.get(node);
    if (subsList == null) {
      subsList = new ArrayList<Subscription>();
      subs.put(node, subsList);
    }
    subsList.add(sub);
  }

  final void removeSubscription(NodeDescriptor node, Subscription sub) {
    Collection<Subscription> subsList = subs.get(node);
    if (subsList == null) return;
    subsList.remove(sub);
    if (subsList.isEmpty()) {
      subs.remove(node);
    }
  }

  final Map<NodeDescriptor, Integer> getMatchingNodes(Event ev) {
    Map<NodeDescriptor, Integer> result = new HashMap<NodeDescriptor, Integer>();
    for (NodeDescriptor node : subs.keySet()) {
      int count = 0;
      for (Subscription sub : subs.get(node)) {
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

  final void removeAllSubscriptionsFor(NodeDescriptor node) {
    subs.remove(node);
  }

  @Override
  public String toString() {
    return "SubscriptionTable [subs=" + subs + "]";
  }

}
