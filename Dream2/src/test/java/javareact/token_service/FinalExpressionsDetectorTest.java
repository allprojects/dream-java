package javareact.token_service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import javareact.common.packets.AdvertisementPacket;
import javareact.common.packets.content.AdvType;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Subscription;

public class FinalExpressionsDetectorTest {

  private static final String hostId = "hostId";
  private static final String atHostId = "@" + hostId;

  @Test
  public void singleExpressionTest() {
    // A
    final FinalExpressionsDetector finalExpressionsDetector = new FinalExpressionsDetector();
    generateAdvertisementPacket(finalExpressionsDetector, "A");
    finalExpressionsDetector.consolidate();

    final Map<String, Integer> results = finalExpressionsDetector.getFinalExpressionsFor("A" + atHostId);
    assertEquals(results.size(), 0);
  }

  @Test
  public void simpleDependencyTest() {
    // A
    // B = f(A)
    final FinalExpressionsDetector finalExpressionsDetector = new FinalExpressionsDetector();
    generateAdvertisementPacket(finalExpressionsDetector, "A");
    generateAdvertisementPacket(finalExpressionsDetector, "B", "A");
    finalExpressionsDetector.consolidate();

    final Map<String, Integer> results = finalExpressionsDetector.getFinalExpressionsFor("A" + atHostId);
    assertEquals(results.size(), 1);
    assertTrue(results.containsKey("A" + atHostId));
    assertTrue(results.get("A" + atHostId) == 1);
  }

  @Test
  public void simpleDependencyTest2() {
    // A
    // B = f(A)
    // C = f(B)
    // D = f(C)
    final FinalExpressionsDetector finalExpressionsDetector = new FinalExpressionsDetector();
    generateAdvertisementPacket(finalExpressionsDetector, "A");
    generateAdvertisementPacket(finalExpressionsDetector, "B", "A");
    generateAdvertisementPacket(finalExpressionsDetector, "C", "B");
    generateAdvertisementPacket(finalExpressionsDetector, "D", "C");
    finalExpressionsDetector.consolidate();

    final Map<String, Integer> results = finalExpressionsDetector.getFinalExpressionsFor("A" + atHostId);
    assertEquals(results.size(), 1);
    assertTrue(results.containsKey("C" + atHostId));
    assertTrue(results.get("C" + atHostId) == 1);
  }

  @Test
  public void simpleDependencyTest3() {
    // A
    // B = f(A)
    // C = f(A)
    final FinalExpressionsDetector finalExpressionsDetector = new FinalExpressionsDetector();
    generateAdvertisementPacket(finalExpressionsDetector, "A");
    generateAdvertisementPacket(finalExpressionsDetector, "B", "A");
    generateAdvertisementPacket(finalExpressionsDetector, "C", "A");
    finalExpressionsDetector.consolidate();

    final Map<String, Integer> results = finalExpressionsDetector.getFinalExpressionsFor("A" + atHostId);
    assertEquals(results.size(), 1);
    assertTrue(results.containsKey("A" + atHostId));
    assertTrue(results.get("A" + atHostId) == 2);
  }

  @Test
  public void doubleDependencyTest() {
    // A
    // B = f(A)
    // C = f(B)
    // D = f(B)
    final FinalExpressionsDetector finalExpressionsDetector = new FinalExpressionsDetector();
    generateAdvertisementPacket(finalExpressionsDetector, "A");
    generateAdvertisementPacket(finalExpressionsDetector, "B", "A");
    generateAdvertisementPacket(finalExpressionsDetector, "C", "B");
    generateAdvertisementPacket(finalExpressionsDetector, "D", "B");
    finalExpressionsDetector.consolidate();

    final Map<String, Integer> results = finalExpressionsDetector.getFinalExpressionsFor("A" + atHostId);
    assertEquals(results.size(), 1);
    assertTrue(results.containsKey("B" + atHostId));
    assertTrue(results.get("B" + atHostId) == 2);
  }

