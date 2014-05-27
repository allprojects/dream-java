package javareact.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Subscription;
import polimi.reds.NodeDescriptor;

final class AdvertisementTable {
  private final Map<NodeDescriptor, Collection<Advertisement>> advs = new HashMap<NodeDescriptor, Collection<Advertisement>>();

  final void addAdvertisement(NodeDescriptor node, Advertisement adv) {
    Collection<Advertisement> advsList = advs.get(node);
    if (advsList == null) {
      advsList = new ArrayList<Advertisement>();
      advs.put(node, advsList);
    }
    advsList.add(adv);
  }

  final void removeAdvertisement(NodeDescriptor node, Advertisement adv) {
    Collection<Advertisement> advsList = advs.get(node);
    if (advsList == null) return;
    advsList.remove(adv);
    if (advsList.isEmpty()) {
      advs.remove(node);
    }
  }

  final Set<NodeDescriptor> getMatchingNodes(Subscription sub) {
    Set<NodeDescriptor> nodes = new HashSet<NodeDescriptor>();
    nodesLoop: for (NodeDescriptor node : advs.keySet()) {
      for (Advertisement adv : advs.get(node)) {
        if (adv.isSatisfiedBy(sub)) {
          nodes.add(node);
          continue nodesLoop;
        }
      }
    }
    return nodes;
  }

  final void removeAllAdvertisementsFor(NodeDescriptor node) {
    advs.remove(node);
  }

}
