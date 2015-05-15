package javareact.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Subscription;
import protopeer.network.NetworkAddress;

final class AdvertisementTable {
  private final Map<NetworkAddress, Collection<Advertisement>> advs = new HashMap<NetworkAddress, Collection<Advertisement>>();

  final void addAdvertisement(NetworkAddress node, Advertisement adv) {
    Collection<Advertisement> advsList = advs.get(node);
    if (advsList == null) {
      advsList = new ArrayList<Advertisement>();
      advs.put(node, advsList);
    }
    advsList.add(adv);
  }

  final void removeAdvertisement(NetworkAddress node, Advertisement adv) {
    Collection<Advertisement> advsList = advs.get(node);
    if (advsList == null) return;
    advsList.remove(adv);
    if (advsList.isEmpty()) {
      advs.remove(node);
    }
  }

  final Set<NetworkAddress> getMatchingNodes(Subscription sub) {
    Set<NetworkAddress> nodes = new HashSet<NetworkAddress>();
    nodesLoop: for (NetworkAddress node : advs.keySet()) {
      for (Advertisement adv : advs.get(node)) {
        if (adv.isSatisfiedBy(sub)) {
          nodes.add(node);
          continue nodesLoop;
        }
      }
    }
    return nodes;
  }

  final void removeAllAdvertisementsFor(NetworkAddress node) {
    advs.remove(node);
  }

}
