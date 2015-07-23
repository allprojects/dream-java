package javareact;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javareact.common.Consts;
import javareact.common.types.IntegerProxy;
import javareact.common.types.ListProxy;
import javareact.common.types.ReactiveChangeListener;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;
import javareact.common.types.StringProxy;
import javareact.server.ServerLauncher;
import javareact.token_service.TokenServiceLauncher;

import org.junit.Test;

public class RemoteTest implements ReactiveChangeListener<Integer> {
	private boolean serverStarted = false;
	private boolean tokenServiceStarted = false;

	@Test
	public void remoteTest1() {
		startServerIfNeeded();
		startTokenServiceIfNeeded();

		Consts.hostName = "abc";

		new Thread(new RemoteObservable()).start();

		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final RemoteVar<Integer> a = new RemoteVar<>("def", "a");
		final RemoteVar<String> someString = new RemoteVar<>("def", "someString");
		final RemoteVar<List<Integer>> intList = new RemoteVar<>("def", "intList");

		Signal<Integer> signal = new Signal<>("signal", () -> {
			if (a.get() == null)
				return null;
			return a.get() * 2;
		}, a);

		Signal<Integer> stringAndListLength = new Signal<>(
				"stringAndListLength", () -> {
					if (someString.get() == null)
						return null;
					if (intList.get() == null)
						return null;
					return someString.get().length() + intList.get().size();
				}, someString, intList);

		Signal<Integer> lastInList = new Signal<>("lastInList", () -> {
			if (intList.get() == null)
				return null;
			if (intList.get().size() == 0)
				return null;
			return intList.get().get(intList.get().size() - 1);
		}, intList);

		lastInList.addReactiveChangeListener(this);
		assertEquals(Integer.valueOf(2), signal.get());
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

	@Override
	public void notifyReactiveChanged(Integer newValue) {
		System.out.println(newValue);
	}

}
