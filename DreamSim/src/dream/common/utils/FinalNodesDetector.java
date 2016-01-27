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
public enum FinalNodesDetector {
  instance;

  DependencyGraph depGraph = DependencyGraph.instance;

  // Source -> set of final nodes
  private final Map<String, Set<String>> finalNodes = new HashMap<>();

  public final synchronized void consolidate() {
    finalNodes.clear();
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
    final Map<String, Set<String>> closure = DependencyGraphUtils.computeDependencyClosure();
    final Set<String> finalNodesSet = DependencyGraphUtils.computeFinalNodes();

    depGraph.getSources().forEach(s -> {
      finalNodes.put(s, intersect(finalNodesSet, closure.get(s)));
    });
  }

  private final Set<String> intersect(Set<String> set1, Set<String> set2) {
    final Set<String> result = new HashSet<>(set1);
    result.retainAll(set2);
    return result;
  }

}
