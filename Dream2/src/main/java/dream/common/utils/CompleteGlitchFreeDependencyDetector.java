package dream.common.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A CompleteGlitchFreeDependencyDetector is a dependency detector used to
 * provide complete glitch freedom. For each source, the detector returns the
 * nodes that require to be locked during the propagation.
 *
 * A node needs to be locked for the propagation of a source s if: (1) it
 * depends on both s and another source s'; (2) there is another node n' that
 * depends on both s and s'.
 */
public class CompleteGlitchFreeDependencyDetector extends InterSourceDependencyDetector {
  private final Map<String, Set<String>> sharedExpressions = new HashMap<>();

  @Override
  public final void computeDataStructs() {
    computeSharedExpressions();
  }

  @Override
  public final synchronized Set<String> getNodesToLockFor(String source) {
    return sharedExpressions.containsKey(source) ? //
        sharedExpressions.get(source)
        : //
        new HashSet<>();
  }

  private final void computeSharedExpressions() {
    graph.getSources().//
        forEach(initExp -> sharedExpressions.put(initExp, computeSharedExpressionsFor(initExp)));
  }

  private final Set<String> computeSharedExpressionsFor(String initialDependency) {
    final Set<String> result = new HashSet<>();
    final Set<String> depNodes = dependencyClosure.get(initialDependency);
    if (depNodes.size() < 2) {
      return result;
    }
    for (final Entry<String, Set<String>> entry : dependencyClosure.entrySet()) {
      if (entry.getKey().equals(initialDependency)) {
        continue;
      }
      final Set<String> intersect = intersect(depNodes, entry.getValue());
      if (intersect.size() >= 2) {
        result.addAll(intersect);
      }
    }
    return result;
  }

  private final Set<String> intersect(Set<String> set1, Set<String> set2) {
    return set1.stream().//
        filter(elem -> set2.contains(elem)).//
        collect(Collectors.toSet());
  }

}
