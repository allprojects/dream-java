package javareact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import javareact.common.Consts;
import javareact.common.types.ReactiveChangeListener;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;
import javareact.common.types.Var;
import javareact.server.ServerLauncher;
import javareact.token_service.TokenServiceLauncher;

import org.junit.Test;

public class RemoteTest implements ReactiveChangeListener<Integer> {
	private boolean serverStarted = false;
	private boolean tokenServiceStarted = false;

	private RemoteVar<Integer> a;
	private Signal<Integer> signal;

	@Test
	public void remoteTest() {
		Consts.hostName = "abc";

		a = new RemoteVar<>("a@def");

		signal = new Signal<>("signal", () -> {
			if (a.get() == null)
				return null;
			return a.get() * 2;
		}, a);

		signal.addReactiveChangeListener(this);

		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(1000);
			}
			catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void notifyReactiveChanged(Integer newValue) {
		assertTrue( signal.get().equals(a.get() * 2));
	}

}
