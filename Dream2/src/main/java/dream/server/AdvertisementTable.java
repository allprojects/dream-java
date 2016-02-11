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
		final Collection<Advertisement> advsList = advs.get(node);
		if (advsList == null) {
			return;
		}
		advsList.remove(adv);
		if (advsList.isEmpty()) {
			advs.remove(node);
		}
	}

	final Set<NodeDescriptor> getMatchingNodes(Subscription sub) {
		final Predicate<Advertisement> isAdvSat = adv -> adv.isSatisfiedBy(sub);
		final Predicate<NodeDescriptor> hasAdvSat = node -> advs.get(node).stream().anyMatch(isAdvSat);
		return advs.keySet().stream().//
				filter(hasAdvSat).//
				collect(Collectors.toSet());
	}

	final void removeAllAdvertisementsFor(NodeDescriptor node) {
		advs.remove(node);
	}

}
