package dream.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Event;
import dream.common.packets.content.Subscription;

public class IntraSourceDependencyDetectorTest {

  @Test
  public void noDependencyTest() {
    // B = f(A)
    // D = f(B, C)
    final DependencyGraph graph = DependencyGraph.instance;
    final IntraSourceDependencyDetector depDetector = IntraSourceDependencyDetector.instance;
    graph.clear();

    final Subscription<Integer> subA = new Subscription<Integer>("Host", "A");
    final Subscription<Integer> subB = new Subscription<Integer>("Host", "B");
    final Subscription<Integer> subC = new Subscription<Integer>("Host", "C");

    final Advertisement advA = new Advertisement("Host", "A");
    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advC = new Advertisement("Host", "C");
    final Advertisement advD = new Advertisement("Host", "D");

    // Subscription to A (A generates B)
    final Set<Subscription> subsB = new HashSet<>();
    subsB.add(subA);
    graph.processAdv(advB, subsB);

    // Subscription to B and C (B, C generate D)
    final Set<Subscription> subsD = new HashSet<>();
    subsD.add(subB);
    subsD.add(subC);
    graph.processAdv(advD, subsD);

    graph.processAdv(advA);
    graph.processAdv(advC);

    // Consolidate
    depDetector.consolidate();

    // Event<Integer>A
    final Event<Integer> evA = new Event<>("Host", "A", 1);
    assertEquals(depDetector.getWaitRecommendations(evA, "A@Host").size(), 0);

    // Event<Integer>B
    final Event<Integer> evB = new Event<>("Host", "B", 1);
    assertEquals(depDetector.getWaitRecommendations(evB, "A@Host").size(), 0);

    // Event<Integer>C
    final Event<Integer> evC = new Event<>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC, "A@Host").size(), 0);

    // Event<Integer>D
    final Event<Integer> evD = new Event<>("Host", "D", 1);
    assertEquals(depDetector.getWaitRecommendations(evD, "A@Host").size(), 0);
  }

  @Test
  public void basicTriangularCycleTest() {
    // B = f(A)
    // C = f(A, B)
    final DependencyGraph graph = DependencyGraph.instance;
    final IntraSourceDependencyDetector depDetector = IntraSourceDependencyDetector.instance;
    graph.clear();

    final Subscription<Integer> subA = new Subscription<Integer>("Host", "A");
    final Subscription<Integer> subB = new Subscription<Integer>("Host", "B");

    final Advertisement advA = new Advertisement("Host", "A");
    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advC = new Advertisement("Host", "C");

    // Subscription to A (A generates B)
    final Set<Subscription> subsB = new HashSet<>();
    subsB.add(subA);
    graph.processAdv(advB, subsB);

    // Subscription to A, B (A, B generates C)
    final Set<Subscription> subsC = new HashSet<>();
    subsC.add(subA);
    subsC.add(subB);
    graph.processAdv(advC, subsC);

    graph.processAdv(advA);

    // Consolidate
    depDetector.consolidate();

    // Event<Integer>A
    final Event<Integer> evA = new Event<Integer>("Host", "A", 1);
    assertEquals(depDetector.getWaitRecommendations(evA, "").size(), 0);
    final Event<Integer> evA2 = new Event<Integer>("Host", "A", 1);
    assertEquals(depDetector.getWaitRecommendations(evA2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evA2, "A@Host")) {
      assertTrue(wr.getExpression().equals("C@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("B@Host"));
    }

    // Event<Integer>B
    final Event<Integer> evB1 = new Event<Integer>("Host", "B", 1);
    assertEquals(depDetector.getWaitRecommendations(evB1, "").size(), 0);
    final Event<Integer> evB2 = new Event<Integer>("Host", "B", 1);
    assertEquals(depDetector.getWaitRecommendations(evB2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, "A@Host")) {
      assertTrue(wr.getExpression().equals("C@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("A@Host"));
    }

    // Event<Integer>C
    final Event<Integer> evC = new Event<Integer>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC, "").size(), 0);
  }

  @Test
  public void basicDualCycleTest() {
    // B = f(A)
    // C = f(A)
    // D = f(B, C)
    final DependencyGraph graph = DependencyGraph.instance;
    final IntraSourceDependencyDetector depDetector = IntraSourceDependencyDetector.instance;
    graph.clear();

    final Subscription<Integer> subA = new Subscription<Integer>("Host", "A");
    final Subscription<Integer> subB = new Subscription<Integer>("Host", "B");
    final Subscription<Integer> subC = new Subscription<Integer>("Host", "C");

    final Advertisement advA = new Advertisement("Host", "A");
    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advC = new Advertisement("Host", "C");
    final Advertisement advD = new Advertisement("Host", "D");

    // Subscription to A (A generates B)
    final Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    graph.processAdv(advB, subsB);

    // Subscription to A (A generates C)
    final Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA);
    graph.processAdv(advC, subsC);

    // Subscription to B, C (B, C generate D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB);
    subsD.add(subC);
    graph.processAdv(advD, subsD);

    graph.processAdv(advA);

    // Consolidate
    depDetector.consolidate();

    // Event<Integer>A
    final Event<Integer> evA = new Event<Integer>("Host", "A", 1);
    assertEquals(depDetector.getWaitRecommendations(evA, "").size(), 0);

    // Event<Integer>B
    final Event<Integer> evB1 = new Event<Integer>("Host", "B", 1);
    assertEquals(depDetector.getWaitRecommendations(evB1, "").size(), 0);
    final Event<Integer> evB2 = new Event<Integer>("Host", "B", 1);
    assertEquals(depDetector.getWaitRecommendations(evB2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, "A@Host")) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("C@Host"));
    }

    // Event<Integer>C
    final Event<Integer> evC1 = new Event<Integer>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC1, "").size(), 0);
    final Event<Integer> evC2 = new Event<Integer>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evC2, "A@Host")) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("B@Host"));
    }

    // Event<Integer>D
    final Event<Integer> evD = new Event<Integer>("Host", "D", 1);
    assertEquals(depDetector.getWaitRecommendations(evD, "").size(), 0);
  }

  @Test
  public void basicDualCycleTest2() {
    // B = f(A)
    // C = f(A)
    // D = f(C)
    // E = f(B, D)
    final DependencyGraph graph = DependencyGraph.instance;
    final IntraSourceDependencyDetector depDetector = IntraSourceDependencyDetector.instance;
    graph.clear();

    final Subscription subA = new Subscription("Host", "A");
    final Subscription subB = new Subscription("Host", "B");
    final Subscription subC = new Subscription("Host", "C");
    final Subscription subD = new Subscription("Host", "D");

    final Advertisement advA = new Advertisement("Host", "A");
    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advC = new Advertisement("Host", "C");
    final Advertisement advD = new Advertisement("Host", "D");
    final Advertisement advE = new Advertisement("Host", "E");

    graph.processAdv(advA);

    // Subscription to A (A generates B)
    final Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    graph.processAdv(advB, subsB);

    // Subscription to A (A generates C)
    final Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA);
    graph.processAdv(advC, subsC);

    // Subscription to C (C generate D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subC);
    graph.processAdv(advD, subsD);

    // Subscription to B, D (B, D generate E)
    final Set<Subscription> subsE = new HashSet<Subscription>();
    subsE.add(subB);
    subsE.add(subD);
    graph.processAdv(advE, subsE);

    // Consolidate
    depDetector.consolidate();

    // Event<Integer>A
    final Event<Integer> evA = new Event<Integer>("Host", "A", 1);
    assertEquals(depDetector.getWaitRecommendations(evA, "").size(), 0);

    // Event<Integer>B
    final Event<Integer> evB1 = new Event<Integer>("Host", "B", 1);
    assertEquals(depDetector.getWaitRecommendations(evB1, "").size(), 0);
    final Event<Integer> evB2 = new Event<Integer>("Host", "B", 1);
    assertEquals(depDetector.getWaitRecommendations(evB2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, "A@Host")) {
      assertTrue(wr.getExpression().equals("E@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("D@Host"));
    }

    // Event<Integer>C
    final Event<Integer> evC1 = new Event<Integer>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC1, "").size(), 0);
    final Event<Integer> evC2 = new Event<Integer>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC2, "A@Host").size(), 0);

    // Event<Integer>D
    final Event<Integer> evD1 = new Event<Integer>("Host", "D", 1);
    assertEquals(depDetector.getWaitRecommendations(evD1, "").size(), 0);
    final Event<Integer> evD2 = new Event<Integer>("Host", "D", 1);
    assertEquals(depDetector.getWaitRecommendations(evD2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evD2, "A@Host")) {
      assertTrue(wr.getExpression().equals("E@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("B@Host"));
    }

    // Event<Integer>E
    final Event<Integer> evE = new Event<Integer>("E", "Host", 1);
    assertEquals(depDetector.getWaitRecommendations(evE, "").size(), 0);
  }

  @Test
  public void basicDualTriangle() {
    // B = f(A)
    // C = f(B)
    // D = f(B, C)
    final DependencyGraph graph = DependencyGraph.instance;
    final IntraSourceDependencyDetector depDetector = IntraSourceDependencyDetector.instance;
    graph.clear();

    final Subscription subA = new Subscription("Host", "A");
    final Subscription subB = new Subscription("Host", "B");
    final Subscription subC = new Subscription("Host", "C");

    final Advertisement advA = new Advertisement("Host", "A");
    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advC = new Advertisement("Host", "C");
    final Advertisement advD = new Advertisement("Host", "D");

    // Subscription to A (A generates B)
    final Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    graph.processAdv(advB, subsB);

    // Subscription to B (B generates C)
    final Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subB);
    graph.processAdv(advC, subsC);

    // Subscription to B, C (B, C generate D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB);
    subsD.add(subC);
    graph.processAdv(advD, subsD);

    graph.processAdv(advA);

    // Consolidate
    depDetector.consolidate();

    // Event<Integer>A
    final Event<Integer> evA = new Event<Integer>("Host", "A", 1);
    assertEquals(depDetector.getWaitRecommendations(evA, "").size(), 0);

    // Event<Integer>B
    final Event<Integer> evB1 = new Event<Integer>("Host", "B", 1);
    assertEquals(depDetector.getWaitRecommendations(evB1, "").size(), 0);
    final Event<Integer> evB2 = new Event<Integer>("Host", "B", 1);
    assertEquals(depDetector.getWaitRecommendations(evB2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, "A@Host")) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("C@Host"));
    }

    // Event<Integer>C
    final Event<Integer> evC1 = new Event<Integer>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC1, "").size(), 0);
    final Event<Integer> evC2 = new Event<Integer>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evC2, "A@Host")) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("B@Host"));
    }

    // Event<Integer>D
    final Event<Integer> evD1 = new Event<Integer>("Host", "D", 1);
    assertEquals(depDetector.getWaitRecommendations(evD1, "").size(), 0);
    final Event<Integer> evD2 = new Event<Integer>("Host", "D", 1);
    assertEquals(depDetector.getWaitRecommendations(evD2, "A@Host").size(), 0);
  }

  @Test
  public void basicTripleCycleTest() {
    // B = f(A)
    // C = f(A)
    // D = f(A)
    // E = f(B, C, D)
    final DependencyGraph graph = DependencyGraph.instance;
    final IntraSourceDependencyDetector depDetector = IntraSourceDependencyDetector.instance;
    graph.clear();

    final Subscription subA = new Subscription("Host", "A");
    final Subscription subB = new Subscription("Host", "B");
    final Subscription subC = new Subscription("Host", "C");
    final Subscription subD = new Subscription("Host", "D");

    final Advertisement advA = new Advertisement("Host", "A");
    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advC = new Advertisement("Host", "C");
    final Advertisement advD = new Advertisement("Host", "D");
    final Advertisement advE = new Advertisement("Host", "E");

    graph.processAdv(advA);

    // Subscription to A (A generates B)
    final Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    graph.processAdv(advB, subsB);

    // Subscription to A (A generates C)
    final Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA);
    graph.processAdv(advC, subsC);

    // Subscription to A (A generates D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subA);
    graph.processAdv(advD, subsD);

    // Subscription to B, C, D (B, C, D generate E)
    final Set<Subscription> subsE = new HashSet<Subscription>();
    subsE.add(subB);
    subsE.add(subC);
    subsE.add(subD);
    graph.processAdv(advE, subsE);

    // Consolidate
    depDetector.consolidate();

    // Event<Integer>A
    final Event<Integer> evA = new Event<Integer>("Host", "A", 1);
    assertEquals(depDetector.getWaitRecommendations(evA, "").size(), 0);

    // Event<Integer>B
    final Event<Integer> evB1 = new Event<Integer>("Host", "B", 1);
    assertEquals(depDetector.getWaitRecommendations(evB1, "").size(), 0);
    final Event<Integer> evB2 = new Event<Integer>("Host", "B", 1);
    assertEquals(depDetector.getWaitRecommendations(evB2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, "A@Host")) {
      assertTrue(wr.getExpression().equals("E@Host"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("C@Host"));
      assertTrue(wr.getRecommendations().contains("D@Host"));
    }

    // Event<Integer>C
    final Event<Integer> evC1 = new Event<Integer>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC1, "").size(), 0);
    final Event<Integer> evC2 = new Event<Integer>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evC2, "A@Host")) {
      assertTrue(wr.getExpression().equals("E@Host"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("B@Host"));
      assertTrue(wr.getRecommendations().contains("D@Host"));
    }

    // Event<Integer>D
    final Event<Integer> evD1 = new Event<Integer>("Host", "D", 1);
    assertEquals(depDetector.getWaitRecommendations(evD1, "").size(), 0);
    final Event<Integer> evD2 = new Event<Integer>("Host", "D", 1);
    assertEquals(depDetector.getWaitRecommendations(evD2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evD2, "A@Host")) {
      assertTrue(wr.getExpression().equals("E@Host"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("B@Host"));
      assertTrue(wr.getRecommendations().contains("C@Host"));
    }

    // Event<Integer>E
    final Event<Integer> evE = new Event<Integer>("Host", "E", 1);
    assertEquals(depDetector.getWaitRecommendations(evE, "A@Host").size(), 0);
  }

  @Test
  public void dualCyclesTest() {
    // B1 = f(A1)
    // C1 = f(A1)
    // B2 = f(A2)
    // C2 = f(A2)
    // D = f(B1, C1, B2, C2)
    final DependencyGraph graph = DependencyGraph.instance;
    final IntraSourceDependencyDetector depDetector = IntraSourceDependencyDetector.instance;
    graph.clear();

    final Subscription subA1 = new Subscription("Host", "A1");
    final Subscription subA2 = new Subscription("Host", "A2");
    final Subscription subB1 = new Subscription("Host", "B1");
    final Subscription subB2 = new Subscription("Host", "B2");
    final Subscription subC1 = new Subscription("Host", "C1");
    final Subscription subC2 = new Subscription("Host", "C2");

    final Advertisement advA1 = new Advertisement("Host", "A1");
    final Advertisement advA2 = new Advertisement("Host", "A2");
    final Advertisement advB1 = new Advertisement("Host", "B1");
    final Advertisement advB2 = new Advertisement("Host", "B2");
    final Advertisement advC1 = new Advertisement("Host", "C1");
    final Advertisement advC2 = new Advertisement("Host", "C2");
    final Advertisement advD = new Advertisement("Host", "D");

    // Subscription to A1 (A1 generates B2)
    final Set<Subscription> subsB1 = new HashSet<Subscription>();
    subsB1.add(subA1);
    graph.processAdv(advB1, subsB1);

    // Subscription to A2 (A2 generates B2)
    final Set<Subscription> subsB2 = new HashSet<Subscription>();
    subsB2.add(subA2);
    graph.processAdv(advB2, subsB2);

    // Subscription to A1 (A1 generates C1)
    final Set<Subscription> subsC1 = new HashSet<Subscription>();
    subsC1.add(subA1);
    graph.processAdv(advC1, subsC1);

    // Subscription to A2 (A2 generates C2)
    final Set<Subscription> subsC2 = new HashSet<Subscription>();
    subsC2.add(subA2);
    graph.processAdv(advC2, subsC2);

    // Subscription to B1, B2, C1, C2 (B1, B2, C1, C2 generate D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB1);
    subsD.add(subB2);
    subsD.add(subC1);
    subsD.add(subC2);
    graph.processAdv(advD, subsD);

    graph.processAdv(advA1);
    graph.processAdv(advA2);

    // Consolidate
    depDetector.consolidate();

    // Event<Integer>A1
    final Event<Integer> evA1 = new Event<Integer>("Host", "A1", 1);
    assertEquals(depDetector.getWaitRecommendations(evA1, "A1@Host").size(), 0);

    // Event<Integer>A2
    final Event<Integer> evA2 = new Event<Integer>("Host", "A2", 1);
    assertEquals(depDetector.getWaitRecommendations(evA2, "A2@Host").size(), 0);

    // Event<Integer>B1
    final Event<Integer> evB1 = new Event<Integer>("Host", "B1", 1);
    assertEquals(depDetector.getWaitRecommendations(evB1, "A1@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB1, "A1@Host")) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("C1@Host"));
    }

    // Event<Integer>C1
    final Event<Integer> evC1 = new Event<Integer>("Host", "C1", 1);
    assertEquals(depDetector.getWaitRecommendations(evC1, "A1@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evC1, "A1@Host")) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("B1@Host"));
    }

    // Event<Integer>B2
    final Event<Integer> evB2 = new Event<Integer>("Host", "B2", 1);
    assertEquals(depDetector.getWaitRecommendations(evB2, "A2@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, "A2@Host")) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("C2@Host"));
    }

    // Event<Integer>C2
    final Event<Integer> evC2 = new Event<Integer>("Host", "C2", 1);
    assertEquals(depDetector.getWaitRecommendations(evC2, "A2@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evC2, "A2@Host")) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("B2@Host"));
    }

    // Event<Integer>D
    final Event<Integer> evD = new Event<Integer>("Host", "D", 1);
    assertEquals(depDetector.getWaitRecommendations(evD, "A2@Host").size(), 0);
  }

  @Test
  public void dualDependencyTest() {
    // B = f(A1)
    // C = f(A1, A2)
    // D = f(B, C)
    final DependencyGraph graph = DependencyGraph.instance;
    final IntraSourceDependencyDetector depDetector = IntraSourceDependencyDetector.instance;
    graph.clear();

    final Subscription subA1 = new Subscription("Host", "A1");
    final Subscription subA2 = new Subscription("Host", "A2");
    final Subscription subB = new Subscription("Host", "B");
    final Subscription subC = new Subscription("Host", "C");

    final Advertisement advA1 = new Advertisement("Host", "A1");
    final Advertisement advA2 = new Advertisement("Host", "A2");
    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advC = new Advertisement("Host", "C");
    final Advertisement advD = new Advertisement("Host", "D");

    graph.processAdv(advA1);
    graph.processAdv(advA2);

    // Subscription to A1 (A1 generates B)
    final Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA1);
    graph.processAdv(advB, subsB);

    // Subscription to A1, A2 (A1, A2 generate C)
    final Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA1);
    subsC.add(subA2);
    graph.processAdv(advC, subsC);

    // Subscription to D (B, C generate D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB);
    subsD.add(subC);
    graph.processAdv(advD, subsD);

    // Consolidate
    depDetector.consolidate();

    // Event<Integer>A1
    final Event<Integer> evA1 = new Event<Integer>("Host", "A1", 1);
    assertEquals(depDetector.getWaitRecommendations(evA1, "A1@Host").size(), 0);

    // Event<Integer>A2
    final Event<Integer> evA2 = new Event<Integer>("Host", "A2", 1);
    assertEquals(depDetector.getWaitRecommendations(evA2, "A2@Host").size(), 0);

    // Event<Integer>B from A1
    final Event<Integer> evB1 = new Event<Integer>("Host", "B", 1);
    assertEquals(depDetector.getWaitRecommendations(evB1, "").size(), 0);
    final Event<Integer> evB2 = new Event<Integer>("Host", "B", 1);
    assertEquals(depDetector.getWaitRecommendations(evB2, "A1@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, "A1@Host")) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("C@Host"));
    }

    // Event<Integer>C from A1
    final Event<Integer> evC1_1 = new Event<Integer>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC1_1, "").size(), 0);
    final Event<Integer> evC1_2 = new Event<Integer>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC1_2, "A1@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evC1_2, "A1@Host")) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("B@Host"));
    }

    // Event<Integer>C from A2
    final Event<Integer> evC2_1 = new Event<Integer>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC2_1, "").size(), 0);
    final Event<Integer> evC2_2 = new Event<Integer>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC2_2, "A2@Host").size(), 0);

    // Event<Integer>D
    final Event<Integer> evD = new Event<Integer>("Host", "D", 1);
    assertEquals(depDetector.getWaitRecommendations(evD, "A2@Host").size(), 0);
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
    final DependencyGraph graph = DependencyGraph.instance;
    final IntraSourceDependencyDetector depDetector = IntraSourceDependencyDetector.instance;
    graph.clear();

    final Subscription subA = new Subscription("Host", "A");
    final Subscription subB = new Subscription("Host", "B");
    final Subscription subC = new Subscription("Host", "C");
    final Subscription subE = new Subscription("Host", "E");
    final Subscription subF = new Subscription("Host", "F");
    final Subscription subG = new Subscription("Host", "G");
    final Subscription subH = new Subscription("Host", "H");

    final Advertisement advA = new Advertisement("Host", "A");
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
    graph.processAdv(advB, subsB);

    // Subscription to A (A generates C)
    final Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA);
    graph.processAdv(advC, subsC);

    // Subscription to A (A generates E)
    final Set<Subscription> subsE = new HashSet<Subscription>();
    subsE.add(subA);
    graph.processAdv(advE, subsE);

    // Subscription to E (E generates G)
    final Set<Subscription> subsG = new HashSet<Subscription>();
    subsG.add(subE);
    graph.processAdv(advG, subsG);

    // Subscription to E (E generates F)
    final Set<Subscription> subsF = new HashSet<Subscription>();
    subsF.add(subE);
    graph.processAdv(advF, subsF);

    // Subscription to F, G (F, G generate H)
    final Set<Subscription> subsH = new HashSet<Subscription>();
    subsH.add(subF);
    subsH.add(subG);
    graph.processAdv(advH, subsH);

    // Subscription to B, C, H (B, C, H generate D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB);
    subsD.add(subC);
    subsD.add(subH);
    graph.processAdv(advD, subsD);

    graph.processAdv(advA);

    // Consolidate
    depDetector.consolidate();

    // Event<Integer>A
    final Event<Integer> evA = new Event<Integer>("Host", "A", 1);
    assertEquals(depDetector.getWaitRecommendations(evA, "A@Host").size(), 0);

    // Event<Integer>B
    final Event<Integer> evB1 = new Event<Integer>("Host", "B", 1);
    assertEquals(depDetector.getWaitRecommendations(evB1, "").size(), 0);
    final Event<Integer> evB2 = new Event<Integer>("Host", "B", 1);
    assertEquals(depDetector.getWaitRecommendations(evB2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB2, "A@Host")) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("C@Host"));
      assertTrue(wr.getRecommendations().contains("H@Host"));
    }

    // Event<Integer>C
    final Event<Integer> evC1 = new Event<Integer>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC1, "").size(), 0);
    final Event<Integer> evC2 = new Event<Integer>("Host", "C", 1);
    assertEquals(depDetector.getWaitRecommendations(evC2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evC2, "A@Host")) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("B@Host"));
      assertTrue(wr.getRecommendations().contains("H@Host"));
    }

    // Event<Integer>E
    final Event<Integer> evE = new Event<Integer>("Host", "E", 1);
    assertEquals(depDetector.getWaitRecommendations(evE, "A@Host").size(), 0);

    // Event<Integer>F
    final Event<Integer> evF1 = new Event<Integer>("Host", "F", 1);
    assertEquals(depDetector.getWaitRecommendations(evF1, "").size(), 0);
    final Event<Integer> evF2 = new Event<Integer>("Host", "F", 1);
    assertEquals(depDetector.getWaitRecommendations(evF2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evF2, "A@Host")) {
      assertTrue(wr.getExpression().equals("H@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("G@Host"));
    }

    // Event<Integer>G
    final Event<Integer> evG1 = new Event<Integer>("Host", "G", 1);
    assertEquals(depDetector.getWaitRecommendations(evG1, "").size(), 0);
    final Event<Integer> evG2 = new Event<Integer>("Host", "G", 1);
    assertEquals(depDetector.getWaitRecommendations(evG2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evG2, "A@Host")) {
      assertTrue(wr.getExpression().equals("H@Host"));
      assertEquals(wr.getRecommendations().size(), 1);
      assertTrue(wr.getRecommendations().contains("F@Host"));
    }

    // Event<Integer>H
    final Event<Integer> evH1 = new Event<Integer>("Host", "H", 1);
    assertEquals(depDetector.getWaitRecommendations(evH1, "").size(), 0);
    final Event<Integer> evH2 = new Event<Integer>("Host", "H", 1);
    assertEquals(depDetector.getWaitRecommendations(evH2, "A@Host").size(), 1);
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evH2, "A@Host")) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(wr.getRecommendations().size(), 2);
      assertTrue(wr.getRecommendations().contains("B@Host"));
      assertTrue(wr.getRecommendations().contains("C@Host"));
    }

    // Event<Integer>D
    final Event<Integer> evD = new Event<Integer>("Host", "D", 1);
    assertEquals(depDetector.getWaitRecommendations(evD, "A@Host").size(), 0);
  }

  @Test
  public void multipleCyclesTest() {
    // B = f(A)
    // C = f(A, B)
    // D = f(B, C)
    // E = f(A, D)
    final DependencyGraph graph = DependencyGraph.instance;
    final IntraSourceDependencyDetector depDetector = IntraSourceDependencyDetector.instance;
    graph.clear();

    final Subscription subA = new Subscription("Host", "A");
    final Subscription subB = new Subscription("Host", "B");
    final Subscription subC = new Subscription("Host", "C");
    final Subscription subD = new Subscription("Host", "D");

    final Advertisement advA = new Advertisement("Host", "A");
    final Advertisement advB = new Advertisement("Host", "B");
    final Advertisement advC = new Advertisement("Host", "C");
    final Advertisement advD = new Advertisement("Host", "D");
    final Advertisement advE = new Advertisement("Host", "E");

    // Subscription to A (A generates B)
    final Set<Subscription> subsB = new HashSet<Subscription>();
    subsB.add(subA);
    graph.processAdv(advB, subsB);

    // Subscription to A, B (A, B generate C)
    final Set<Subscription> subsC = new HashSet<Subscription>();
    subsC.add(subA);
    subsC.add(subB);
    graph.processAdv(advC, subsC);

    // Subscription to B, C (B, C generate D)
    final Set<Subscription> subsD = new HashSet<Subscription>();
    subsD.add(subB);
    subsD.add(subC);
    graph.processAdv(advD, subsD);

    // Subscription to A, D (A, D generate E)
    final Set<Subscription> subsE = new HashSet<Subscription>();
    subsE.add(subA);
    subsE.add(subD);
    graph.processAdv(advE, subsE);

    graph.processAdv(advA);

    // Consolidate
    depDetector.consolidate();

    // Event<Integer>A
    final Event<Integer> evA = new Event<Integer>("Host", "A", 1);
    assertEquals(2, depDetector.getWaitRecommendations(evA, "A@Host").size());
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evA, "A@Host")) {
      assertTrue(wr.getExpression().equals("C@Host") || wr.getExpression().equals("E@Host"));
      if (wr.getExpression().equals("C@Host")) {
        assertEquals(1, wr.getRecommendations().size());
        assertTrue(wr.getRecommendations().contains("B@Host"));
      }
      if (wr.getExpression().equals("E@Host")) {
        assertEquals(1, wr.getRecommendations().size());
        assertTrue(wr.getRecommendations().contains("D@Host"));
      }
    }

    // Event<Integer>B
    final Event<Integer> evB = new Event<Integer>("Host", "B", 1);
    assertEquals(2, depDetector.getWaitRecommendations(evB, "A@Host").size());
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evB, "A@Host")) {
      assertTrue(wr.getExpression().equals("C@Host") || wr.getExpression().equals("D@Host"));
      if (wr.getExpression().equals("C@Host")) {
        assertEquals(1, wr.getRecommendations().size());
        assertTrue(wr.getRecommendations().contains("A@Host"));
      }
      if (wr.getExpression().equals("D@Host")) {
        assertEquals(1, wr.getRecommendations().size());
        assertTrue(wr.getRecommendations().contains("C@Host"));
      }
    }

    // Event<Integer>C
    final Event<Integer> evC = new Event<Integer>("Host", "C", 1);
    assertEquals(1, depDetector.getWaitRecommendations(evC, "A@Host").size());
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evC, "A@Host")) {
      assertTrue(wr.getExpression().equals("D@Host"));
      assertEquals(1, wr.getRecommendations().size());
      assertTrue(wr.getRecommendations().contains("B@Host"));
    }

    // Event<Integer>D
    final Event<Integer> evD = new Event<Integer>("Host", "D", 1);
    assertEquals(1, depDetector.getWaitRecommendations(evD, "A@Host").size());
    for (final WaitRecommendations wr : depDetector.getWaitRecommendations(evD, "A@Host")) {
      assertTrue(wr.getExpression().equals("E@Host"));
      assertEquals(1, wr.getRecommendations().size());
      assertTrue(wr.getRecommendations().contains("A@Host"));
    }

    // Event<Integer>E
    final Event<Integer> evE = new Event<Integer>("Host", "E", 1);
    assertTrue(depDetector.getWaitRecommendations(evE, "A@Host").isEmpty());
  }
}