  @Test
  public void tripleDependencyTest() {
    // A
    // B = f(A)
    // C = f(B)
    // D = f(B)
    // E = f(D)
    final FinalExpressionsDetector finalExpressionsDetector = new FinalExpressionsDetector();
    generateAdvertisementPacket(finalExpressionsDetector, "A");
    generateAdvertisementPacket(finalExpressionsDetector, "B", "A");
    generateAdvertisementPacket(finalExpressionsDetector, "C", "B");
    generateAdvertisementPacket(finalExpressionsDetector, "D", "B");
    generateAdvertisementPacket(finalExpressionsDetector, "E", "D");
    finalExpressionsDetector.consolidate();

    final Map<String, Integer> results = finalExpressionsDetector.getFinalExpressionsFor("A" + atHostId);
    assertEquals(results.size(), 1);
    assertTrue(results.containsKey("D" + atHostId));
    assertTrue(results.get("D" + atHostId) == 1);
  }

  @Test
  public void treeDependencyTest() {
    // A
    // B = f(A)
    // C = f(A)
    // D = f(B)
    // E = f(B)
    // F = f(C)
    // G = f(C)
    final FinalExpressionsDetector finalExpressionsDetector = new FinalExpressionsDetector();
    generateAdvertisementPacket(finalExpressionsDetector, "A");
    generateAdvertisementPacket(finalExpressionsDetector, "B", "A");
    generateAdvertisementPacket(finalExpressionsDetector, "C", "A");
    generateAdvertisementPacket(finalExpressionsDetector, "D", "B");
    generateAdvertisementPacket(finalExpressionsDetector, "E", "B");
    generateAdvertisementPacket(finalExpressionsDetector, "F", "C");
    generateAdvertisementPacket(finalExpressionsDetector, "G", "C");
    finalExpressionsDetector.consolidate();

    final Map<String, Integer> results = finalExpressionsDetector.getFinalExpressionsFor("A" + atHostId);
    assertEquals(results.size(), 2);
    assertTrue(results.containsKey("B" + atHostId));
    assertTrue(results.containsKey("C" + atHostId));
    assertTrue(results.get("B" + atHostId) == 2);
    assertTrue(results.get("C" + atHostId) == 2);
  }

  @Test
  public void treeDependencyTest2() {
    // A
    // B = f(A)
    // C = f(A)
    // D = f(B)
    // E = f(B)
    // F = f(C)
    // G = f(C)
    // H = f(G)
    final FinalExpressionsDetector finalExpressionsDetector = new FinalExpressionsDetector();
    generateAdvertisementPacket(finalExpressionsDetector, "A");
    generateAdvertisementPacket(finalExpressionsDetector, "B", "A");
    generateAdvertisementPacket(finalExpressionsDetector, "C", "A");
    generateAdvertisementPacket(finalExpressionsDetector, "D", "B");
    generateAdvertisementPacket(finalExpressionsDetector, "E", "B");
    generateAdvertisementPacket(finalExpressionsDetector, "F", "C");
    generateAdvertisementPacket(finalExpressionsDetector, "G", "C");
    generateAdvertisementPacket(finalExpressionsDetector, "H", "G");
    finalExpressionsDetector.consolidate();

    final Map<String, Integer> results = finalExpressionsDetector.getFinalExpressionsFor("A" + atHostId);
    assertEquals(results.size(), 2);
    assertTrue(results.containsKey("B" + atHostId));
    assertTrue(results.containsKey("G" + atHostId));
    assertTrue(results.get("B" + atHostId) == 2);
    assertTrue(results.get("G" + atHostId) == 1);
  }

