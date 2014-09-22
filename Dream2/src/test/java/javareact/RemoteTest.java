package javareact;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javareact.common.Consts;
import javareact.common.types.IntegerProxy;
import javareact.common.types.ListProxy;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;
import javareact.common.types.StringProxy;
import javareact.server.ServerLauncher;
import javareact.token_service.TokenServiceLauncher;

public class RemoteTest {
	private boolean serverStarted = false;
	private boolean tokenServiceStarted = false;
	
	public static void main(String [] args) {
		
		Consts.hostName = "abc";
		
		Runtime rt = Runtime.getRuntime();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final RemoteVar<Integer> a = new IntegerProxy("def", "a");
		final RemoteVar<String> someString = new StringProxy("def",
				"someString");
		final RemoteVar<List<Integer>> intList = new ListProxy<Integer>(
				"def", "intList");

		Signal<Integer> signal = new Signal<Integer>("signal", a) {
			@Override
			public Integer evaluate() {
				if (a.get() == null)
					return null;
				return a.get() * 2;
			}
		};

		Signal<Integer> stringandlistlength = new Signal<Integer>(
				"stringandlistlength", someString, intList) {
			@Override
			public Integer evaluate() {
				if (someString.get() == null)
					return null;
				if (intList.get() == null)
					return null;
				return someString.get().length() + intList.get().size();
			}
		};

		Signal<Integer> lastInList = new Signal<Integer>("lastInList", intList) {
			@Override
			public Integer evaluate() {
				if (intList.get() == null)
					return null;
				if (intList.get().size() == 0)
					return null;
				return intList.get().get(intList.get().size() - 1);
			}
		};
		
		//assertEquals(signal.get(), Integer.valueOf(2));
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
