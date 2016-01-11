package javareact.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javareact.common.packets.AdvertisementPacket;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.Subscription;

final class DependencyDetector {
  private final Map<String, Collection<String>> dependencyGraph = new HashMap<>();
  private final Set<String> initialExpressions = new HashSet<>();

  // Expression -> set of initial expressions it (directly or indirectly)
  // depends on
  private final Map<String, Set<String>> initialDependency = new HashMap<>();

  // Stores the dependencies to compute expressions
  // Expression Expr -> Expression that led to the re-computation of Expr ->
  // Wait recommendations
  private final Map<String, Map<String, Set<WaitRecommendations>>> recommendations = new HashMap<>();

  final Set<WaitRecommendations> getWaitRecommendations(Event<?> event, String initialVar) {
    final Map<String, Set<WaitRecommendations>> innerMap = recommendations.get(event.getSignature());
    if (innerMap == null) {
      return new HashSet<>();
    }
    return innerMap.containsKey(initialVar) ? innerMap.get(initialVar) : new HashSet<>();
  }

  final void processAdvertisementPacket(AdvertisementPacket advPkt) {
    switch (advPkt.getAdvType()) {
    case ADV:
      processAdv(advPkt);
      break;
    case UNADV:
      processUnadv(advPkt);
      break;
    default:
      assert false : advPkt.getAdvType();
    }
  }

  final void consolidate() {
    recommendations.clear();
    computeRecommendations();
  }

  private final void processAdv(AdvertisementPacket advPkt) {
    final String advSignature = advPkt.getAdvertisement().getSignature();
    final Set<Subscription> subs = advPkt.getSubscriptions();
    if (!subs.isEmpty()) {
      final Set<String> subSignatures = subs.stream().//
          map(sub -> sub.getSignature()).//
          collect(Collectors.toSet());
      dependencyGraph.put(advSignature, subSignatures);
    } else {
      initialExpressions.add(advSignature);
    }
  }

  private final void processUnadv(AdvertisementPacket advPkt) {
    // TODO: manage unadvertisements
  }

  private final void computeRecommendations() {
    recommendations.clear();
    computeInitialDependencies();
    dependencyGraph.keySet().forEach(expr -> {
      initialDependency.get(expr).forEach(initialExpr -> storeRecommendationsFor(expr, initialExpr));
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
    return dependencyGraph.get(expr).stream().//
        filter(dep -> initialDependency.get(dep).contains(initialExpression)).//
        collect(Collectors.toSet());
  }

  private final void computeInitialDependencies() {
    initialDependency.clear();
    initialExpressions.forEach(expr -> {
      final HashSet<String> initialExpressionsSet = new HashSet<>();
      initialExpressionsSet.add(expr);
      initialDependency.put(expr, initialExpressionsSet);
    });
    dependencyGraph.keySet().forEach(expr -> initialDependency.put(expr, computeInitialDependenciesFor(expr)));
  }

  private final Set<String> computeInitialDependenciesFor(String expression) {
    final Set<String> newExpressions = new HashSet<>();
    final Set<String> accumulator = new HashSet<>();
    newExpressions.add(expression);
    computeInitialDependenciesFor(newExpressions, accumulator);
    return accumulator;
  }

  private final void computeInitialDependenciesFor(Set<String> newExpressions, Set<String> accumulator) {
    newExpressions.stream().//
        filter(initialExpressions::contains).//
        collect(() -> accumulator, Set::add, Set::addAll);

    final Set<String> newExpressionsToEvaluate = newExpressions.stream().//
        filter(expr -> !initialExpressions.contains(expr)).//
        map(dependencyGraph::get).//
        collect(HashSet::new, HashSet::addAll, HashSet::addAll);

    if (!newExpressionsToEvaluate.isEmpty()) {
      computeInitialDependenciesFor(newExpressionsToEvaluate, accumulator);
    }
  }

}
