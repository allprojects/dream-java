package javareact.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import javareact.common.packets.AdvertisementPacket;
import javareact.common.packets.content.AdvType;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.Subscription;

import org.junit.Test;

public class DependencyDetectorTest {

  @Test
  public void noDependencyTest() {
    // B = f(A)
    // D = f(B, C)
    DependencyDetector depDetector = new DependencyDetector();

    Subscription subA = new Subscription("A", "Host");
    Subscription subB = new Subscription("B", "Host");
    Subscription subC = new Subscription("C", "Host");

    Advertisement advB = new Advertisement("B", "Host");
    Advertisement advD = new Advertisement("D", "Host");

    // Subscription to A (A generates B)
    Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    AdvertisementPacket advPktA = new AdvertisementPacket(advB, AdvType.ADV, subsB, true);
    depDetector.processAdvertisementPacket(advPktA);

    // Subscription to B and C (B, C generate D)
    Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB);
    subsD.add(subC);
    AdvertisementPacket advPktD = new AdvertisementPacket(advD, AdvType.ADV, subsD, true);
    depDetector.processAdvertisementPacket(advPktD);

    // Consolidate
    depDetector.consolidate();

    // Event A
    Set<String> computedFromA = new HashSet<String>();
    Event evA = new Event("A", "Host");
    assertEquals(depDetector.getWaitRecommendations(evA, computedFromA).size(), 0);

    // Event B
    Set<String> computedFromB = new HashSet<String>();
    Event evB = new Event("B", "Host");
    assertEquals(depDetector.getWaitRecommendations(evB, computedFromB).size(), 0);

