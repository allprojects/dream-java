package dream.common.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The FinalNodesDetector is responsible for computing the set of final nodes
 * for each source, that is to say the set of nodes that are reachable from a
 * given source and do not have any other node that deoends on them.
 */
public final class FinalNodesDetector {
  DependencyGraph depGraph = DependencyGraph.instance;

  // Source -> set of final nodes
  private final Map<String, Set<String>> finalNodes = new HashMap<>();

  public final synchronized void consolidate() {
    computeFinalNodes();
  }

  /**
   * Return the set of final nodes for the given source.
   *
   * @param source
   *          the source.
   * @return the set of final nodes for source.
   */
  public final synchronized Set<String> getFinalNodesFor(String source) {
    return finalNodes.get(source);
  }

  private final void computeFinalNodes() {
    finalNodes.clear();
    final Map<String, Set<String>> closure = DependencyGraphUtils.computeDependencyClosure();
    final Set<String> finalNodesSet = DependencyGraphUtils.computeFinalNodes();

    closure.entrySet().forEach(e -> {
      finalNodes.put(e.getKey(), intersect(finalNodesSet, e.getValue()));
    });
  }

  private final Set<String> intersect(Set<String> set1, Set<String> set2) {
    final Set<String> result = new HashSet<>(set1);
    result.retainAll(set2);
    return result;
  }

}
