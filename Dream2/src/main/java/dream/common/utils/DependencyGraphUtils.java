package dream.common.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

class DependencyGraphUtils {

	/**
	 * Return, for each node, the set of sources it directly or indirectly
	 * depends on.
	 *
	 * @return the set of sources each node directly or indirectly depends on.
	 */
	static final Map<String, Set<String>> computeRelevantSources() {
		final Map<String, Set<String>> result = new HashMap<>();
		final DependencyGraph depGraph = DependencyGraph.instance;
		depGraph.getSources().forEach(s -> {
			final HashSet<String> sources = new HashSet<>();
			sources.add(s);
			result.put(s, sources);
		});
		depGraph.getGraph().keySet().//
				forEach(node -> result.put(node, computeRelevantSourcesFor(node, depGraph)));
		return result;
	}

	/**
	 * Return the set of final nodes in the propagation graph, that is to say,
	 * all the nodes that do not have any other nodes that depends on them.
	 *
	 * @return the set of final nodes in the propagation graph.
	 */
	static final Set<String> computeFinalNodes() {
		final Map<String, Collection<String>> graph = DependencyGraph.instance.getGraph();
		final Set<String> sources = DependencyGraph.instance.getSources();

		final Set<String> allNodes = new HashSet<>(graph.keySet());
		allNodes.addAll(sources);

		return allNodes.stream()//
				.filter(node -> graph.values().stream() //
						.allMatch(depNodes -> !depNodes.contains(node))) //
				.collect(Collectors.toSet());
	}

	/**
	 * Return, for each source, the set of nodes that directly or indirectly
	 * depend on it.
	 *
	 * @return for each source, the set of nodes that directly or indirectly
	 *         depend on it.
	 */
	static final Map<String, Set<String>> computeDependencyClosure() {
		final Map<String, Set<String>> result = new HashMap<>();
		final DependencyGraph depGraph = DependencyGraph.instance;
		depGraph.getSources().forEach(s -> result.put(s, computeDependencyClosureFor(s, depGraph)));
		return result;
	}

	private static final Set<String> computeDependencyClosureFor(String source, DependencyGraph depGraph) {
		final Set<String> result = new HashSet<>();
		final Set<String> newNodes = new HashSet<>();
		result.add(source);
		newNodes.add(source);
		computeDependencyClosureFor(newNodes, result, depGraph);
		return result;
	}

	private static final void computeDependencyClosureFor(Set<String> newNodes, Set<String> accumulator,
			DependencyGraph depGraph) {
		final Set<String> newNodesToEvaluate = new HashSet<>();
		newNodes.forEach(n -> {
			final Set<String> depNodes = dependentNodesFor(n, depGraph);
			depNodes.removeAll(accumulator);
			accumulator.addAll(depNodes);
			newNodesToEvaluate.addAll(depNodes);
		});
		if (!newNodesToEvaluate.isEmpty()) {
			computeDependencyClosureFor(newNodesToEvaluate, accumulator, depGraph);
		}
	}

	/**
	 * Return the set of nodes that directly depend on node.
	 */
	private static Set<String> dependentNodesFor(String node, DependencyGraph depGraph) {
		return depGraph.getGraph().entrySet().stream().//
				filter(e -> e.getValue().contains(node)).//
				map(e -> e.getKey()).//
				collect(Collectors.toSet());
	}

	private static final Set<String> computeRelevantSourcesFor(String node, DependencyGraph depGraph) {
		final Set<String> newNodes = new HashSet<>();
		final Set<String> accumulator = new HashSet<>();
		newNodes.add(node);
		computeRelevantSourcesFor(newNodes, accumulator, depGraph);
		return accumulator;
	}

	private static final void computeRelevantSourcesFor(Set<String> newNodes, Set<String> accumulator,
			DependencyGraph depGraph) {
		newNodes.stream().//
				filter(depGraph.getSources()::contains).//
				collect(() -> accumulator, Set::add, Set::addAll);

		final Set<String> newNodesToEvaluate = newNodes.stream().//
				filter(expr -> !depGraph.getSources().contains(expr)).//
				map(depGraph.getGraph()::get).//
				filter(x -> x != null).//
				collect(HashSet::new, HashSet::addAll, HashSet::addAll);

		if (!newNodesToEvaluate.isEmpty()) {
			computeRelevantSourcesFor(newNodesToEvaluate, accumulator, depGraph);
		}
	}

}
