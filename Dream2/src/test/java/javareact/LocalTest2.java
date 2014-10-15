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

public class LocalTest2 {
  private boolean serverStarted = false;
  private boolean tokenServiceStarted = false;

  @Test
  public void localTest2() {
    startServerIfNeeded();
    startTokenServiceIfNeeded();

    Var<Integer> obIntStart = new Var<>("obIntStart", Integer.valueOf(1));
    RemoteVar<Integer> obIntStartR = obIntStart.getProxy();
    Signal<Integer> reactInterm1 = new Signal<Integer>("reactInterm1",
    		() -> {
    			System.out.println("reactInterm1: " + obIntStartR.get());
    			if(obIntStartR.get() == null) return null;
    			return obIntStartR.get() * 2;
    		},
    		obIntStartR);
    
    RemoteVar<Integer> reactInterm1R = reactInterm1.getProxy();

    Signal<Integer> reactInterm2 = new Signal<Integer>("reactInterm2", 
    		() -> {
    			System.out.println("reactInterm2: " + reactInterm1R.get());
    			if(reactInterm1R.get() == null) return null;
    			return reactInterm1R.get() * 2;
    		},
    		reactInterm1R);
    
    RemoteVar<Integer> reactInterm2R = reactInterm2.getProxy();

    Signal<Integer> reactFinal = new Signal<Integer>("reactFinal",
    		() -> {
    			System.out.println("reactFinal: " + reactInterm1.get() + " " + reactInterm2.get());
    			if(reactInterm1.get() == null || reactInterm2.get() == null) return null;
    			return reactInterm1.get() + reactInterm2.get();
    		},
    		reactInterm1R, reactInterm2R);
    
    /*Signal<Integer> reactFinal2 = new Signal<Integer>("reactFinal2",
    		() -> {
    			if(reactInterm1.get() == null || obIntStart.get() == null) return null;
    		  return reactInterm1.get() + obIntStart.get();
    		},
    		reactInterm1, obIntStart);*/

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    obIntStart.set(100);

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    assertEquals(reactInterm1.get(), Integer.valueOf(200));
    assertEquals(reactInterm2.get(), Integer.valueOf(400));
    assertEquals(reactFinal.get(), Integer.valueOf(600));
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
