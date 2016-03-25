package dream.common.utils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The LocalityDetector is used to understand if a propagation is "local" or
 * not, that is to say, if it requires the acquisition of locks or not.
 */
public enum LocalityDetector {
	instance;

	DependencyGraph depGraph = DependencyGraph.instance;

	// Sources that need the acquisition of a lock for propagation
	private final Set<String> sourcesToLock = new HashSet<>();

	// Nodes that need to be locked in read mode in the case of atomic consistency
	private final Set<String> nodesToLockOnRead = new HashSet<>();

	public final synchronized void consolidate() {
		sourcesToLock.clear();
		nodesToLockOnRead.clear();
		computeSourcesToLock();
		computeNodesToLockOnRead();
	}

	public final synchronized boolean sourceRequiresLock(String source) {
		return sourcesToLock.contains(source);
	}

	public final synchronized boolean nodeRequiresReadLock(String node) {
		return nodesToLockOnRead.contains(node);
	}

	/**
	 * A source is local if all its dependent nodes are defined in the same host
	 * and do not depend on remote sources. In this case, the order of
	 * propagations can be decided with a local synchronization mechanism between
	 * sources.
	 */
	private final void computeSourcesToLock() {
		final Map<String, Set<String>> relevantSources = DependencyGraphUtils.computeRelevantSources();
		final Map<String, Set<String>> dependencyClosure = DependencyGraphUtils.computeDependencyClosure();

		final Predicate<String> dependsOnRemoteSource = //
		node -> relevantSources.get(node).stream() //
		    .anyMatch(source -> !source.split("@")[1].equals(node.split("@")[1]));

		final Predicate<String> someNodeDependsOnRemoteSource = //
		source -> dependencyClosure.get(source).stream().anyMatch(dependsOnRemoteSource);

		depGraph.getSources().stream() //
		    .filter(someNodeDependsOnRemoteSource) //
		    .collect(Collectors.toCollection(() -> sourcesToLock));
	}

	/**
	 * A node needs to be locked on read if it depends (directly or indirectly) on
	 * a node defined in a different host or if some of its (directly or
	 * indirectly) dependent nodes are defined in a different host.
	 */
	private final void computeNodesToLockOnRead() {
		final Map<String, Set<String>> dependencyClosure = DependencyGraphUtils.computeDependencyClosure();

		final Predicate<String> aDependentNodeIsRemote = //
		node -> dependencyClosure.get(node).stream() //
		    .anyMatch(depNode -> !depNode.split("@")[1].equals(node.split("@")[1]));

		final Predicate<String> dependsOnRemoteNode = node -> //
		dependencyClosure.entrySet().stream() //
		    .filter(e -> e.getKey().equals(node)) //
		    .filter(e -> e.getValue().contains(node)) //
		    .anyMatch(e -> !e.getKey().split("@")[1].equals(node.split("@")[1]));

		depGraph.getGraph().keySet().stream() //
		    .filter(aDependentNodeIsRemote.or(dependsOnRemoteNode)) //
		    .collect(Collectors.toCollection(() -> nodesToLockOnRead));
	}

}
