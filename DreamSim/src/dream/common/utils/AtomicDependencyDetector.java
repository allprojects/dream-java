package dream.common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum AtomicDependencyDetector {
	instance;

	protected final DependencyGraph graph = DependencyGraph.instance;

	protected Map<String, Set<String>> dependencyClosure = new HashMap<>();

	public final synchronized void consolidate() {
		dependencyClosure = DependencyGraphUtils.computeDependencyClosure();
	}

	/**
	 * Returns the nodes that require to be locked during the propagation of an
	 * update originated at the given source.
	 *
	 * @param source
	 *          the source.
	 * @return the nodes that need to be locked during the propagation.
	 */
	public Set<String> getNodesToLockFor(String source) {
		return dependencyClosure.get(source);
	}

}
