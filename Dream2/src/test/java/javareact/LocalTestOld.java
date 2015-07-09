package javareact;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import javareact.common.types.IntegerProxy;
import javareact.common.types.ObservableInteger;
import javareact.common.types.ObservableString;
import javareact.common.types.ReactiveInteger;
import javareact.common.types.ReactiveString;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;
import javareact.common.types.Var;
import javareact.server.ServerLauncher;
import javareact.token_service.TokenServiceLauncher;

import org.junit.Test;

public class LocalTestOld {
	private boolean serverStarted = false;
	private boolean tokenServiceStarted = false;

	@Test
	public void localTest1() {
		startServerIfNeeded();
		startTokenServiceIfNeeded();

		ObservableInteger obInt = new ObservableInteger("obInt", Integer.valueOf(1));
		ObservableString obString1 = new ObservableString("obString1", "");
		ObservableString obString2 = new ObservableString("obString2", "");

		ReactiveInteger reactInt = new ReactiveInteger("reactInt", obInt.getProxy()) {
			@Override
			public Integer evaluate() {
				if(obInt.get() == null) return null;
				return 10 - 2 + ((obInt.get() * 2) + obInt.get()) / 2;
			}
		};

		ReactiveString reactString = new ReactiveString("reactString", obString1.getProxy(), obString2.getProxy()) {
			@Override
			public String evaluate() {
				return obString1.get() + obString2.get();
			}
		};

		ReactiveInteger reactInt2 = new ReactiveInteger("reactInt2", reactInt.getProxy()) {
			@Override
			public Integer evaluate() {
				if(reactInt.get() == null) return null;
				return reactInt.get() * 2;
			}
		};

		ObservableInteger obIntStart = new ObservableInteger("obIntStart", Integer.valueOf(1));
		IntegerProxy obIntStartR = obIntStart.getProxy();
		ReactiveInteger reactInterm1 = new ReactiveInteger("reactInterm1", obIntStartR) {
			@Override
			public Integer evaluate() {
				System.out.println("reactInterm1: " + obIntStartR.get());
				if(obIntStartR.get() == null) return null;
				return obIntStartR.get() * 2;
			}
		};

		IntegerProxy reactInterm1R = reactInterm1.getProxy();

		ReactiveInteger reactInterm2 = new ReactiveInteger("reactInterm2", reactInterm1R) {
			@Override
			public Integer evaluate() {
				System.out.println("reactInterm2: " + reactInterm1R.get());
				if(reactInterm1R.get() == null) return null;
				return reactInterm1R.get() * 2;
			}
		};

		IntegerProxy reactInterm2R = reactInterm2.getProxy();

		ReactiveInteger reactFinal = new ReactiveInteger("reactFinal", reactInterm1R, reactInterm2R) {
			@Override
			public Integer evaluate() {
				System.out.println("reactFinal: " + reactInterm1.get() + " " + reactInterm2.get());
				if(reactInterm1.get() == null || reactInterm2.get() == null) return null;
				return reactInterm1.get() + reactInterm2.get();
			}
		};

		ReactiveInteger reactFinal2 = new ReactiveInteger("reactFinal2", reactInterm1.getProxy(), obIntStart.getProxy()) {
			@Override
			public Integer evaluate() {
				if(reactInterm1.get() == null || obIntStart.get() == null) return null;
				return reactInterm1.get() + obIntStart.get();
			}
		};

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