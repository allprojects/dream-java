package javareact.token_service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javareact.common.packets.AdvertisementPacket;
import javareact.common.packets.content.Subscription;

/**
 * A FinalExpressionsDetector is used by the token service in case of atomic consistency. It detects all the final
 * expressions in a reactive graph. Final expressions travel with an event packet. The token service waits until all the
 * final expressions have been processed before releasing the token. To enable this, each server notifies the token
 * service when processing a final expression.
 */
final class FinalExpressionsDetector {
  // Initial expressions (pure observables)
  private final Set<String> initialExpressions = new HashSet<String>();
  // Expression E -> expressions that directly depend from E
  private final Map<String, Set<String>> dependencyGraph = new HashMap<String, Set<String>>();
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
    assert (finalExpressions.containsKey(expr));
    return finalExpressions.get(expr);
  }

  private final void processAdv(AdvertisementPacket advPkt) {
    String advSignature = advPkt.getAdvertisement().getSignature();
    if (!advPkt.containtsSubscriptions()) {
      initialExpressions.add(advSignature);
    } else {
      for (Subscription sub : advPkt.getSubscriptions()) {
        String subSignature = sub.getSignature();
        addDependency(subSignature, advSignature);
      }
    }
  }

  private final void processUnadv(AdvertisementPacket advPkt) {
    // TODO: manage unadvertisements
  }

  private final void addDependency(String expression, String depending) {
    Set<String> dependingList = dependencyGraph.get(expression);
    if (dependingList == null) {
      dependingList = new HashSet<String>();
      dependencyGraph.put(expression, dependingList);
    }
    dependingList.add(depending);
  }

  private final void computeFinalExpressions() {
    for (String initialExpr : initialExpressions) {
      computeFinalExpressionsFor(initialExpr);
    }
  }

  private final void computeFinalExpressionsFor(String initialExpr) {
    Map<String, Integer> finalExpressionsMap = new HashMap<String, Integer>();
    computeFinalExpressionsFor(initialExpr, finalExpressionsMap);
    finalExpressions.put(initialExpr, finalExpressionsMap);
  }

  private final void computeFinalExpressionsFor(String currentExpr, Map<String, Integer> results) {
    int count = getFinalLinksFor(currentExpr);
    if (count != 0) {
      results.put(currentExpr, count);
    }
    if (dependencyGraph.containsKey(currentExpr)) {
      for (String expr : dependencyGraph.get(currentExpr)) {
        computeFinalExpressionsFor(expr, results);
      }
    }
  }

  private final int getFinalLinksFor(String expr) {
    int count = 0;
    if (dependencyGraph.containsKey(expr)) {
      for (String e : dependencyGraph.get(expr)) {
        if (!dependencyGraph.containsKey(e)) {
          count++;
        } else {
          return 0;
        }
      }
    }
    return count;
  }
}
