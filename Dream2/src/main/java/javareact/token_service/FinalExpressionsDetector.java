package javareact.token_service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javareact.common.packets.AdvertisementPacket;
import javareact.common.packets.content.Subscription;

/**
 * A FinalExpressionsDetector is used by the token service in case of atomic
 * consistency. It detects all the final expressions in a reactive graph. Final
 * expressions travel with an event packet. The token service waits until all
 * the final expressions have been processed before releasing the token. To
 * enable this, each server notifies the token service when processing a final
 * expression.
 */
final class FinalExpressionsDetector {
  // Initial expressions
  private final Set<String> initialExpressions = new HashSet<String>();
  // Expression E -> expressions that directly depend on E
  private final Map<String, Map<String, UUID>> dependencyGraph = new HashMap<String, Map<String, UUID>>();
  // Expression E -> final expressions generated from E
  private final Map<String, Map<String, Integer>> finalExpressions = new HashMap<String, Map<String, Integer>>();

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
    finalExpressions.clear();
    computeFinalExpressions();
  }

  final Map<String, Integer> getFinalExpressionsFor(String expr) {
    assert finalExpressions.containsKey(expr);
    return finalExpressions.get(expr);
  }

  private final void processAdv(AdvertisementPacket advPkt) {
    final String advSignature = advPkt.getAdvertisement().getSignature();
    final Set<Subscription> subs = advPkt.getSubscriptions();
    if (subs.isEmpty()) {
      initialExpressions.add(advSignature);
    } else {
      for (final Subscription sub : advPkt.getSubscriptions()) {
        final String subSignature = sub.getSignature();
        addDependency(subSignature, advSignature, sub.getProxyID());
      }
    }
  }

  private final void processUnadv(AdvertisementPacket advPkt) {
    // TODO: manage unadvertisements
  }

  private final void addDependency(String expression, String depending, UUID proxyID) {
    Map<String, UUID> dependingMap = dependencyGraph.get(expression);
    if (dependingMap == null) {
      dependingMap = new HashMap<String, UUID>();
      dependencyGraph.put(expression, dependingMap);
    }
    dependingMap.put(depending, proxyID);
  }

  private final void computeFinalExpressions() {
    for (final String initialExpr : initialExpressions) {
      computeFinalExpressionsFor(initialExpr);
    }
  }

  private final void computeFinalExpressionsFor(String initialExpr) {
    final Map<String, Integer> finalExpressionsMap = new HashMap<String, Integer>();
    computeFinalExpressionsFor(initialExpr, finalExpressionsMap);
    finalExpressions.put(initialExpr, finalExpressionsMap);
  }

  private final void computeFinalExpressionsFor(String currentExpr, Map<String, Integer> results) {
    final int count = getFinalLinksFor(currentExpr);
    if (count != 0) {
      results.put(currentExpr, count);
    }
    if (dependencyGraph.containsKey(currentExpr)) {
      for (final String expr : dependencyGraph.get(currentExpr).keySet()) {
        computeFinalExpressionsFor(expr, results);
      }
    }
  }

  private final int getFinalLinksFor(String expr) {
    int count = 0;
    final Set<UUID> alreadyUsedIds = new HashSet<UUID>();
    if (dependencyGraph.containsKey(expr)) {
      final Map<String, UUID> innerMap = dependencyGraph.get(expr);
      for (final String e : innerMap.keySet()) {
        final UUID proxyID = innerMap.get(e);
        if (alreadyUsedIds.contains(proxyID)) {
          continue;
        }
        if (!dependencyGraph.containsKey(e)) {
          alreadyUsedIds.add(proxyID);
          count++;
        } else {
          return 0;
        }
      }
    }
    return count;
  }
}
