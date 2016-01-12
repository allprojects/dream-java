package dream.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Subscription;
import dream.common.utils.DependencyGraph;
import dream.common.utils.DependencyGraphUtils;

public class DependencyGraphUtilsTest {

  @Test
  public void test1() {
    final DependencyGraph graph = DependencyGraph.instance;
    graph.clear();

    assertTrue(DependencyGraphUtils.computeRelevantSources().isEmpty());
    assertTrue(DependencyGraphUtils.computeDependencyClosure().isEmpty());

    graph.processAdv(new Advertisement("host", "A"));

    Map<String, Set<String>> relevantSources = DependencyGraphUtils.computeRelevantSources();
    assertEquals(1, relevantSources.size());
    assertTrue(relevantSources.containsKey("A@host"));
    assertTrue(relevantSources.get("A@host").contains("A@host"));

    Map<String, Set<String>> dependencyClosure = DependencyGraphUtils.computeDependencyClosure();
    assertEquals(1, dependencyClosure.size());
    assertTrue(dependencyClosure.containsKey("A@host"));
    assertEquals(1, dependencyClosure.get("A@host").size());
    assertTrue(dependencyClosure.get("A@host").contains("A@host"));

    final Set<Subscription> subsB = new HashSet<>();
    subsB.add(new Subscription("host", "A"));
    graph.processAdv(new Advertisement("host", "B"), subsB);

    relevantSources = DependencyGraphUtils.computeRelevantSources();
    assertEquals(2, relevantSources.size());
    assertTrue(relevantSources.containsKey("A@host"));
    assertEquals(1, relevantSources.get("A@host").size());
    assertTrue(relevantSources.get("A@host").contains("A@host"));
    assertTrue(relevantSources.containsKey("B@host"));
    assertEquals(1, relevantSources.get("B@host").size());
    assertTrue(relevantSources.get("B@host").contains("A@host"));

    dependencyClosure = DependencyGraphUtils.computeDependencyClosure();
    assertEquals(1, dependencyClosure.size());
    assertTrue(dependencyClosure.containsKey("A@host"));
    assertEquals(2, dependencyClosure.get("A@host").size());
    assertTrue(dependencyClosure.get("A@host").contains("A@host"));
    assertTrue(dependencyClosure.get("A@host").contains("B@host"));

    final Set<Subscription> subsC = new HashSet<>();
    subsC.add(new Subscription("host", "A"));
    graph.processAdv(new Advertisement("host", "C"), subsC);

    relevantSources = DependencyGraphUtils.computeRelevantSources();
    assertEquals(3, relevantSources.size());
    assertTrue(relevantSources.containsKey("A@host"));
    assertTrue(relevantSources.containsKey("B@host"));
    assertTrue(relevantSources.containsKey("C@host"));
    assertEquals(1, relevantSources.get("A@host").size());
    assertEquals(1, relevantSources.get("B@host").size());
    assertEquals(1, relevantSources.get("C@host").size());
    assertTrue(relevantSources.get("B@host").contains("A@host"));
    assertTrue(relevantSources.get("C@host").contains("A@host"));

    dependencyClosure = DependencyGraphUtils.computeDependencyClosure();
    assertEquals(1, dependencyClosure.size());
    assertTrue(dependencyClosure.containsKey("A@host"));
    assertEquals(3, dependencyClosure.get("A@host").size());
    assertTrue(dependencyClosure.get("A@host").contains("A@host"));
    assertTrue(dependencyClosure.get("A@host").contains("B@host"));
    assertTrue(dependencyClosure.get("A@host").contains("C@host"));

    final Set<Subscription> subsD = new HashSet<>();
    subsD.add(new Subscription("host", "B"));
    subsD.add(new Subscription("host", "C"));
    graph.processAdv(new Advertisement("host", "D"), subsD);

    relevantSources = DependencyGraphUtils.computeRelevantSources();
    assertEquals(4, relevantSources.size());
    assertTrue(relevantSources.containsKey("A@host"));
    assertTrue(relevantSources.containsKey("B@host"));
    assertTrue(relevantSources.containsKey("C@host"));
    assertTrue(relevantSources.containsKey("D@host"));
    assertEquals(1, relevantSources.get("A@host").size());
    assertEquals(1, relevantSources.get("B@host").size());
    assertEquals(1, relevantSources.get("C@host").size());
    assertEquals(1, relevantSources.get("D@host").size());
    assertTrue(relevantSources.get("A@host").contains("A@host"));
    assertTrue(relevantSources.get("B@host").contains("A@host"));
    assertTrue(relevantSources.get("C@host").contains("A@host"));
    assertTrue(relevantSources.get("D@host").contains("A@host"));

    dependencyClosure = DependencyGraphUtils.computeDependencyClosure();
    assertEquals(1, dependencyClosure.size());
    assertTrue(dependencyClosure.containsKey("A@host"));
    assertEquals(4, dependencyClosure.get("A@host").size());
    assertTrue(dependencyClosure.get("A@host").contains("A@host"));
    assertTrue(dependencyClosure.get("A@host").contains("B@host"));
    assertTrue(dependencyClosure.get("A@host").contains("C@host"));
    assertTrue(dependencyClosure.get("A@host").contains("D@host"));

  }

