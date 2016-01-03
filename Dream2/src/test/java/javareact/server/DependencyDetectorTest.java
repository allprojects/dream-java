package javareact.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import javareact.common.packets.AdvertisementPacket;
import javareact.common.packets.content.AdvType;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Event;
import javareact.common.packets.content.Subscription;

public class DependencyDetectorTest {

  @Test
  public void noDependencyTest() {
    // B = f(A)
    // D = f(B, C)
    final DependencyDetector depDetector = new DependencyDetector();

    final Subscription subA = new Subscription("Host", "A", UUID.randomUUID());
    final Subscription subB = new Subscription("Host", "B", UUID.randomUUID());
    final Subscription subC = new Subscription("Host", "C", UUID.randomUUID());

    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advD = new Advertisement("Host", "D");

    // Subscription to A (A generates B)
    final Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    final AdvertisementPacket advPktA = new AdvertisementPacket(advB, AdvType.ADV, subsB, true);
    depDetector.processAdvertisementPacket(advPktA);

    // Subscription to B and C (B, C generate D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB);
    subsD.add(subC);
    final AdvertisementPacket advPktD = new AdvertisementPacket(advD, AdvType.ADV, subsD, true);
    depDetector.processAdvertisementPacket(advPktD);

    // Consolidate
    depDetector.consolidate();

    // Event A
    final Set<String> computedFromA = new HashSet<String>();
    final Event evA = new Event("Host", "A");
    assertEquals(depDetector.getWaitRecommendations(evA, computedFromA).size(), 0);

    // Event B
    final Set<String> computedFromB = new HashSet<String>();
    final Event evB = new Event("Host", "B");
    assertEquals(depDetector.getWaitRecommendations(evB, computedFromB).size(), 0);

    // Event C
    final Set<String> computedFromC = new HashSet<String>();
    final Event evC = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC, computedFromC).size(), 0);

    // Event D
    final Set<String> computedFromD = new HashSet<String>();
    final Event evD = new Event("Host", "D");
    assertEquals(depDetector.getWaitRecommendations(evD, computedFromD).size(), 0);
  }

  @Test
  public void basicTriangularCycleTest() {
    // B = f(A)
    // C = f(A, B)
    final DependencyDetector depDetector = new DependencyDetector();

    final Subscription subA = new Subscription("Host", "A", UUID.randomUUID());
    final Subscription subB = new Subscription("Host", "B", UUID.randomUUID());

    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advC = new Advertisement("Host", "C");

    // Subscription to A (A generates B)
    final Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    final AdvertisementPacket advPktA = new AdvertisementPacket(advB, AdvType.ADV, subsB, true);
    depDetector.processAdvertisementPacket(advPktA);

    // Subscription to A, B (A, B generates C)
    final Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA);
    subsC.add(subB);
    final AdvertisementPacket advPktC = new AdvertisementPacket(advC, AdvType.ADV, subsC, true);
    depDetector.processAdvertisementPacket(advPktC);

    // Consolidate
    depDetector.consolidate();

    // Event A
    final Set<String> computedFromA = new HashSet<String>();
    final Event evA = new Event("Host", "A");
    assertEquals(depDetector.getWaitRecommendations(evA, computedFromA).size(), 0);
    computedFromA.add("A@Host");
    final Event evA2 = new Event("Host", "A");
    assertEquals(depDetector.getWaitRecommendations(evA2, computedFromA).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evA2, computedFromA)) {
      assertTrue(wr.getExpression().equals("C@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("B@Host"));
    }

    // Event B
    final Set<String> computedFromB = new HashSet<String>();
    final Event evB1 = new Event("Host", "B");
    assertEquals(depDetector.getWaitRecommendations(evB1, computedFromB).size(), 0);
    computedFromB.add("A@Host");
    final Event evB2 = new Event("Host", "B");
    assertEquals(depDetector.getWaitRecommendations(evB2, computedFromB).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, computedFromB)) {
      assertTrue(wr.getExpression().equals("C@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("A@Host"));
    }

    // Event C
    final Set<String> computedFromC = new HashSet<String>();
    final Event evC = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC, computedFromC).size(), 0);
  }

  @Test
  public void basicDualCycleTest() {
    // B = f(A)
    // C = f(A)
    // D = f(B, C)
    final DependencyDetector depDetector = new DependencyDetector();

    final Subscription subA = new Subscription("Host", "A", UUID.randomUUID());
    final Subscription subB = new Subscription("Host", "B", UUID.randomUUID());
    final Subscription subC = new Subscription("Host", "C", UUID.randomUUID());

    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advC = new Advertisement("Host", "C");
    final Advertisement advD = new Advertisement("Host", "D");

    // Subscription to A (A generates B)
    final Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    final AdvertisementPacket advPktA = new AdvertisementPacket(advB, AdvType.ADV, subsB, true);
    depDetector.processAdvertisementPacket(advPktA);

    // Subscription to A (A generates C)
    final Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA);
    final AdvertisementPacket advPktC = new AdvertisementPacket(advC, AdvType.ADV, subsC, true);
    depDetector.processAdvertisementPacket(advPktC);

    // Subscription to B, C (B, C generate D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB);
    subsD.add(subC);
    final AdvertisementPacket advPktD = new AdvertisementPacket(advD, AdvType.ADV, subsD, true);
    depDetector.processAdvertisementPacket(advPktD);

    // Consolidate
    depDetector.consolidate();

    // Event A
    final Set<String> computedFromA = new HashSet<String>();
    final Event evA = new Event("Host", "A");
    assertEquals(depDetector.getWaitRecommendations(evA, computedFromA).size(), 0);

    // Event B
    final Set<String> computedFromB = new HashSet<String>();
    final Event evB1 = new Event("Host", "B");
    assertEquals(depDetector.getWaitRecommendations(evB1, computedFromB).size(), 0);
    computedFromB.add("A@Host");
    final Event evB2 = new Event("Host", "B");
    assertEquals(depDetector.getWaitRecommendations(evB2, computedFromB).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, computedFromB)) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("C@Host"));
    }

    // Event C
    final Set<String> computedFromC = new HashSet<String>();
    final Event evC1 = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC1, computedFromC).size(), 0);
    computedFromC.add("A@Host");
    final Event evC2 = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC2, computedFromC).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evC2, computedFromC)) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("B@Host"));
    }

    // Event D
    final Set<String> computedFromD = new HashSet<String>();
    final Event evD = new Event("Host", "D");
    assertEquals(depDetector.getWaitRecommendations(evD, computedFromD).size(), 0);
  }

  @Test
  public void basicDualCycleTest2() {
    // B = f(A)
    // C = f(A)
    // D = f(C)
    // E = f(B, D)
    final DependencyDetector depDetector = new DependencyDetector();

    final Subscription subA = new Subscription("Host", "A", UUID.randomUUID());
    final Subscription subB = new Subscription("Host", "B", UUID.randomUUID());
    final Subscription subC = new Subscription("Host", "C", UUID.randomUUID());
    final Subscription subD = new Subscription("Host", "D", UUID.randomUUID());

    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advC = new Advertisement("Host", "C");
    final Advertisement advD = new Advertisement("Host", "D");
    final Advertisement advE = new Advertisement("Host", "E");

    // Subscription to A (A generates B)
    final Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    final AdvertisementPacket advPktA = new AdvertisementPacket(advB, AdvType.ADV, subsB, true);
    depDetector.processAdvertisementPacket(advPktA);

    // Subscription to A (A generates C)
    final Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA);
    final AdvertisementPacket advPktC = new AdvertisementPacket(advC, AdvType.ADV, subsC, true);
    depDetector.processAdvertisementPacket(advPktC);

    // Subscription to C (C generate D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subC);
    final AdvertisementPacket advPktD = new AdvertisementPacket(advD, AdvType.ADV, subsD, true);
    depDetector.processAdvertisementPacket(advPktD);

    // Subscription to B, D (B, D generate E)
    final Set<Subscription> subsE = new HashSet<Subscription>();
    subsE.add(subB);
    subsE.add(subD);
    final AdvertisementPacket advPktE = new AdvertisementPacket(advE, AdvType.ADV, subsE, true);
    depDetector.processAdvertisementPacket(advPktE);

    // Consolidate
    depDetector.consolidate();

    // Event A
    final Set<String> computedFromA = new HashSet<String>();
    final Event evA = new Event("Host", "A");
    assertEquals(depDetector.getWaitRecommendations(evA, computedFromA).size(), 0);

    // Event B
    final Set<String> computedFromB = new HashSet<String>();
    final Event evB1 = new Event("Host", "B");
    assertEquals(depDetector.getWaitRecommendations(evB1, computedFromB).size(), 0);
    computedFromB.add("A@Host");
    final Event evB2 = new Event("Host", "B");
    assertEquals(depDetector.getWaitRecommendations(evB2, computedFromB).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, computedFromB)) {
      assertTrue(wr.getExpression().equals("E@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("D@Host"));
    }

    // Event C
    final Set<String> computedFromC = new HashSet<String>();
    final Event evC1 = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC1, computedFromC).size(), 0);
    computedFromC.add("A@Host");
    final Event evC2 = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC2, computedFromC).size(), 0);

    // Event D
    final Set<String> computedFromD = new HashSet<String>();
    final Event evD1 = new Event("Host", "D");
    assertEquals(depDetector.getWaitRecommendations(evD1, computedFromD).size(), 0);
    computedFromD.add("A@Host");
    final Event evD2 = new Event("Host", "D");
    assertEquals(depDetector.getWaitRecommendations(evD2, computedFromC).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evD2, computedFromC)) {
      assertTrue(wr.getExpression().equals("E@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("B@Host"));
    }

    // Event E
    final Set<String> computedFromE = new HashSet<String>();
    final Event evE = new Event("E", "Host");
    assertEquals(depDetector.getWaitRecommendations(evE, computedFromE).size(), 0);
  }

  @Test
  public void basicDualTriangle() {
    // B = f(A)
    // C = f(B)
    // D = f(B, C)
    final DependencyDetector depDetector = new DependencyDetector();

    final Subscription subA = new Subscription("Host", "A", UUID.randomUUID());
    final Subscription subB = new Subscription("Host", "B", UUID.randomUUID());
    final Subscription subC = new Subscription("Host", "C", UUID.randomUUID());

    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advC = new Advertisement("Host", "C");
    final Advertisement advD = new Advertisement("Host", "D");

    // Subscription to A (A generates B)
    final Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    final AdvertisementPacket advPktA = new AdvertisementPacket(advB, AdvType.ADV, subsB, true);
    depDetector.processAdvertisementPacket(advPktA);

    // Subscription to B (B generates C)
    final Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subB);
    final AdvertisementPacket advPktC = new AdvertisementPacket(advC, AdvType.ADV, subsC, true);
    depDetector.processAdvertisementPacket(advPktC);

    // Subscription to B, C (B, C generate D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB);
    subsD.add(subC);
    final AdvertisementPacket advPktD = new AdvertisementPacket(advD, AdvType.ADV, subsD, true);
    depDetector.processAdvertisementPacket(advPktD);

    // Consolidate
    depDetector.consolidate();

    // Event A
    final Set<String> computedFromA = new HashSet<String>();
    final Event evA = new Event("Host", "A");
    assertEquals(depDetector.getWaitRecommendations(evA, computedFromA).size(), 0);

    // Event B
    final Set<String> computedFromB = new HashSet<String>();
    final Event evB1 = new Event("Host", "B");
    assertEquals(depDetector.getWaitRecommendations(evB1, computedFromB).size(), 0);
    computedFromB.add("B@Host");
    final Event evB2 = new Event("Host", "B");
    assertEquals(depDetector.getWaitRecommendations(evB2, computedFromB).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, computedFromB)) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("C@Host"));
    }

    // Event C
    final Set<String> computedFromC = new HashSet<String>();
    final Event evC1 = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC1, computedFromC).size(), 0);
    computedFromC.add("B@Host");
    final Event evC2 = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC2, computedFromC).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evC2, computedFromB)) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("B@Host"));
    }

    // Event D
    final Set<String> computedFromD = new HashSet<String>();
    final Event evD1 = new Event("Host", "D");
    assertEquals(depDetector.getWaitRecommendations(evD1, computedFromD).size(), 0);
    computedFromD.add("A@Host");
    final Event evD2 = new Event("Host", "D");
    assertEquals(depDetector.getWaitRecommendations(evD2, computedFromC).size(), 0);
  }

  @Test
  public void basicTripleCycleTest() {
    // B = f(A)
    // C = f(A)
    // D = f(A)
    // E = f(B, C, D)
    final DependencyDetector depDetector = new DependencyDetector();

    final Subscription subA = new Subscription("Host", "A", UUID.randomUUID());
    final Subscription subB = new Subscription("Host", "B", UUID.randomUUID());
    final Subscription subC = new Subscription("Host", "C", UUID.randomUUID());
    final Subscription subD = new Subscription("Host", "D", UUID.randomUUID());

    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advC = new Advertisement("Host", "C");
    final Advertisement advD = new Advertisement("Host", "D");
    final Advertisement advE = new Advertisement("Host", "E");

    // Subscription to A (A generates B)
    final Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    final AdvertisementPacket advPktA = new AdvertisementPacket(advB, AdvType.ADV, subsB, true);
    depDetector.processAdvertisementPacket(advPktA);

    // Subscription to A (A generates C)
    final Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA);
    final AdvertisementPacket advPktC = new AdvertisementPacket(advC, AdvType.ADV, subsC, true);
    depDetector.processAdvertisementPacket(advPktC);

    // Subscription to A (A generates D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subA);
    final AdvertisementPacket advPktD = new AdvertisementPacket(advD, AdvType.ADV, subsD, true);
    depDetector.processAdvertisementPacket(advPktD);

    // Subscription to B, C, D (B, C, D generate E)
    final Set<Subscription> subsE = new HashSet<Subscription>();
    subsE.add(subB);
    subsE.add(subC);
    subsE.add(subD);
    final AdvertisementPacket advPktE = new AdvertisementPacket(advE, AdvType.ADV, subsE, true);
    depDetector.processAdvertisementPacket(advPktE);

    // Consolidate
    depDetector.consolidate();

    // Event A
    final Set<String> computedFromA = new HashSet<String>();
    final Event evA = new Event("Host", "A");
    assertEquals(depDetector.getWaitRecommendations(evA, computedFromA).size(), 0);

    // Event B
    final Set<String> computedFromB = new HashSet<String>();
    final Event evB1 = new Event("Host", "B");
    assertEquals(depDetector.getWaitRecommendations(evB1, computedFromB).size(), 0);
    computedFromB.add("A@Host");
    final Event evB2 = new Event("Host", "B");
    assertEquals(depDetector.getWaitRecommendations(evB2, computedFromB).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, computedFromB)) {
      assertTrue(wr.getExpression().equals("E@Host"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("C@Host"));
      assertTrue(wr.getRecommendations().contains("D@Host"));
    }

    // Event C
    final Set<String> computedFromC = new HashSet<String>();
    final Event evC1 = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC1, computedFromC).size(), 0);
    computedFromC.add("A@Host");
    final Event evC2 = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC2, computedFromC).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evC2, computedFromC)) {
      assertTrue(wr.getExpression().equals("E@Host"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("B@Host"));
      assertTrue(wr.getRecommendations().contains("D@Host"));
    }

    // Event D
    final Set<String> computedFromD = new HashSet<String>();
    final Event evD1 = new Event("Host", "D");
    assertEquals(depDetector.getWaitRecommendations(evD1, computedFromD).size(), 0);
    computedFromD.add("A@Host");
    final Event evD2 = new Event("Host", "D");
    assertEquals(depDetector.getWaitRecommendations(evD2, computedFromD).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evD2, computedFromD)) {
      assertTrue(wr.getExpression().equals("E@Host"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("B@Host"));
      assertTrue(wr.getRecommendations().contains("C@Host"));
    }

    // Event E
    final Set<String> computedFromE = new HashSet<String>();
    final Event evE = new Event("Host", "E");
    assertEquals(depDetector.getWaitRecommendations(evE, computedFromE).size(), 0);
  }

  @Test
  public void dualCyclesTest() {
    // B1 = f(A1)
    // C1 = f(A1)
    // B2 = f(A2)
    // C2 = f(A2)
    // D = f(B1, C1, B2, C2)
    final DependencyDetector depDetector = new DependencyDetector();

    final Subscription subA1 = new Subscription("Host", "A1", UUID.randomUUID());
    final Subscription subA2 = new Subscription("Host", "A2", UUID.randomUUID());
    final Subscription subB1 = new Subscription("Host", "B1", UUID.randomUUID());
    final Subscription subB2 = new Subscription("Host", "B2", UUID.randomUUID());
    final Subscription subC1 = new Subscription("Host", "C1", UUID.randomUUID());
    final Subscription subC2 = new Subscription("Host", "C2", UUID.randomUUID());

    final Advertisement advB1 = new Advertisement("Host", "B1");
    final Advertisement advB2 = new Advertisement("Host", "B2");
    final Advertisement advC1 = new Advertisement("Host", "C1");
    final Advertisement advC2 = new Advertisement("Host", "C2");
    final Advertisement advD = new Advertisement("Host", "D");

    // Subscription to A1 (A1 generates B2)
    final Set<Subscription> subsB1 = new HashSet<Subscription>();
    subsB1.add(subA1);
    final AdvertisementPacket advPktA1 = new AdvertisementPacket(advB1, AdvType.ADV, subsB1, true);
    depDetector.processAdvertisementPacket(advPktA1);

    // Subscription to A2 (A2 generates B2)
    final Set<Subscription> subsB2 = new HashSet<Subscription>();
    subsB2.add(subA2);
    final AdvertisementPacket advPktA2 = new AdvertisementPacket(advB2, AdvType.ADV, subsB2, true);
    depDetector.processAdvertisementPacket(advPktA2);

    // Subscription to A1 (A1 generates C1)
    final Set<Subscription> subsC1 = new HashSet<Subscription>();
    subsC1.add(subA1);
    final AdvertisementPacket advPktC1 = new AdvertisementPacket(advC1, AdvType.ADV, subsC1, true);
    depDetector.processAdvertisementPacket(advPktC1);

    // Subscription to A2 (A2 generates C2)
    final Set<Subscription> subsC2 = new HashSet<Subscription>();
    subsC2.add(subA2);
    final AdvertisementPacket advPktC2 = new AdvertisementPacket(advC2, AdvType.ADV, subsC2, true);
    depDetector.processAdvertisementPacket(advPktC2);

    // Subscription to B1, B2, C1, C2 (B1, B2, C1, C2 generate D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB1);
    subsD.add(subB2);
    subsD.add(subC1);
    subsD.add(subC2);
    final AdvertisementPacket advPktD = new AdvertisementPacket(advD, AdvType.ADV, subsD, true);
    depDetector.processAdvertisementPacket(advPktD);

    // Consolidate
    depDetector.consolidate();

    // Event A1
    final Set<String> computedFromA1 = new HashSet<String>();
    final Event evA1 = new Event("Host", "A1");
    assertEquals(depDetector.getWaitRecommendations(evA1, computedFromA1).size(), 0);

    // Event A2
    final Set<String> computedFromA2 = new HashSet<String>();
    final Event evA2 = new Event("Host", "A2");
    assertEquals(depDetector.getWaitRecommendations(evA2, computedFromA2).size(), 0);

    // Event B1
    final Set<String> computedFromB1 = new HashSet<String>();
    computedFromB1.add("A1@Host");
    final Event evB1 = new Event("Host", "B1");
    assertEquals(depDetector.getWaitRecommendations(evB1, computedFromB1).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB1, computedFromB1)) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("C1@Host"));
    }

    // Event C1
    final Set<String> computedFromC1 = new HashSet<String>();
    computedFromC1.add("A1@Host");
    final Event evC1 = new Event("Host", "C1");
    assertEquals(depDetector.getWaitRecommendations(evC1, computedFromC1).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evC1, computedFromC1)) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("B1@Host"));
    }

    // Event B2
    final Set<String> computedFromB2 = new HashSet<String>();
    computedFromB2.add("A2@Host");
    final Event evB2 = new Event("Host", "B2");
    assertEquals(depDetector.getWaitRecommendations(evB2, computedFromB2).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, computedFromB2)) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("C2@Host"));
    }

    // Event C2
    final Set<String> computedFromC2 = new HashSet<String>();
    computedFromC2.add("A2@Host");
    final Event evC2 = new Event("Host", "C2");
    assertEquals(depDetector.getWaitRecommendations(evC2, computedFromC2).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evC2, computedFromC2)) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("B2@Host"));
    }

    // Event D
    final Set<String> computedFromD = new HashSet<String>();
    final Event evD = new Event("Host", "D");
    assertEquals(depDetector.getWaitRecommendations(evD, computedFromD).size(), 0);
  }

  @Test
  public void dualDependencyTest() {
    // B = f(A1)
    // C = f(A1, A2)
    // D = f(B, C)
    final DependencyDetector depDetector = new DependencyDetector();

    final Subscription subA1 = new Subscription("Host", "A1", UUID.randomUUID());
    final Subscription subA2 = new Subscription("Host", "A2", UUID.randomUUID());
    final Subscription subB = new Subscription("Host", "B", UUID.randomUUID());
    final Subscription subC = new Subscription("Host", "C", UUID.randomUUID());

    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advC = new Advertisement("Host", "C");
    final Advertisement advD = new Advertisement("Host", "D");

    // Subscription to A1 (A1 generates B)
    final Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA1);
    final AdvertisementPacket advPktA = new AdvertisementPacket(advB, AdvType.ADV, subsB, true);
    depDetector.processAdvertisementPacket(advPktA);

    // Subscription to A1, A2 (A1, A2 generate C)
    final Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA1);
    subsC.add(subA2);
    final AdvertisementPacket advPktC = new AdvertisementPacket(advC, AdvType.ADV, subsC, true);
    depDetector.processAdvertisementPacket(advPktC);

    // Subscription to D (B, C generate D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB);
    subsD.add(subC);
    final AdvertisementPacket advPktD = new AdvertisementPacket(advD, AdvType.ADV, subsD, true);
    depDetector.processAdvertisementPacket(advPktD);

    // Consolidate
    depDetector.consolidate();

    // Event A1
    final Set<String> computedFromA1 = new HashSet<String>();
    final Event evA1 = new Event("Host", "A1");
    assertEquals(depDetector.getWaitRecommendations(evA1, computedFromA1).size(), 0);

    // Event A2
    final Set<String> computedFromA2 = new HashSet<String>();
    final Event evA2 = new Event("Host", "A2");
    assertEquals(depDetector.getWaitRecommendations(evA2, computedFromA2).size(), 0);

    // Event B from A1
    final Set<String> computedFromB = new HashSet<String>();
    final Event evB1 = new Event("Host", "B");
    assertEquals(depDetector.getWaitRecommendations(evB1, computedFromB).size(), 0);
    computedFromB.add("A1@Host");
    final Event evB2 = new Event("Host", "B");
    assertEquals(depDetector.getWaitRecommendations(evB2, computedFromB).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, computedFromB)) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("C@Host"));
    }

    // Event C from A1
    final Set<String> computedFromC1 = new HashSet<String>();
    final Event evC1_1 = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC1_1, computedFromC1).size(), 0);
    computedFromC1.add("A1@Host");
    final Event evC1_2 = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC1_2, computedFromC1).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evC1_2, computedFromC1)) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("B@Host"));
    }

    // Event C from A2
    final Set<String> computedFromC2 = new HashSet<String>();
    final Event evC2_1 = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC2_1, computedFromC2).size(), 0);
    computedFromC1.add("A2@Host");
    final Event evC2_2 = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC2_2, computedFromC2).size(), 0);

    // Event D
    final Set<String> computedFromD = new HashSet<String>();
    final Event evD = new Event("Host", "D");
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
    final DependencyDetector depDetector = new DependencyDetector();

    final Subscription subA = new Subscription("Host", "A", UUID.randomUUID());
    final Subscription subB = new Subscription("Host", "B", UUID.randomUUID());
    final Subscription subC = new Subscription("Host", "C", UUID.randomUUID());
    final Subscription subE = new Subscription("Host", "E", UUID.randomUUID());
    final Subscription subF = new Subscription("Host", "F", UUID.randomUUID());
    final Subscription subG = new Subscription("Host", "G", UUID.randomUUID());
    final Subscription subH = new Subscription("Host", "H", UUID.randomUUID());

    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advC = new Advertisement("Host", "C");
    final Advertisement advD = new Advertisement("Host", "D");
    final Advertisement advE = new Advertisement("Host", "E");
    final Advertisement advF = new Advertisement("Host", "F");
    final Advertisement advG = new Advertisement("Host", "G");
    final Advertisement advH = new Advertisement("Host", "H");

    // Subscription to A (A generates B)
    final Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    final AdvertisementPacket advPktA = new AdvertisementPacket(advB, AdvType.ADV, subsB, true);
    depDetector.processAdvertisementPacket(advPktA);

    // Subscription to A (A generates C)
    final Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA);
    final AdvertisementPacket advPktC = new AdvertisementPacket(advC, AdvType.ADV, subsC, true);
    depDetector.processAdvertisementPacket(advPktC);

    // Subscription to A (A generates E)
    final Set<Subscription> subsE = new HashSet<Subscription>();
    subsE.add(subA);
    final AdvertisementPacket advPktE = new AdvertisementPacket(advE, AdvType.ADV, subsE, true);
    depDetector.processAdvertisementPacket(advPktE);

    // Subscription to E (E generates G)
    final Set<Subscription> subsG = new HashSet<Subscription>();
    subsG.add(subE);
    final AdvertisementPacket advPktG = new AdvertisementPacket(advG, AdvType.ADV, subsG, true);
    depDetector.processAdvertisementPacket(advPktG);

    // Subscription to E (E generates F)
    final Set<Subscription> subsF = new HashSet<Subscription>();
    subsF.add(subE);
    final AdvertisementPacket advPktF = new AdvertisementPacket(advF, AdvType.ADV, subsF, true);
    depDetector.processAdvertisementPacket(advPktF);

    // Subscription to F, G (F, G generate H)
    final Set<Subscription> subsH = new HashSet<Subscription>();
    subsH.add(subF);
    subsH.add(subG);
    final AdvertisementPacket advPktH = new AdvertisementPacket(advH, AdvType.ADV, subsH, true);
    depDetector.processAdvertisementPacket(advPktH);

    // Subscription to B, C, H (B, C, H generate D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB);
    subsD.add(subC);
    subsD.add(subH);
    final AdvertisementPacket subPktD = new AdvertisementPacket(advD, AdvType.ADV, subsD, true);
    depDetector.processAdvertisementPacket(subPktD);

    // Consolidate
    depDetector.consolidate();

    // Event A
    final Set<String> computedFromA = new HashSet<String>();
    final Event evA = new Event("Host", "A");
    assertEquals(depDetector.getWaitRecommendations(evA, computedFromA).size(), 0);

    // Event B
    final Set<String> computedFromB = new HashSet<String>();
    final Event evB1 = new Event("Host", "B");
    assertEquals(depDetector.getWaitRecommendations(evB1, computedFromB).size(), 0);
    computedFromB.add("A@Host");
    final Event evB2 = new Event("Host", "B");
    assertEquals(depDetector.getWaitRecommendations(evB2, computedFromB).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, computedFromB)) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("C@Host"));
      assertTrue(wr.getRecommendations().contains("H@Host"));
    }

    // Event C
    final Set<String> computedFromC = new HashSet<String>();
    final Event evC1 = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC1, computedFromC).size(), 0);
    computedFromC.add("A@Host");
    final Event evC2 = new Event("Host", "C");
    assertEquals(depDetector.getWaitRecommendations(evC2, computedFromC).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evC2, computedFromC)) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("B@Host"));
      assertTrue(wr.getRecommendations().contains("H@Host"));
    }

    // Event E
    final Set<String> computedFromE = new HashSet<String>();
    final Event evE = new Event("Host", "E");
    assertEquals(depDetector.getWaitRecommendations(evE, computedFromE).size(), 0);

    // Event F
    final Set<String> computedFromF = new HashSet<String>();
    final Event evF1 = new Event("Host", "F");
    assertEquals(depDetector.getWaitRecommendations(evF1, computedFromF).size(), 0);
    computedFromF.add("E@Host");
    final Event evF2 = new Event("Host", "F");
    assertEquals(depDetector.getWaitRecommendations(evF2, computedFromF).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evF2, computedFromF)) {
      assertTrue(wr.getExpression().equals("H@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("G@Host"));
    }

    // Event G
    final Set<String> computedFromG = new HashSet<String>();
    final Event evG1 = new Event("Host", "G");
    assertEquals(depDetector.getWaitRecommendations(evG1, computedFromG).size(), 0);
    computedFromG.add("E@Host");
    final Event evG2 = new Event("Host", "G");
    assertEquals(depDetector.getWaitRecommendations(evG2, computedFromG).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evG2, computedFromG)) {
      assertTrue(wr.getExpression().equals("H@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("F@Host"));
    }

    // Event H
    final Set<String> computedFromH = new HashSet<String>();
    final Event evH1 = new Event("Host", "H");
    assertEquals(depDetector.getWaitRecommendations(evH1, computedFromH).size(), 0);
    computedFromH.add("A@Host");
    final Event evH2 = new Event("Host", "H");
    assertEquals(depDetector.getWaitRecommendations(evH2, computedFromH).size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evH2, computedFromH)) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("B@Host"));
      assertTrue(wr.getRecommendations().contains("C@Host"));
    }

    // Event D
    final Set<String> computedFromD = new HashSet<String>();
    final Event evD = new Event("Host", "D");
    assertEquals(depDetector.getWaitRecommendations(evD, computedFromD).size(), 0);
  }
}