package dream.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Subscription;
import dream.common.utils.CompleteGlitchFreeDependencyDetector;
import dream.common.utils.DependencyGraph;

public class CompleteGlitchFreeDependencyDetectorTest {

  @Test
  public void test1() {
    final DependencyGraph graph = DependencyGraph.instance;
    graph.clear();
    final CompleteGlitchFreeDependencyDetector depDetector = new CompleteGlitchFreeDependencyDetector();

    graph.processAdv(new Advertisement("host", "A"));
    depDetector.consolidate();
    assertTrue(depDetector.getNodesToLockFor("A@host").isEmpty());

    final Set<Subscription> subsB = new HashSet<>();
    subsB.add(new Subscription("host", "A"));
    graph.processAdv(new Advertisement("host", "B"), subsB);
    depDetector.consolidate();
    assertTrue(depDetector.getNodesToLockFor("A@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("B@host").isEmpty());

    final Set<Subscription> subsC = new HashSet<>();
    subsC.add(new Subscription("host", "A"));
    graph.processAdv(new Advertisement("host", "C"), subsC);
    depDetector.consolidate();
    assertTrue(depDetector.getNodesToLockFor("A@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("B@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("C@host").isEmpty());

    final Set<Subscription> subsD = new HashSet<>();
    subsD.add(new Subscription("host", "B"));
    subsD.add(new Subscription("host", "C"));
    graph.processAdv(new Advertisement("host", "D"), subsD);
    depDetector.consolidate();
    assertTrue(depDetector.getNodesToLockFor("A@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("B@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("C@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("D@host").isEmpty());
  }

  @Test
  public void test2() {
    final DependencyGraph graph = DependencyGraph.instance;
    graph.clear();
    final CompleteGlitchFreeDependencyDetector depDetector = new CompleteGlitchFreeDependencyDetector();

    graph.processAdv(new Advertisement("host", "A1"));
    graph.processAdv(new Advertisement("host", "A2"));
    depDetector.consolidate();
    assertTrue(depDetector.getNodesToLockFor("A1@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("A2@host").isEmpty());

    final Set<Subscription> subsB = new HashSet<>();
    subsB.add(new Subscription("host", "A1"));
    subsB.add(new Subscription("host", "A2"));
    graph.processAdv(new Advertisement("host", "B"), subsB);
    depDetector.consolidate();
    assertTrue(depDetector.getNodesToLockFor("A1@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("A2@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("B@host").isEmpty());

    final Set<Subscription> subsC = new HashSet<>();
    subsC.add(new Subscription("host", "A1"));
    graph.processAdv(new Advertisement("host", "C"), subsC);
    depDetector.consolidate();
    assertTrue(depDetector.getNodesToLockFor("A1@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("A2@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("B@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("C@host").isEmpty());

    final Set<Subscription> subsD = new HashSet<>();
    subsD.add(new Subscription("host", "B"));
    subsD.add(new Subscription("host", "C"));
    graph.processAdv(new Advertisement("host", "D"), subsD);
    depDetector.consolidate();
    assertEquals(2, depDetector.getNodesToLockFor("A1@host").size());
    assertTrue(depDetector.getNodesToLockFor("A1@host").contains("B@host"));
    assertTrue(depDetector.getNodesToLockFor("A1@host").contains("D@host"));
    assertEquals(2, depDetector.getNodesToLockFor("A2@host").size());
    assertTrue(depDetector.getNodesToLockFor("A2@host").contains("B@host"));
    assertTrue(depDetector.getNodesToLockFor("A2@host").contains("D@host"));
    assertTrue(depDetector.getNodesToLockFor("B@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("C@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("D@host").isEmpty());
  }

  @Test
  public void test3() {
    final DependencyGraph graph = DependencyGraph.instance;
    graph.clear();
    final CompleteGlitchFreeDependencyDetector depDetector = new CompleteGlitchFreeDependencyDetector();

    graph.processAdv(new Advertisement("host", "A1"));
    graph.processAdv(new Advertisement("host", "A2"));
    depDetector.consolidate();
    assertTrue(depDetector.getNodesToLockFor("A1@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("A2@host").isEmpty());

    final Set<Subscription> subsB = new HashSet<>();
    subsB.add(new Subscription("host", "A1"));
    subsB.add(new Subscription("host", "A2"));
    graph.processAdv(new Advertisement("host", "B"), subsB);
    depDetector.consolidate();
    assertTrue(depDetector.getNodesToLockFor("A1@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("A2@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("B@host").isEmpty());

    final Set<Subscription> subsC = new HashSet<>();
    subsC.add(new Subscription("host", "A1"));
    subsC.add(new Subscription("host", "A2"));
    graph.processAdv(new Advertisement("host", "C"), subsC);
    depDetector.consolidate();
    assertTrue(depDetector.getNodesToLockFor("B@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("C@host").isEmpty());
    assertEquals(2, depDetector.getNodesToLockFor("A1@host").size());
    assertEquals(2, depDetector.getNodesToLockFor("A2@host").size());
    assertTrue(depDetector.getNodesToLockFor("A1@host").contains("B@host"));
    assertTrue(depDetector.getNodesToLockFor("A1@host").contains("C@host"));
    assertTrue(depDetector.getNodesToLockFor("A2@host").contains("B@host"));
    assertTrue(depDetector.getNodesToLockFor("A2@host").contains("C@host"));

    final Set<Subscription> subsD = new HashSet<>();
    subsD.add(new Subscription("host", "B"));
    subsD.add(new Subscription("host", "C"));
    graph.processAdv(new Advertisement("host", "D"), subsD);
    depDetector.consolidate();
    assertTrue(depDetector.getNodesToLockFor("B@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("C@host").isEmpty());
    assertTrue(depDetector.getNodesToLockFor("D@host").isEmpty());
    assertEquals(3, depDetector.getNodesToLockFor("A1@host").size());
    assertEquals(3, depDetector.getNodesToLockFor("A2@host").size());
    assertTrue(depDetector.getNodesToLockFor("A1@host").contains("B@host"));
    assertTrue(depDetector.getNodesToLockFor("A1@host").contains("C@host"));
    assertTrue(depDetector.getNodesToLockFor("A1@host").contains("D@host"));
    assertTrue(depDetector.getNodesToLockFor("A2@host").contains("B@host"));
    assertTrue(depDetector.getNodesToLockFor("A2@host").contains("C@host"));
    assertTrue(depDetector.getNodesToLockFor("A2@host").contains("D@host"));

  }

}