  @Test
  public void test2() {
    final DependencyGraph graph = DependencyGraph.instance;
    graph.clear();

    assertTrue(DependencyGraphUtils.computeRelevantSources().isEmpty());
    assertTrue(DependencyGraphUtils.computeDependencyClosure().isEmpty());

    graph.processAdv(new Advertisement("host", "A1"));
    graph.processAdv(new Advertisement("host", "A2"));

    Map<String, Set<String>> relevantSources = DependencyGraphUtils.computeRelevantSources();
    assertEquals(2, relevantSources.size());
    assertTrue(relevantSources.containsKey("A1@host"));
    assertTrue(relevantSources.get("A1@host").contains("A1@host"));
    assertTrue(relevantSources.containsKey("A2@host"));
    assertTrue(relevantSources.get("A2@host").contains("A2@host"));

    Map<String, Set<String>> dependencyClosure = DependencyGraphUtils.computeDependencyClosure();
    assertEquals(2, dependencyClosure.size());
    assertTrue(dependencyClosure.containsKey("A1@host"));
    assertEquals(1, dependencyClosure.get("A1@host").size());
    assertTrue(dependencyClosure.get("A1@host").contains("A1@host"));
    assertTrue(dependencyClosure.containsKey("A2@host"));
    assertEquals(1, dependencyClosure.get("A2@host").size());
    assertTrue(dependencyClosure.get("A2@host").contains("A2@host"));

    final Set<Subscription> subsB = new HashSet<>();
    subsB.add(new Subscription("host", "A1"));
    subsB.add(new Subscription("host", "A2"));
    graph.processAdv(new Advertisement("host", "B"), subsB);

    relevantSources = DependencyGraphUtils.computeRelevantSources();
    assertEquals(3, relevantSources.size());
    assertTrue(relevantSources.containsKey("A1@host"));
    assertEquals(1, relevantSources.get("A1@host").size());
    assertTrue(relevantSources.get("A1@host").contains("A1@host"));
    assertTrue(relevantSources.containsKey("A2@host"));
    assertEquals(1, relevantSources.get("A2@host").size());
    assertTrue(relevantSources.get("A2@host").contains("A2@host"));
    assertTrue(relevantSources.containsKey("B@host"));
    assertEquals(2, relevantSources.get("B@host").size());
    assertTrue(relevantSources.get("B@host").contains("A1@host"));
    assertTrue(relevantSources.get("B@host").contains("A2@host"));

    dependencyClosure = DependencyGraphUtils.computeDependencyClosure();
    assertEquals(2, dependencyClosure.size());
    assertTrue(dependencyClosure.containsKey("A1@host"));
    assertEquals(2, dependencyClosure.get("A1@host").size());
    assertTrue(dependencyClosure.get("A1@host").contains("A1@host"));
    assertTrue(dependencyClosure.get("A1@host").contains("B@host"));
    assertTrue(dependencyClosure.containsKey("A2@host"));
    assertEquals(2, dependencyClosure.get("A2@host").size());
    assertTrue(dependencyClosure.get("A2@host").contains("A2@host"));
    assertTrue(dependencyClosure.get("A2@host").contains("B@host"));

    final Set<Subscription> subsC = new HashSet<>();
    subsC.add(new Subscription("host", "A1"));
    graph.processAdv(new Advertisement("host", "C"), subsC);

    relevantSources = DependencyGraphUtils.computeRelevantSources();
    assertEquals(4, relevantSources.size());
    assertTrue(relevantSources.containsKey("A1@host"));
    assertTrue(relevantSources.containsKey("A2@host"));
    assertTrue(relevantSources.containsKey("B@host"));
    assertTrue(relevantSources.containsKey("C@host"));
    assertEquals(1, relevantSources.get("A1@host").size());
    assertEquals(1, relevantSources.get("A2@host").size());
    assertEquals(2, relevantSources.get("B@host").size());
    assertEquals(1, relevantSources.get("C@host").size());
    assertTrue(relevantSources.get("B@host").contains("A1@host"));
    assertTrue(relevantSources.get("B@host").contains("A2@host"));
    assertTrue(relevantSources.get("C@host").contains("A1@host"));

    dependencyClosure = DependencyGraphUtils.computeDependencyClosure();
    assertEquals(2, dependencyClosure.size());
    assertTrue(dependencyClosure.containsKey("A1@host"));
    assertTrue(dependencyClosure.containsKey("A2@host"));
    assertEquals(3, dependencyClosure.get("A1@host").size());
    assertTrue(dependencyClosure.get("A1@host").contains("A1@host"));
    assertTrue(dependencyClosure.get("A1@host").contains("B@host"));
    assertTrue(dependencyClosure.get("A1@host").contains("C@host"));
    assertEquals(2, dependencyClosure.get("A2@host").size());
    assertTrue(dependencyClosure.get("A2@host").contains("A2@host"));
    assertTrue(dependencyClosure.get("A2@host").contains("B@host"));

    final Set<Subscription> subsD = new HashSet<>();
    subsD.add(new Subscription("host", "B"));
    subsD.add(new Subscription("host", "C"));
    graph.processAdv(new Advertisement("host", "D"), subsD);

    relevantSources = DependencyGraphUtils.computeRelevantSources();
    assertEquals(5, relevantSources.size());
    assertTrue(relevantSources.containsKey("A1@host"));
    assertTrue(relevantSources.containsKey("A2@host"));
    assertTrue(relevantSources.containsKey("B@host"));
    assertTrue(relevantSources.containsKey("C@host"));
    assertTrue(relevantSources.containsKey("D@host"));
    assertEquals(1, relevantSources.get("A1@host").size());
    assertEquals(1, relevantSources.get("A2@host").size());
    assertEquals(2, relevantSources.get("B@host").size());
    assertEquals(1, relevantSources.get("C@host").size());
    assertEquals(2, relevantSources.get("D@host").size());
    assertTrue(relevantSources.get("A1@host").contains("A1@host"));
    assertTrue(relevantSources.get("B@host").contains("A1@host"));
    assertTrue(relevantSources.get("B@host").contains("A2@host"));
    assertTrue(relevantSources.get("C@host").contains("A1@host"));
    assertTrue(relevantSources.get("D@host").contains("A1@host"));

    dependencyClosure = DependencyGraphUtils.computeDependencyClosure();
    assertEquals(2, dependencyClosure.size());
    assertTrue(dependencyClosure.containsKey("A1@host"));
    assertTrue(dependencyClosure.containsKey("A2@host"));
    assertEquals(4, dependencyClosure.get("A1@host").size());
    assertTrue(dependencyClosure.get("A1@host").contains("A1@host"));
    assertTrue(dependencyClosure.get("A1@host").contains("B@host"));
    assertTrue(dependencyClosure.get("A1@host").contains("C@host"));
    assertTrue(dependencyClosure.get("A1@host").contains("D@host"));
    assertEquals(3, dependencyClosure.get("A2@host").size());
    assertTrue(dependencyClosure.get("A2@host").contains("A2@host"));
    assertTrue(dependencyClosure.get("A2@host").contains("B@host"));
    assertTrue(dependencyClosure.get("A2@host").contains("D@host"));

  }

}
