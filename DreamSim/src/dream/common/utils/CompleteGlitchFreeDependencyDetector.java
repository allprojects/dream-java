package dream.common.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A CompleteGlitchFreeDependencyDetector is a dependency detector used to
 * provide complete glitch freedom. For each source, the detector returns the
 * nodes that require to be locked during the propagation and the node that
 * needs to request the lock.
 *
 * A node needs to be locked for the propagation of a source s if: (1) it
 * depends on both s and another source s'; (2) there is another node n' that
 * depends on both s and s'.
 */
public enum CompleteGlitchFreeDependencyDetector {
  instance;

  private final DependencyGraph graph = DependencyGraph.instance;
  private Map<String, Set<String>> dependencyClosure = new HashMap<>();

  private final Map<String, Set<String>> sharedNodes = new HashMap<>();
  private final Map<String, String> lockRequestNode = new HashMap<>();

  public final void consolidate() {
    dependencyClosure = DependencyGraphUtils.computeDependencyClosure();
    sharedNodes.clear();
    lockRequestNode.clear();
    computeSharedNodes();
  }

  /**
   * Return the nodes that require to be locked during the propagation of an
   * update originated at the given source.
   *
   * @param source
   *          the source.
   * @return the nodes that need to be locked during the propagation.
   */
  public final synchronized Set<String> getNodesToLockFor(String source) {
    return sharedNodes.containsKey(source) //
        ? sharedNodes.get(source) //
        : new HashSet<>();
  }

  /**
   * Return the node that is responsible to send a lock request for a given
   * source.
   *
   * @param source
   *          the source.
   * @return the node that need to request a lock during the propagation.
   */
  public final synchronized String getLockRequestNodeFor(String source) {
    return lockRequestNode.get(source);
  }

  private final void computeSharedNodes() {
    graph.getSources()//
        .forEach(source -> sharedNodes.put(source, computeSharedNodesFor(source)));
  }

  private final void computeLockRequestNodes() {
    graph.getSources()//
        .forEach(source -> lockRequestNode.put(source, computeLockRequestNodeFor(source)));
  }

  private final Set<String> computeSharedNodesFor(String source) {
    final Set<String> result = new HashSet<>();
    final Set<String> depNodes = dependencyClosure.get(source);
    if (depNodes.size() < 2) {
      return result;
    }
    dependencyClosure.entrySet().stream() //
        .filter(entry -> !entry.getKey().equals(source)) //
        .forEach(entry -> {
          final Set<String> intersect = intersect(depNodes, entry.getValue());
          if (intersect.size() >= 2) {
            result.addAll(intersect);
          }
        });
    return result;
  }

  private final String computeLockRequestNodeFor(String source) {
    // Select the nodes on which all the nodes to lock depend
    final Set<String> nodesToLock = sharedNodes.get(source);
    final List<String> candidateNodes = dependencyClosure.get(source).stream()//
        .filter(n -> dependencyClosure.get(n).containsAll(nodesToLock))//
        .collect(Collectors.toList());

    Collections.sort(candidateNodes, (n1, n2) -> {
      assert dependencyClosure.get(n1).contains(n2) || dependencyClosure.get(n2).contains(n1);
      return dependencyClosure.get(n1).contains(n2) ? -1 : 1;
    });

    assert !candidateNodes.isEmpty();
    return candidateNodes.get(0);
  }

  private final Set<String> intersect(Set<String> set1, Set<String> set2) {
    return set1.stream().//
        filter(elem -> set2.contains(elem)).//
        collect(Collectors.toSet());
  }

}