  @Test
  public void graphDependencyTest() {
    // A
    // B
    // C = f(A, B)
    // D = f(A, B)
    // E = f(C, D)
    final FinalExpressionsDetector finalExpressionsDetector = new FinalExpressionsDetector();
    generateAdvertisementPacket(finalExpressionsDetector, "A");
    generateAdvertisementPacket(finalExpressionsDetector, "B");
    generateAdvertisementPacket(finalExpressionsDetector, "C", "A", "B");
    generateAdvertisementPacket(finalExpressionsDetector, "D", "A", "B");
    generateAdvertisementPacket(finalExpressionsDetector, "E", "C", "D");
    finalExpressionsDetector.consolidate();

    final Map<String, Integer> resultsA = finalExpressionsDetector.getFinalExpressionsFor("A" + atHostId);
    assertEquals(resultsA.size(), 2);
    assertTrue(resultsA.containsKey("C" + atHostId));
    assertTrue(resultsA.containsKey("D" + atHostId));
    assertTrue(resultsA.get("C" + atHostId) == 1);
    assertTrue(resultsA.get("D" + atHostId) == 1);

    final Map<String, Integer> resultsB = finalExpressionsDetector.getFinalExpressionsFor("B" + atHostId);
    assertEquals(resultsB.size(), 2);
    assertTrue(resultsB.containsKey("C" + atHostId));
    assertTrue(resultsB.containsKey("D" + atHostId));
    assertTrue(resultsB.get("C" + atHostId) == 1);
    assertTrue(resultsB.get("D" + atHostId) == 1);
  }

  @Test
  public void triangularDependencyTest() {
    // A
    // B = f(A)
    // C = f(A, B)
    final FinalExpressionsDetector finalExpressionsDetector = new FinalExpressionsDetector();
    generateAdvertisementPacket(finalExpressionsDetector, "A");
    generateAdvertisementPacket(finalExpressionsDetector, "B", "A");
    generateAdvertisementPacket(finalExpressionsDetector, "C", "A", "B");
    finalExpressionsDetector.consolidate();

    final Map<String, Integer> results = finalExpressionsDetector.getFinalExpressionsFor("A" + atHostId);
    assertEquals(results.size(), 1);
    assertTrue(results.containsKey("B" + atHostId));
    assertTrue(results.get("B" + atHostId) == 1);
  }

  @Test
  public void cycleDependencyTest() {
    // A
    // B = f(A)
    // C = f(A)
    // D = f(B, C)
    final FinalExpressionsDetector finalExpressionsDetector = new FinalExpressionsDetector();
    generateAdvertisementPacket(finalExpressionsDetector, "A");
    generateAdvertisementPacket(finalExpressionsDetector, "B", "A");
    generateAdvertisementPacket(finalExpressionsDetector, "C", "A");
    generateAdvertisementPacket(finalExpressionsDetector, "D", "B", "C");
    finalExpressionsDetector.consolidate();

    final Map<String, Integer> results = finalExpressionsDetector.getFinalExpressionsFor("A" + atHostId);
    assertEquals(results.size(), 2);
    assertTrue(results.containsKey("B" + atHostId));
    assertTrue(results.containsKey("C" + atHostId));
    assertTrue(results.get("B" + atHostId) == 1);
    assertTrue(results.get("C" + atHostId) == 1);
  }

  private final void generateAdvertisementPacket(FinalExpressionsDetector finalExpressionsDetector, String name, String... subsNames) {
    final AdvertisementPacket advPkt = generateAdvertisementPacket(name, subsNames);
    finalExpressionsDetector.processAdvertisementPacket(advPkt);
  }

  private final AdvertisementPacket generateAdvertisementPacket(String name, String... subsNames) {
    final Advertisement adv = new Advertisement(hostId, name);
    final Set<Subscription> subscriptions = new HashSet<Subscription>();
    for (final String subName : subsNames) {
      final Subscription sub = new Subscription(hostId, subName);
      subscriptions.add(sub);
    }
    if (subscriptions.isEmpty()) {
      return new AdvertisementPacket(adv, AdvType.ADV, true);
    } else {
      return new AdvertisementPacket(adv, AdvType.ADV, subscriptions, true);
    }
  }
}
