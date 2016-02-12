package dream;

import org.junit.BeforeClass;
import org.junit.Test;

import dream.client.RemoteVar;
import dream.client.Signal;
import dream.locking.LockManagerLauncher;
import dream.server.ServerLauncher;

public class UnknownVar {

	private static boolean serverStarted = false;
	private final static boolean lockManagerStarted = false;

	@BeforeClass
	public static void setup() {
		startServerIfNeeded();
		startTokenServiceIfNeeded();
	}

	@Test
	public void testUnknownVar() {
		RemoteVar<String> listener = new RemoteVar<String>("Server", "Variable");
		Signal<String> listenerSignal = new Signal<String>("listener", () -> {
			if (listener.get() == null)
				return "";
			else
				return listener.get();
		} , listener);
		listenerSignal.change().addHandler((o, n) -> System.out.println(o + "->" + n));
	}

	private final static void startServerIfNeeded() {
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

	private final static void startTokenServiceIfNeeded() {
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