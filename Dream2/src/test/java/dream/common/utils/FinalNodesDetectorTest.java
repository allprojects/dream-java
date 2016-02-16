package dream.common.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Subscription;

public class FinalNodesDetectorTest {

	private static final String hostId = "hostId";
	private static final String atHostId = "@" + hostId;

	@Test
	public void singleExpressionTest() {
		// A
		DependencyGraph.instance.clear();
		final FinalNodesDetector finalNodesDetector = new FinalNodesDetector();

		generateAdvertisementPacket(finalNodesDetector, "A");
		finalNodesDetector.consolidate();

		final Set<String> results = finalNodesDetector.getFinalNodesFor("A" + atHostId);
		assertEquals(1, results.size());
		assertTrue(results.contains("A" + atHostId));
	}

	@Test
	public void simpleDependencyTest() {
		// A
		// B = f(A)
		DependencyGraph.instance.clear();
		final FinalNodesDetector finalNodesDetector = new FinalNodesDetector();

		generateAdvertisementPacket(finalNodesDetector, "A");
		generateAdvertisementPacket(finalNodesDetector, "B", "A");
		finalNodesDetector.consolidate();

		final Set<String> results = finalNodesDetector.getFinalNodesFor("A" + atHostId);
		assertEquals(1, results.size());
		assertTrue(results.contains("B" + atHostId));
	}

	@Test
	public void simpleDependencyTest2() {
		// A
		// B = f(A)
		// C = f(B)
		// D = f(C)
		DependencyGraph.instance.clear();
		final FinalNodesDetector finalNodesDetector = new FinalNodesDetector();

		generateAdvertisementPacket(finalNodesDetector, "A");
		generateAdvertisementPacket(finalNodesDetector, "B", "A");
		generateAdvertisementPacket(finalNodesDetector, "C", "B");
		generateAdvertisementPacket(finalNodesDetector, "D", "C");
		finalNodesDetector.consolidate();

		final Set<String> results = finalNodesDetector.getFinalNodesFor("A" + atHostId);
		assertEquals(1, results.size());
		assertTrue(results.contains("D" + atHostId));
	}

	@Test
	public void simpleDependencyTest3() {
		// A
		// B = f(A)
		// C = f(A)
		DependencyGraph.instance.clear();
		final FinalNodesDetector finalNodesDetector = new FinalNodesDetector();

		generateAdvertisementPacket(finalNodesDetector, "A");
		generateAdvertisementPacket(finalNodesDetector, "B", "A");
		generateAdvertisementPacket(finalNodesDetector, "C", "A");
		finalNodesDetector.consolidate();

		final Set<String> results = finalNodesDetector.getFinalNodesFor("A" + atHostId);
		assertEquals(2, results.size());
		assertTrue(results.contains("B" + atHostId));
		assertTrue(results.contains("C" + atHostId));
	}

	@Test
	public void doubleDependencyTest() {
		// A
		// B = f(A)
		// C = f(B)
		// D = f(B)
		DependencyGraph.instance.clear();
		final FinalNodesDetector finalNodesDetector = new FinalNodesDetector();

		generateAdvertisementPacket(finalNodesDetector, "A");
		generateAdvertisementPacket(finalNodesDetector, "B", "A");
		generateAdvertisementPacket(finalNodesDetector, "C", "B");
		generateAdvertisementPacket(finalNodesDetector, "D", "B");
		finalNodesDetector.consolidate();

		final Set<String> results = finalNodesDetector.getFinalNodesFor("A" + atHostId);
		assertEquals(2, results.size());
		assertTrue(results.contains("C" + atHostId));
		assertTrue(results.contains("C" + atHostId));
	}

