package dream.common.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

	public synchronized final void addVar(String name) {
		sources.add(name);
	}

	public synchronized final void addSignal(String name, Collection<String> deps) {
		assert !deps.isEmpty();
		graph.put(name, deps);
	}

	public synchronized final Map<String, Collection<String>> getGraph() {
		return graph;
	}

	public synchronized final Set<String> getSources() {
		return sources;
	}

}
