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

    final RemoteVar<Integer> obIntProxy = obInt.getProxy();
    final RemoteVar<String> obString1Proxy = obString1.getProxy();
    final RemoteVar<String> obString2Proxy = obString2.getProxy();
    
    Signal<Integer> reactInt = new Signal<Integer>("reactInt", obIntProxy) {
      @Override
      public Integer evaluate() {
        return 10 - 2 + ((obIntProxy.get() * 2) + obIntProxy.get()) / 2;
      }
    };

    Signal<String> reactString = new Signal<String>("reactString", obString1Proxy, obString2Proxy) {
      @Override
      public String evaluate() {
        return obString1Proxy.get() + obString2Proxy.get();
      }
    };

    final RemoteVar<Integer> reactIntProxy = reactInt.getProxy();

    Signal<Integer> reactInt2 = new Signal<Integer>("reactInt2", reactIntProxy) {
      @Override
      public Integer evaluate() {
        return reactIntProxy.get() * 2;
      }
    };

    Var<Integer> obIntStart = new Var<>("obIntStart", Integer.valueOf(1));
    final RemoteVar<Integer> obIntStartProxy = obIntStart.getProxy();

    Signal<Integer> reactInterm1 = new Signal<Integer>("reactInterm1", obIntStartProxy) {
      @Override
      public Integer evaluate() {
        return obIntStartProxy.get() * 2;
      }
    };

    final RemoteVar<Integer> intermProxy1 = reactInterm1.getProxy();

    Signal<Integer> reactInterm2 = new Signal<Integer>("reactInterm2", intermProxy1) {
      @Override
      public Integer evaluate() {
        return intermProxy1.get() * 2;
      }
    };

    final RemoteVar<Integer> intermProxy2 = reactInterm2.getProxy();

    Signal<Integer> reactFinal = new Signal<Integer>("reactFinal", intermProxy1, intermProxy2) {
      @Override
      public Integer evaluate() {
        return intermProxy1.get() + intermProxy2.get();
      }
    };

    Signal<Integer> reactFinal2 = new Signal<Integer>("reactFinal2", intermProxy1, obIntStartProxy) {
      @Override
      public Integer evaluate() {
        return intermProxy1.get() + obIntStartProxy.get();
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
