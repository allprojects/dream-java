package dream.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Subscription;

public class AtomicDependencyDetectorTest {

	@Test
	public void test1() {
		final DependencyGraph graph = DependencyGraph.instance;
		graph.clear();
		final AtomicDependencyDetector depDetector = new AtomicDependencyDetector();

		graph.processAdv(new Advertisement("host", "A"));
		depDetector.consolidate();
		assertEquals(1, depDetector.getNodesToLockFor("A@host").size());
		assertTrue(depDetector.getNodesToLockFor("A@host").contains("A@host"));

		final Set<Subscription<?>> subsB = new HashSet<>();
		subsB.add(new Subscription<>("host", "A"));
		graph.processAdv(new Advertisement("host", "B"), subsB);
		depDetector.consolidate();
		assertEquals(2, depDetector.getNodesToLockFor("A@host").size());
		assertTrue(depDetector.getNodesToLockFor("A@host").contains("A@host"));
		assertTrue(depDetector.getNodesToLockFor("A@host").contains("B@host"));

		final Set<Subscription<?>> subsC = new HashSet<>();
		subsC.add(new Subscription<>("host", "A"));
		graph.processAdv(new Advertisement("host", "C"), subsC);
		depDetector.consolidate();
		assertEquals(3, depDetector.getNodesToLockFor("A@host").size());
		assertTrue(depDetector.getNodesToLockFor("A@host").contains("A@host"));
		assertTrue(depDetector.getNodesToLockFor("A@host").contains("B@host"));
		assertTrue(depDetector.getNodesToLockFor("A@host").contains("C@host"));

		final Set<Subscription<?>> subsD = new HashSet<>();
		subsD.add(new Subscription<>("host", "B"));
		subsD.add(new Subscription<>("host", "C"));
		graph.processAdv(new Advertisement("host", "D"), subsD);
		depDetector.consolidate();
		assertEquals(4, depDetector.getNodesToLockFor("A@host").size());
		assertTrue(depDetector.getNodesToLockFor("A@host").contains("A@host"));
		assertTrue(depDetector.getNodesToLockFor("A@host").contains("B@host"));
		assertTrue(depDetector.getNodesToLockFor("A@host").contains("C@host"));
		assertTrue(depDetector.getNodesToLockFor("A@host").contains("D@host"));
	}

	@Test
	public void test2() {
		final DependencyGraph graph = DependencyGraph.instance;
		graph.clear();
		final AtomicDependencyDetector depDetector = new AtomicDependencyDetector();

		graph.processAdv(new Advertisement("host", "A1"));
		graph.processAdv(new Advertisement("host", "A2"));
		depDetector.consolidate();
		assertEquals(1, depDetector.getNodesToLockFor("A1@host").size());
		assertEquals(1, depDetector.getNodesToLockFor("A2@host").size());
		assertTrue(depDetector.getNodesToLockFor("A1@host").contains("A1@host"));
		assertTrue(depDetector.getNodesToLockFor("A2@host").contains("A2@host"));

		final Set<Subscription<?>> subsB = new HashSet<>();
		subsB.add(new Subscription<>("host", "A1"));
		subsB.add(new Subscription<>("host", "A2"));
		graph.processAdv(new Advertisement("host", "B"), subsB);
		depDetector.consolidate();
		assertEquals(2, depDetector.getNodesToLockFor("A1@host").size());
		assertEquals(2, depDetector.getNodesToLockFor("A2@host").size());
		assertTrue(depDetector.getNodesToLockFor("A1@host").contains("A1@host"));
		assertTrue(depDetector.getNodesToLockFor("A2@host").contains("A2@host"));
		assertTrue(depDetector.getNodesToLockFor("A1@host").contains("B@host"));
		assertTrue(depDetector.getNodesToLockFor("A2@host").contains("B@host"));

		final Set<Subscription<?>> subsC = new HashSet<>();
		subsC.add(new Subscription<>("host", "A1"));
		graph.processAdv(new Advertisement("host", "C"), subsC);
		depDetector.consolidate();
		assertEquals(3, depDetector.getNodesToLockFor("A1@host").size());
		assertEquals(2, depDetector.getNodesToLockFor("A2@host").size());
		assertTrue(depDetector.getNodesToLockFor("A1@host").contains("A1@host"));
		assertTrue(depDetector.getNodesToLockFor("A2@host").contains("A2@host"));
		assertTrue(depDetector.getNodesToLockFor("A1@host").contains("B@host"));
		assertTrue(depDetector.getNodesToLockFor("A2@host").contains("B@host"));
		assertTrue(depDetector.getNodesToLockFor("A1@host").contains("C@host"));

		final Set<Subscription<?>> subsD = new HashSet<>();
		subsD.add(new Subscription<>("host", "B"));
		subsD.add(new Subscription<>("host", "C"));
		graph.processAdv(new Advertisement("host", "D"), subsD);
		depDetector.consolidate();
		assertEquals(4, depDetector.getNodesToLockFor("A1@host").size());
		assertEquals(3, depDetector.getNodesToLockFor("A2@host").size());
		assertTrue(depDetector.getNodesToLockFor("A1@host").contains("A1@host"));
		assertTrue(depDetector.getNodesToLockFor("A2@host").contains("A2@host"));
		assertTrue(depDetector.getNodesToLockFor("A1@host").contains("B@host"));
		assertTrue(depDetector.getNodesToLockFor("A2@host").contains("B@host"));
		assertTrue(depDetector.getNodesToLockFor("A1@host").contains("C@host"));
		assertTrue(depDetector.getNodesToLockFor("A1@host").contains("D@host"));
		assertTrue(depDetector.getNodesToLockFor("A2@host").contains("D@host"));
	}
}
