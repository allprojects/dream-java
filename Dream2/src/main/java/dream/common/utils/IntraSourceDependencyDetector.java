package dream.common.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import dream.common.packets.content.Event;

public enum IntraSourceDependencyDetector implements DependencyDetector {
  instance;

  private final DependencyGraph depGraph = DependencyGraph.instance;
  private Map<String, Set<String>> relevantSources = new HashMap<>();

  // Stores the dependencies to compute expressions
  // Expression Expr -> Initial expression that caused the recomputation ->
  // Wait recommendations
  private final Map<String, Map<String, Set<WaitRecommendations>>> recommendations = new HashMap<>();

  public synchronized final Set<WaitRecommendations> getWaitRecommendations(Event<?> event, String initialVar) {
    final Map<String, Set<WaitRecommendations>> innerMap = recommendations.get(event.getSignature());
    if (innerMap == null) {
      return new HashSet<>();
    }
    return innerMap.containsKey(initialVar) ? innerMap.get(initialVar) : new HashSet<>();
  }

  @Override
  public synchronized final void consolidate() {
    recommendations.clear();
    computeRecommendations();
  }

  private final void computeRecommendations() {
    recommendations.clear();
    relevantSources = DependencyGraphUtils.computeRelevantSources();
    depGraph.getGraph().keySet().forEach(expr -> {
      relevantSources.get(expr).forEach(initialExpr -> storeRecommendationsFor(expr, initialExpr));
    });
  }

  private final void storeRecommendationsFor(String expr, String initialExpr) {
    final Set<String> dependentSiblings = computeDependentSiblingsFor(expr, initialExpr);
    if (dependentSiblings.size() > 1) {
      dependentSiblings.forEach(sibling -> {
        Map<String, Set<WaitRecommendations>> recommendationsMap = recommendations.get(sibling);
        if (recommendationsMap == null) {
          recommendationsMap = new HashMap<>();
          recommendations.put(sibling, recommendationsMap);
        }
        final Set<WaitRecommendations> recommendationsSet = new HashSet<>();
        recommendationsMap.put(initialExpr, recommendationsSet);
        final WaitRecommendations wr = new WaitRecommendations(expr);
        recommendationsSet.add(wr);
        dependentSiblings.stream().//
            filter(e -> !e.equals(sibling)).//
            forEach(wr::addRecommendation);
      });
    }
  }

  /**
   * Compute the set of all expressions with the following properties:
   *
   * 1) expr directly depends on them
   *
   * 2) they directly or indirectly depend on the initialExpression
   */
  private final Set<String> computeDependentSiblingsFor(String expr, String initialExpression) {
    return depGraph.getGraph().get(expr).stream().//
        filter(dep -> relevantSources.get(dep).contains(initialExpression)).//
        collect(Collectors.toSet());
  }

}
