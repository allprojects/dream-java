package javareact;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import javareact.common.types.Var;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;
import javareact.server.ServerLauncher;
import javareact.token_service.TokenServiceLauncher;

import org.junit.Test;

public class LocalTest {
	private boolean serverStarted = false;
	private boolean tokenServiceStarted = false;

	@Test
	public void localTest1() {
		startServerIfNeeded();
		startTokenServiceIfNeeded();

		Var<Integer> obInt = new Var<>("obInt", Integer.valueOf(1));
		Var<String> obString1 = new Var<>("obString1", "");
		Var<String> obString2 = new Var<>("obString2", "");

		Signal<Integer> reactInt = new Signal<Integer>("reactInt", () -> {
			if (obInt.get() == null)
				return null;
			return 10 - 2 + ((obInt.get() * 2) + obInt.get()) / 2;
		}, obInt);

		Signal<String> reactString = new Signal<String>("reactString", () -> obString1.get() + obString2.get(),
				obString1, obString2);

		Signal<Integer> reactInt2 = new Signal<Integer>("reactInt2", () -> {
			if (reactInt.get() == null)
				return null;
			return reactInt.get() * 2;
		}, reactInt);

		Var<Integer> obIntStart = new Var<>("obIntStart", Integer.valueOf(1));

		Signal<Integer> reactInterm1 = new Signal<Integer>("reactInterm1", () -> {
			System.out.println("reactInterm1: " + obIntStart.get());
			if (obIntStart.get() == null)
				return null;
			return obIntStart.get() * 2;
		}, obIntStart);

		Signal<Integer> reactInterm2 = new Signal<Integer>("reactInterm2", () -> {
			System.out.println("reactInterm2: " + reactInterm1.get());
			if (reactInterm1.get() == null)
				return null;
			return reactInterm1.get() * 2;
		}, reactInterm1);

		Signal<Integer> reactFinal = new Signal<Integer>("reactFinal", () -> {
			System.out.println("reactFinal: " + reactInterm1.get() + " " + reactInterm2.get());
			if (reactInterm1.get() == null || reactInterm2.get() == null)
				return null;
			return reactInterm1.get() + reactInterm2.get();
		}, reactInterm1, reactInterm2);

		Signal<Integer> reactFinal2 = new Signal<Integer>("reactFinal2", () -> {
			if (reactInterm1.get() == null || obIntStart.get() == null)
				return null;
			return reactInterm1.get() + obIntStart.get();
		}, reactInterm1, obIntStart);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		obInt.set(100);
		obString1.set("Hello ");
		obString2.set("World!");
		obIntStart.set(100);

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		assertEquals(reactInt.get(), Integer.valueOf(158));
		assertEquals(reactString.get(), "Hello World!");
		assertEquals(reactInt2.get(), Integer.valueOf(316));
		assertEquals(reactInterm1.get(), Integer.valueOf(200));
		assertEquals(reactInterm2.get(), Integer.valueOf(400));
		assertEquals(reactFinal.get(), Integer.valueOf(600));
		assertEquals(reactFinal2.get(), Integer.valueOf(300));
	}

	private final void startServerIfNeeded() {
		if (!serverStarted) {
			ServerLauncher.start();
			serverStarted = true;
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private final void startTokenServiceIfNeeded() {
		if (!tokenServiceStarted) {
			String serverAddress = "reds-tcp:localhost:9000";
			Set<String> addresses = new HashSet<String>();
			addresses.add(serverAddress);
			TokenServiceLauncher.start(addresses);
			tokenServiceStarted = true;
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