    // Event C
    Set<String> computedFromC = new HashSet<String>();
    Event evC = new Event("C", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC, computedFromC).size(), 0);

    // Event D
    Set<String> computedFromD = new HashSet<String>();
    Event evD = new Event("D", "Host");
    assertEquals(depDetector.getWaitRecommendations(evD, computedFromD).size(), 0);
  }

  @Test
  public void basicTriangularCycleTest() {
    // B = f(A)
    // C = f(A, B)
    DependencyDetector depDetector = new DependencyDetector();

    Subscription subA = new Subscription("A", "Host");
    Subscription subB = new Subscription("B", "Host");

    Advertisement advB = new Advertisement("B", "Host");
    Advertisement advC = new Advertisement("C", "Host");

    // Subscription to A (A generates B)
    Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    AdvertisementPacket advPktA = new AdvertisementPacket(advB, AdvType.ADV, subsB, true);
    depDetector.processAdvertisementPacket(advPktA);

    // Subscription to A, B (A, B generates C)
    Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA);
    subsC.add(subB);
    AdvertisementPacket advPktC = new AdvertisementPacket(advC, AdvType.ADV, subsC, true);
    depDetector.processAdvertisementPacket(advPktC);

    // Consolidate
    depDetector.consolidate();

    // Event A
    Set<String> computedFromA = new HashSet<String>();
    Event evA = new Event("A", "Host");
    assertEquals(depDetector.getWaitRecommendations(evA, computedFromA).size(), 0);
    computedFromA.add("Host.A");
    Event evA2 = new Event("A", "Host");
    assertEquals(depDetector.getWaitRecommendations(evA2, computedFromA).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evA2, computedFromA)) {
      assertTrue(wr.getExpression().equals("Host.C"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("Host.B"));
    }

    // Event B
    Set<String> computedFromB = new HashSet<String>();
    Event evB1 = new Event("B", "Host");
    assertEquals(depDetector.getWaitRecommendations(evB1, computedFromB).size(), 0);
    computedFromB.add("Host.A");
    Event evB2 = new Event("B", "Host");
    assertEquals(depDetector.getWaitRecommendations(evB2, computedFromB).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, computedFromB)) {
      assertTrue(wr.getExpression().equals("Host.C"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("Host.A"));
    }

    // Event C
    Set<String> computedFromC = new HashSet<String>();
    Event evC = new Event("C", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC, computedFromC).size(), 0);
  }

  @Test
  public void basicDualCycleTest() {
    // B = f(A)
    // C = f(A)
    // D = f(B, C)
    DependencyDetector depDetector = new DependencyDetector();

    Subscription subA = new Subscription("A", "Host");
    Subscription subB = new Subscription("B", "Host");
    Subscription subC = new Subscription("C", "Host");

    Advertisement advB = new Advertisement("B", "Host");
    Advertisement advC = new Advertisement("C", "Host");
    Advertisement advD = new Advertisement("D", "Host");

    // Subscription to A (A generates B)
    Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    AdvertisementPacket advPktA = new AdvertisementPacket(advB, AdvType.ADV, subsB, true);
    depDetector.processAdvertisementPacket(advPktA);

    // Subscription to A (A generates C)
    Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA);
    AdvertisementPacket advPktC = new AdvertisementPacket(advC, AdvType.ADV, subsC, true);
    depDetector.processAdvertisementPacket(advPktC);

    // Subscription to B, C (B, C generate D)
    Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB);
    subsD.add(subC);
    AdvertisementPacket advPktD = new AdvertisementPacket(advD, AdvType.ADV, subsD, true);
    depDetector.processAdvertisementPacket(advPktD);

    // Consolidate
    depDetector.consolidate();

    // Event A
    Set<String> computedFromA = new HashSet<String>();
    Event evA = new Event("A", "Host");
    assertEquals(depDetector.getWaitRecommendations(evA, computedFromA).size(), 0);

    // Event B
    Set<String> computedFromB = new HashSet<String>();
    Event evB1 = new Event("B", "Host");
    assertEquals(depDetector.getWaitRecommendations(evB1, computedFromB).size(), 0);
    computedFromB.add("Host.A");
    Event evB2 = new Event("B", "Host");
    assertEquals(depDetector.getWaitRecommendations(evB2, computedFromB).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, computedFromB)) {
      assertTrue(wr.getExpression().equals("Host.D"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("Host.C"));
    }

    // Event C
    Set<String> computedFromC = new HashSet<String>();
    Event evC1 = new Event("C", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC1, computedFromC).size(), 0);
    computedFromC.add("Host.A");
    Event evC2 = new Event("C", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC2, computedFromC).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evC2, computedFromC)) {
      assertTrue(wr.getExpression().equals("Host.D"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("Host.B"));
    }

    // Event D
    Set<String> computedFromD = new HashSet<String>();
    Event evD = new Event("D", "Host");
    assertEquals(depDetector.getWaitRecommendations(evD, computedFromD).size(), 0);
  }

  @Test
  public void basicDualCycleTest2() {
    // B = f(A)
    // C = f(A)
    // D = f(C)
    // E = f(B, D)
    DependencyDetector depDetector = new DependencyDetector();

    Subscription subA = new Subscription("A", "Host");
    Subscription subB = new Subscription("B", "Host");
    Subscription subC = new Subscription("C", "Host");
    Subscription subD = new Subscription("D", "Host");

    Advertisement advB = new Advertisement("B", "Host");
    Advertisement advC = new Advertisement("C", "Host");
    Advertisement advD = new Advertisement("D", "Host");
    Advertisement advE = new Advertisement("E", "Host");

    // Subscription to A (A generates B)
    Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    AdvertisementPacket advPktA = new AdvertisementPacket(advB, AdvType.ADV, subsB, true);
    depDetector.processAdvertisementPacket(advPktA);

    // Subscription to A (A generates C)
    Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA);
    AdvertisementPacket advPktC = new AdvertisementPacket(advC, AdvType.ADV, subsC, true);
    depDetector.processAdvertisementPacket(advPktC);

    // Subscription to C (C generate D)
    Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subC);
    AdvertisementPacket advPktD = new AdvertisementPacket(advD, AdvType.ADV, subsD, true);
    depDetector.processAdvertisementPacket(advPktD);

    // Subscription to B, D (B, D generate E)
    Set<Subscription> subsE = new HashSet<Subscription>();
    subsE.add(subB);
    subsE.add(subD);
    AdvertisementPacket advPktE = new AdvertisementPacket(advE, AdvType.ADV, subsE, true);
    depDetector.processAdvertisementPacket(advPktE);

    // Consolidate
    depDetector.consolidate();

    // Event A
    Set<String> computedFromA = new HashSet<String>();
    Event evA = new Event("A", "Host");
    assertEquals(depDetector.getWaitRecommendations(evA, computedFromA).size(), 0);

    // Event B
    Set<String> computedFromB = new HashSet<String>();
    Event evB1 = new Event("B", "Host");
    assertEquals(depDetector.getWaitRecommendations(evB1, computedFromB).size(), 0);
    computedFromB.add("Host.A");
    Event evB2 = new Event("B", "Host");
    assertEquals(depDetector.getWaitRecommendations(evB2, computedFromB).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, computedFromB)) {
      assertTrue(wr.getExpression().equals("Host.E"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("Host.D"));
    }

    // Event C
    Set<String> computedFromC = new HashSet<String>();
    Event evC1 = new Event("C", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC1, computedFromC).size(), 0);
    computedFromC.add("Host.A");
    Event evC2 = new Event("C", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC2, computedFromC).size(), 0);

    // Event D
    Set<String> computedFromD = new HashSet<String>();
    Event evD1 = new Event("D", "Host");
    assertEquals(depDetector.getWaitRecommendations(evD1, computedFromD).size(), 0);
    computedFromD.add("Host.A");
    Event evD2 = new Event("D", "Host");
    assertEquals(depDetector.getWaitRecommendations(evD2, computedFromC).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evD2, computedFromC)) {
      assertTrue(wr.getExpression().equals("Host.E"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("Host.B"));
    }

    // Event E
    Set<String> computedFromE = new HashSet<String>();
    Event evE = new Event("E", "Host");
    assertEquals(depDetector.getWaitRecommendations(evE, computedFromE).size(), 0);
  }

  @Test
  public void basicTripleCycleTest() {
    // B = f(A)
    // C = f(A)
    // D = f(A)
    // E = f(B, C, D)
    DependencyDetector depDetector = new DependencyDetector();

    Subscription subA = new Subscription("A", "Host");
    Subscription subB = new Subscription("B", "Host");
    Subscription subC = new Subscription("C", "Host");
    Subscription subD = new Subscription("D", "Host");

    Advertisement advB = new Advertisement("B", "Host");
    Advertisement advC = new Advertisement("C", "Host");
    Advertisement advD = new Advertisement("D", "Host");
    Advertisement advE = new Advertisement("E", "Host");

    // Subscription to A (A generates B)
    Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    AdvertisementPacket advPktA = new AdvertisementPacket(advB, AdvType.ADV, subsB, true);
    depDetector.processAdvertisementPacket(advPktA);

    // Subscription to A (A generates C)
    Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA);
    AdvertisementPacket advPktC = new AdvertisementPacket(advC, AdvType.ADV, subsC, true);
    depDetector.processAdvertisementPacket(advPktC);

    // Subscription to A (A generates D)
    Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subA);
    AdvertisementPacket advPktD = new AdvertisementPacket(advD, AdvType.ADV, subsD, true);
    depDetector.processAdvertisementPacket(advPktD);

    // Subscription to B, C, D (B, C, D generate E)
    Set<Subscription> subsE = new HashSet<Subscription>();
    subsE.add(subB);
    subsE.add(subC);
    subsE.add(subD);
    AdvertisementPacket advPktE = new AdvertisementPacket(advE, AdvType.ADV, subsE, true);
    depDetector.processAdvertisementPacket(advPktE);

    // Consolidate
    depDetector.consolidate();

    // Event A
    Set<String> computedFromA = new HashSet<String>();
    Event evA = new Event("A", "Host");
    assertEquals(depDetector.getWaitRecommendations(evA, computedFromA).size(), 0);

    // Event B
    Set<String> computedFromB = new HashSet<String>();
    Event evB1 = new Event("B", "Host");
    assertEquals(depDetector.getWaitRecommendations(evB1, computedFromB).size(), 0);
    computedFromB.add("Host.A");
    Event evB2 = new Event("B", "Host");
    assertEquals(depDetector.getWaitRecommendations(evB2, computedFromB).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, computedFromB)) {
      assertTrue(wr.getExpression().equals("Host.E"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("Host.C"));
      assertTrue(wr.getRecommendations().contains("Host.D"));
    }

    // Event C
    Set<String> computedFromC = new HashSet<String>();
    Event evC1 = new Event("C", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC1, computedFromC).size(), 0);
    computedFromC.add("Host.A");
    Event evC2 = new Event("C", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC2, computedFromC).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evC2, computedFromC)) {
      assertTrue(wr.getExpression().equals("Host.E"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("Host.B"));
      assertTrue(wr.getRecommendations().contains("Host.D"));
    }

    // Event D
    Set<String> computedFromD = new HashSet<String>();
    Event evD1 = new Event("D", "Host");
    assertEquals(depDetector.getWaitRecommendations(evD1, computedFromD).size(), 0);
    computedFromD.add("Host.A");
    Event evD2 = new Event("D", "Host");
    assertEquals(depDetector.getWaitRecommendations(evD2, computedFromD).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evD2, computedFromD)) {
      assertTrue(wr.getExpression().equals("Host.E"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("Host.B"));
      assertTrue(wr.getRecommendations().contains("Host.C"));
    }

    // Event E
    Set<String> computedFromE = new HashSet<String>();
    Event evE = new Event("E", "Host");
    assertEquals(depDetector.getWaitRecommendations(evE, computedFromE).size(), 0);
  }

  @Test
  public void dualCyclesTest() {
    // B1 = f(A1)
    // C1 = f(A1)
    // B2 = f(A2)
    // C2 = f(A2)
    // D = f(B1, C1, B2, C2)
    DependencyDetector depDetector = new DependencyDetector();

    Subscription subA1 = new Subscription("A1", "Host");
    Subscription subA2 = new Subscription("A2", "Host");
    Subscription subB1 = new Subscription("B1", "Host");
    Subscription subB2 = new Subscription("B2", "Host");
    Subscription subC1 = new Subscription("C1", "Host");
    Subscription subC2 = new Subscription("C2", "Host");

    Advertisement advB1 = new Advertisement("B1", "Host");
    Advertisement advB2 = new Advertisement("B2", "Host");
    Advertisement advC1 = new Advertisement("C1", "Host");
    Advertisement advC2 = new Advertisement("C2", "Host");
    Advertisement advD = new Advertisement("D", "Host");

    // Subscription to A1 (A1 generates B2)
    Set<Subscription> subsB1 = new HashSet<Subscription>();
    subsB1.add(subA1);
    AdvertisementPacket advPktA1 = new AdvertisementPacket(advB1, AdvType.ADV, subsB1, true);
    depDetector.processAdvertisementPacket(advPktA1);

    // Subscription to A2 (A2 generates B2)
    Set<Subscription> subsB2 = new HashSet<Subscription>();
    subsB2.add(subA2);
    AdvertisementPacket advPktA2 = new AdvertisementPacket(advB2, AdvType.ADV, subsB2, true);
    depDetector.processAdvertisementPacket(advPktA2);

    // Subscription to A1 (A1 generates C1)
    Set<Subscription> subsC1 = new HashSet<Subscription>();
    subsC1.add(subA1);
    AdvertisementPacket advPktC1 = new AdvertisementPacket(advC1, AdvType.ADV, subsC1, true);
    depDetector.processAdvertisementPacket(advPktC1);

    // Subscription to A2 (A2 generates C2)
    Set<Subscription> subsC2 = new HashSet<Subscription>();
    subsC2.add(subA2);
    AdvertisementPacket advPktC2 = new AdvertisementPacket(advC2, AdvType.ADV, subsC2, true);
    depDetector.processAdvertisementPacket(advPktC2);

    // Subscription to B1, B2, C1, C2 (B1, B2, C1, C2 generate D)
    Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB1);
    subsD.add(subB2);
    subsD.add(subC1);
    subsD.add(subC2);
    AdvertisementPacket advPktD = new AdvertisementPacket(advD, AdvType.ADV, subsD, true);
    depDetector.processAdvertisementPacket(advPktD);

    // Consolidate
    depDetector.consolidate();

    // Event A1
    Set<String> computedFromA1 = new HashSet<String>();
    Event evA1 = new Event("A1", "Host");
    assertEquals(depDetector.getWaitRecommendations(evA1, computedFromA1).size(), 0);

    // Event A2
    Set<String> computedFromA2 = new HashSet<String>();
    Event evA2 = new Event("A2", "Host");
    assertEquals(depDetector.getWaitRecommendations(evA2, computedFromA2).size(), 0);

    // Event B1
    Set<String> computedFromB1 = new HashSet<String>();
    computedFromB1.add("Host.A1");
    Event evB1 = new Event("B1", "Host");
    assertEquals(depDetector.getWaitRecommendations(evB1, computedFromB1).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evB1, computedFromB1)) {
      assertTrue(wr.getExpression().equals("Host.D"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("Host.C1"));
    }

    // Event C1
    Set<String> computedFromC1 = new HashSet<String>();
    computedFromC1.add("Host.A1");
    Event evC1 = new Event("C1", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC1, computedFromC1).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evC1, computedFromC1)) {
      assertTrue(wr.getExpression().equals("Host.D"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("Host.B1"));
    }

    // Event B2
    Set<String> computedFromB2 = new HashSet<String>();
    computedFromB2.add("Host.A2");
    Event evB2 = new Event("B2", "Host");
    assertEquals(depDetector.getWaitRecommendations(evB2, computedFromB2).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, computedFromB2)) {
      assertTrue(wr.getExpression().equals("Host.D"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("Host.C2"));
    }

    // Event C2
    Set<String> computedFromC2 = new HashSet<String>();
    computedFromC2.add("Host.A2");
    Event evC2 = new Event("C2", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC2, computedFromC2).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evC2, computedFromC2)) {
      assertTrue(wr.getExpression().equals("Host.D"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("Host.B2"));
    }

    // Event D
    Set<String> computedFromD = new HashSet<String>();
    Event evD = new Event("D", "Host");
    assertEquals(depDetector.getWaitRecommendations(evD, computedFromD).size(), 0);
  }

  @Test
  public void dualDependencyTest() {
    // B = f(A1)
    // C = f(A1, A2)
    // D = f(B, C)
    DependencyDetector depDetector = new DependencyDetector();

    Subscription subA1 = new Subscription("A1", "Host");
    Subscription subA2 = new Subscription("A2", "Host");
    Subscription subB = new Subscription("B", "Host");
    Subscription subC = new Subscription("C", "Host");

    Advertisement advB = new Advertisement("B", "Host");
    Advertisement advC = new Advertisement("C", "Host");
    Advertisement advD = new Advertisement("D", "Host");

    // Subscription to A1 (A1 generates B)
    Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA1);
    AdvertisementPacket advPktA = new AdvertisementPacket(advB, AdvType.ADV, subsB, true);
    depDetector.processAdvertisementPacket(advPktA);

    // Subscription to A1, A2 (A1, A2 generate C)
    Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA1);
    subsC.add(subA2);
    AdvertisementPacket advPktC = new AdvertisementPacket(advC, AdvType.ADV, subsC, true);
    depDetector.processAdvertisementPacket(advPktC);

    // Subscription to D (B, C generate D)
    Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB);
    subsD.add(subC);
    AdvertisementPacket advPktD = new AdvertisementPacket(advD, AdvType.ADV, subsD, true);
    depDetector.processAdvertisementPacket(advPktD);

    // Consolidate
    depDetector.consolidate();

    // Event A1
    Set<String> computedFromA1 = new HashSet<String>();
    Event evA1 = new Event("A1", "Host");
    assertEquals(depDetector.getWaitRecommendations(evA1, computedFromA1).size(), 0);

    // Event A2
    Set<String> computedFromA2 = new HashSet<String>();
    Event evA2 = new Event("A2", "Host");
    assertEquals(depDetector.getWaitRecommendations(evA2, computedFromA2).size(), 0);

    // Event B from A1
    Set<String> computedFromB = new HashSet<String>();
    Event evB1 = new Event("B", "Host");
    assertEquals(depDetector.getWaitRecommendations(evB1, computedFromB).size(), 0);
    computedFromB.add("Host.A1");
    Event evB2 = new Event("B", "Host");
    assertEquals(depDetector.getWaitRecommendations(evB2, computedFromB).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, computedFromB)) {
      assertTrue(wr.getExpression().equals("Host.D"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("Host.C"));
    }

    // Event C from A1
    Set<String> computedFromC1 = new HashSet<String>();
    Event evC1_1 = new Event("C", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC1_1, computedFromC1).size(), 0);
    computedFromC1.add("Host.A1");
    Event evC1_2 = new Event("C", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC1_2, computedFromC1).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evC1_2, computedFromC1)) {
      assertTrue(wr.getExpression().equals("Host.D"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("Host.B"));
    }

    // Event C from A2
    Set<String> computedFromC2 = new HashSet<String>();
    Event evC2_1 = new Event("C", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC2_1, computedFromC2).size(), 0);
    computedFromC1.add("Host.A2");
    Event evC2_2 = new Event("C", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC2_2, computedFromC2).size(), 0);

    // Event D
    Set<String> computedFromD = new HashSet<String>();
    Event evD = new Event("D", "Host");
    assertEquals(depDetector.getWaitRecommendations(evD, computedFromD).size(), 0);
  }

  @Test
  public void nestedCyclesTest() {
    // B = f(A)
    // C = f(A)
    // E = f(A)
    // G = f(E)
    // F = f(E)
    // H = f(F, G)
    // D = f(B, C, H)
    DependencyDetector depDetector = new DependencyDetector();

    Subscription subA = new Subscription("A", "Host");
    Subscription subB = new Subscription("B", "Host");
    Subscription subC = new Subscription("C", "Host");
    Subscription subE = new Subscription("E", "Host");
    Subscription subF = new Subscription("F", "Host");
    Subscription subG = new Subscription("G", "Host");
    Subscription subH = new Subscription("H", "Host");

    Advertisement advB = new Advertisement("B", "Host");
    Advertisement advC = new Advertisement("C", "Host");
    Advertisement advD = new Advertisement("D", "Host");
    Advertisement advE = new Advertisement("E", "Host");
    Advertisement advF = new Advertisement("F", "Host");
    Advertisement advG = new Advertisement("G", "Host");
    Advertisement advH = new Advertisement("H", "Host");

    // Subscription to A (A generates B)
    Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    AdvertisementPacket advPktA = new AdvertisementPacket(advB, AdvType.ADV, subsB, true);
    depDetector.processAdvertisementPacket(advPktA);

    // Subscription to A (A generates C)
    Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA);
    AdvertisementPacket advPktC = new AdvertisementPacket(advC, AdvType.ADV, subsC, true);
    depDetector.processAdvertisementPacket(advPktC);

    // Subscription to A (A generates E)
    Set<Subscription> subsE = new HashSet<Subscription>();
    subsE.add(subA);
    AdvertisementPacket advPktE = new AdvertisementPacket(advE, AdvType.ADV, subsE, true);
    depDetector.processAdvertisementPacket(advPktE);

    // Subscription to E (E generates G)
    Set<Subscription> subsG = new HashSet<Subscription>();
    subsG.add(subE);
    AdvertisementPacket advPktG = new AdvertisementPacket(advG, AdvType.ADV, subsG, true);
    depDetector.processAdvertisementPacket(advPktG);

    // Subscription to E (E generates F)
    Set<Subscription> subsF = new HashSet<Subscription>();
    subsF.add(subE);
    AdvertisementPacket advPktF = new AdvertisementPacket(advF, AdvType.ADV, subsF, true);
    depDetector.processAdvertisementPacket(advPktF);

    // Subscription to F, G (F, G generate H)
    Set<Subscription> subsH = new HashSet<Subscription>();
    subsH.add(subF);
    subsH.add(subG);
    AdvertisementPacket advPktH = new AdvertisementPacket(advH, AdvType.ADV, subsH, true);
    depDetector.processAdvertisementPacket(advPktH);

    // Subscription to B, C, H (B, C, H generate D)
    Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB);
    subsD.add(subC);
    subsD.add(subH);
    AdvertisementPacket subPktD = new AdvertisementPacket(advD, AdvType.ADV, subsD, true);
    depDetector.processAdvertisementPacket(subPktD);

    // Consolidate
    depDetector.consolidate();

    // Event A
    Set<String> computedFromA = new HashSet<String>();
    Event evA = new Event("A", "Host");
    assertEquals(depDetector.getWaitRecommendations(evA, computedFromA).size(), 0);

    // Event B
    Set<String> computedFromB = new HashSet<String>();
    Event evB1 = new Event("B", "Host");
    assertEquals(depDetector.getWaitRecommendations(evB1, computedFromB).size(), 0);
    computedFromB.add("Host.A");
    Event evB2 = new Event("B", "Host");
    assertEquals(depDetector.getWaitRecommendations(evB2, computedFromB).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, computedFromB)) {
      assertTrue(wr.getExpression().equals("Host.D"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("Host.C"));
      assertTrue(wr.getRecommendations().contains("Host.H"));
    }

    // Event C
    Set<String> computedFromC = new HashSet<String>();
    Event evC1 = new Event("C", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC1, computedFromC).size(), 0);
    computedFromC.add("Host.A");
    Event evC2 = new Event("C", "Host");
    assertEquals(depDetector.getWaitRecommendations(evC2, computedFromC).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evC2, computedFromC)) {
      assertTrue(wr.getExpression().equals("Host.D"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("Host.B"));
      assertTrue(wr.getRecommendations().contains("Host.H"));
    }

    // Event E
    Set<String> computedFromE = new HashSet<String>();
    Event evE = new Event("E", "Host");
    assertEquals(depDetector.getWaitRecommendations(evE, computedFromE).size(), 0);

    // Event F
    Set<String> computedFromF = new HashSet<String>();
    Event evF1 = new Event("F", "Host");
    assertEquals(depDetector.getWaitRecommendations(evF1, computedFromF).size(), 0);
    computedFromF.add("Host.E");
    Event evF2 = new Event("F", "Host");
    assertEquals(depDetector.getWaitRecommendations(evF2, computedFromF).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evF2, computedFromF)) {
      assertTrue(wr.getExpression().equals("Host.H"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("Host.G"));
    }

    // Event G
    Set<String> computedFromG = new HashSet<String>();
    Event evG1 = new Event("G", "Host");
    assertEquals(depDetector.getWaitRecommendations(evG1, computedFromG).size(), 0);
    computedFromG.add("Host.E");
    Event evG2 = new Event("G", "Host");
    assertEquals(depDetector.getWaitRecommendations(evG2, computedFromG).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evG2, computedFromG)) {
      assertTrue(wr.getExpression().equals("Host.H"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("Host.F"));
    }

    // Event H
    Set<String> computedFromH = new HashSet<String>();
    Event evH1 = new Event("H", "Host");
    assertEquals(depDetector.getWaitRecommendations(evH1, computedFromH).size(), 0);
    computedFromH.add("Host.A");
    Event evH2 = new Event("H", "Host");
    assertEquals(depDetector.getWaitRecommendations(evH2, computedFromH).size(), 1);
    for (WaitRecommendations wr : depDetector.getWaitRecommendations(evH2, computedFromH)) {
      assertTrue(wr.getExpression().equals("Host.D"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("Host.B"));
      assertTrue(wr.getRecommendations().contains("Host.C"));
    }

    // Event D
    Set<String> computedFromD = new HashSet<String>();
    Event evD = new Event("D", "Host");
    assertEquals(depDetector.getWaitRecommendations(evD, computedFromD).size(), 0);
  }
}
