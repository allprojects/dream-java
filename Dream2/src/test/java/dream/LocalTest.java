package dream;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import dream.client.Signal;
import dream.client.Var;
import dream.common.utils.DependencyGraph;
import dream.locking.LockManagerLauncher;
import dream.server.ServerLauncher;

public class LocalTest {
	private boolean serverStarted = false;
	private final boolean lockManagerStarted = false;

	@Test
	public void localTest1() {
		startServerIfNeeded();
		startTokenServiceIfNeeded();

		DependencyGraph.instance.clear();

		final Var<Integer> varInt = new Var<>("varInt", Integer.valueOf(1));
		final Var<String> varString1 = new Var<>("varString1", "");
		final Var<String> varString2 = new Var<>("varString2", "");

		final Signal<Integer> signalInt = new Signal<Integer>("signalInt",
				() -> 10 - 2 + (varInt.get() * 2 + varInt.get()) / 2, varInt);
		final Signal<String> signalString = new Signal<String>("signalString",
				() -> varString1.get() + varString2.get(), varString1, varString2);
		final Signal<Integer> signalInt2 = new Signal<Integer>("signalInt2", () -> signalInt.get() * 2, signalInt);

		final Var<Integer> varStart = new Var<>("varStart", Integer.valueOf(1));
		final Signal<Integer> signalMid1 = new Signal<Integer>("signalMid1", () -> varStart.get() * 2, varStart);
		final Signal<Integer> signalMid2 = new Signal<Integer>("signalMid2", () -> signalMid1.get() * 2, signalMid1);
		final Signal<Integer> signalFinal = new Signal<Integer>("signalFinal",
				() -> signalMid1.get() + signalMid2.get(), signalMid1, signalMid2);
		final Signal<Integer> signalFinal2 = new Signal<Integer>("signalFinal2",
				() -> signalMid1.get() + varStart.get(), signalMid1, varStart);

		try {
			Thread.sleep(500);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		varInt.set(100);
		varString1.set("Hello ");
		varString2.set("World!");
		varStart.set(100);

		try {
			Thread.sleep(500);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals(signalInt.get(), Integer.valueOf(158));
		assertEquals(signalString.get(), "Hello World!");
		assertEquals(signalInt2.get(), Integer.valueOf(316));
		assertEquals(signalMid1.get(), Integer.valueOf(200));
		assertEquals(signalMid2.get(), Integer.valueOf(400));
		assertEquals(signalFinal.get(), Integer.valueOf(600));
		assertEquals(signalFinal2.get(), Integer.valueOf(300));
	}

	private final void startServerIfNeeded() {
		if (!serverStarted) {
			ServerLauncher.start();
			serverStarted = true;
		}
		try {
			Thread.sleep(500);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}

	private final void startTokenServiceIfNeeded() {
		if (!lockManagerStarted) {
			LockManagerLauncher.start();
		}
		try {
			Thread.sleep(500);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
	}
}
