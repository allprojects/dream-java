package dream.common.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Subscription;

public enum DependencyGraph {
	instance;

	// Node -> set of nodes it directly depends on
	private final Map<String, Collection<String>> graph = new HashMap<>();
	// Sources
	private final Set<String> sources = new HashSet<>();

	public synchronized final void clear() {
		graph.clear();
		sources.clear();
	}

	public synchronized final void processAdv(Advertisement adv) {
		final String advSignature = adv.getSignature();
		sources.add(advSignature);
	}

	public synchronized final void processAdv(Advertisement adv, Set<Subscription<?>> subs) {
		final String advSignature = adv.getSignature();
		assert !subs.isEmpty();
		final Set<String> subSignatures = subs.stream().//
				map(sub -> sub.getSignature()).//
				collect(Collectors.toSet());
		graph.put(advSignature, subSignatures);
	}

	public synchronized final void processUnAdv(Advertisement adv) {
		// TODO manage unadvertisements
	}

	public synchronized final void processUnAdv(Advertisement adv, Set<Subscription<?>> subs) {
		// TODO manage unadvertisements
	}

	public synchronized final Map<String, Collection<String>> getGraph() {
		return graph;
	}

	public synchronized final Set<String> getSources() {
		return sources;
	}

}
