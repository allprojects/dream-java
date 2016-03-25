package dream.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Subscription;
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
		final Collection<Advertisement> advsList = advs.get(node);
		if (advsList == null) {
			return;
		}
		advsList.remove(adv);
		if (advsList.isEmpty()) {
			advs.remove(node);
		}
	}

	final Set<NetworkAddress> getMatchingNodes(Subscription sub) {
		final Predicate<Advertisement> isAdvSat = adv -> adv.isSatisfiedBy(sub);
		final Predicate<NetworkAddress> hasAdvSat = node -> advs.get(node).stream().anyMatch(isAdvSat);
		return advs.keySet().stream().//
		    filter(hasAdvSat).//
		    collect(Collectors.toSet());
	}

	final void removeAllAdvertisementsFor(NetworkAddress node) {
		advs.remove(node);
	}

}