	@Test
	public void tripleDependencyTest() {
		// A
		// B = f(A)
		// C = f(B)
		// D = f(B)
		// E = f(D)
		DependencyGraph.instance.clear();
		final FinalNodesDetector finalNodesDetector = new FinalNodesDetector();

		generateAdvertisementPacket(finalNodesDetector, "A");
		generateAdvertisementPacket(finalNodesDetector, "B", "A");
		generateAdvertisementPacket(finalNodesDetector, "C", "B");
		generateAdvertisementPacket(finalNodesDetector, "D", "B");
		generateAdvertisementPacket(finalNodesDetector, "E", "D");
		finalNodesDetector.consolidate();

		final Set<String> results = finalNodesDetector.getFinalNodesFor("A" + atHostId);
		assertEquals(2, results.size());
		assertTrue(results.contains("C" + atHostId));
		assertTrue(results.contains("E" + atHostId));
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
		DependencyGraph.instance.clear();
		final FinalNodesDetector finalNodesDetector = new FinalNodesDetector();

		generateAdvertisementPacket(finalNodesDetector, "A");
		generateAdvertisementPacket(finalNodesDetector, "B", "A");
		generateAdvertisementPacket(finalNodesDetector, "C", "A");
		generateAdvertisementPacket(finalNodesDetector, "D", "B");
		generateAdvertisementPacket(finalNodesDetector, "E", "B");
		generateAdvertisementPacket(finalNodesDetector, "F", "C");
		generateAdvertisementPacket(finalNodesDetector, "G", "C");
		finalNodesDetector.consolidate();

		final Set<String> results = finalNodesDetector.getFinalNodesFor("A" + atHostId);
		assertEquals(4, results.size());
		assertTrue(results.contains("D" + atHostId));
		assertTrue(results.contains("E" + atHostId));
		assertTrue(results.contains("F" + atHostId));
		assertTrue(results.contains("G" + atHostId));
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
		DependencyGraph.instance.clear();
		final FinalNodesDetector finalNodesDetector = new FinalNodesDetector();

		generateAdvertisementPacket(finalNodesDetector, "A");
		generateAdvertisementPacket(finalNodesDetector, "B", "A");
		generateAdvertisementPacket(finalNodesDetector, "C", "A");
		generateAdvertisementPacket(finalNodesDetector, "D", "B");
		generateAdvertisementPacket(finalNodesDetector, "E", "B");
		generateAdvertisementPacket(finalNodesDetector, "F", "C");
		generateAdvertisementPacket(finalNodesDetector, "G", "C");
		generateAdvertisementPacket(finalNodesDetector, "H", "G");
		finalNodesDetector.consolidate();

		final Set<String> results = finalNodesDetector.getFinalNodesFor("A" + atHostId);
		assertEquals(4, results.size());
		assertTrue(results.contains("D" + atHostId));
		assertTrue(results.contains("E" + atHostId));
		assertTrue(results.contains("F" + atHostId));
		assertTrue(results.contains("H" + atHostId));
	}

	@Test
	public void graphDependencyTest() {
		// A
		// B
		// C = f(A, B)
		// D = f(A, B)
		// E = f(C, D)
		DependencyGraph.instance.clear();
		final FinalNodesDetector finalNodesDetector = new FinalNodesDetector();

		generateAdvertisementPacket(finalNodesDetector, "A");
		generateAdvertisementPacket(finalNodesDetector, "B");
		generateAdvertisementPacket(finalNodesDetector, "C", "A", "B");
		generateAdvertisementPacket(finalNodesDetector, "D", "A", "B");
		generateAdvertisementPacket(finalNodesDetector, "E", "C", "D");
		finalNodesDetector.consolidate();

		final Set<String> resultsA = finalNodesDetector.getFinalNodesFor("A" + atHostId);
		assertEquals(1, resultsA.size());
		assertTrue(resultsA.contains("E" + atHostId));

		final Set<String> resultsB = finalNodesDetector.getFinalNodesFor("B" + atHostId);
		assertEquals(1, resultsB.size());
		assertTrue(resultsB.contains("E" + atHostId));
	}

	@Test
	public void triangularDependencyTest() {
		// A
		// B = f(A)
		// C = f(A, B)
		DependencyGraph.instance.clear();
		final FinalNodesDetector finalNodesDetector = new FinalNodesDetector();

		generateAdvertisementPacket(finalNodesDetector, "A");
		generateAdvertisementPacket(finalNodesDetector, "B", "A");
		generateAdvertisementPacket(finalNodesDetector, "C", "A", "B");
		finalNodesDetector.consolidate();

		final Set<String> results = finalNodesDetector.getFinalNodesFor("A" + atHostId);
		assertEquals(1, results.size());
		assertTrue(results.contains("C" + atHostId));
	}

	@Test
	public void cycleDependencyTest() {
		// A
		// B = f(A)
		// C = f(A)
		// D = f(B, C)
		DependencyGraph.instance.clear();
		final FinalNodesDetector finalNodesDetector = new FinalNodesDetector();

		generateAdvertisementPacket(finalNodesDetector, "A");
		generateAdvertisementPacket(finalNodesDetector, "B", "A");
		generateAdvertisementPacket(finalNodesDetector, "C", "A");
		generateAdvertisementPacket(finalNodesDetector, "D", "B", "C");
		finalNodesDetector.consolidate();

		final Set<String> results = finalNodesDetector.getFinalNodesFor("A" + atHostId);
		assertEquals(1, results.size());
		assertTrue(results.contains("D" + atHostId));
	}

	private final void generateAdvertisementPacket(FinalNodesDetector finalNodesDetector, String name,
			String... subsNames) {
		final Advertisement adv = new Advertisement(hostId, name);
		final Set<Subscription<?>> subs = new HashSet<>();
		for (final String subName : subsNames) {
			final Subscription<?> sub = new Subscription<>(hostId, subName);
			subs.add(sub);
		}
		if (subs.isEmpty()) {
			DependencyGraph.instance.processAdv(adv);
		} else {
			DependencyGraph.instance.processAdv(adv, subs);
		}
	}

}
