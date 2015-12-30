package javareact;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import javareact.common.types.Signal;
import javareact.common.types.Var;
import javareact.server.ServerLauncher;
import javareact.token_service.TokenServiceLauncher;

public class LocalTest2 {
  private boolean serverStarted = false;
  private boolean tokenServiceStarted = false;

  @Test
  public void localTest2() {
    startServerIfNeeded();
    startTokenServiceIfNeeded();

    final Var<Integer> obIntStart = new Var<>("obIntStart", Integer.valueOf(1));
    final Signal<Integer> reactInterm1 = new Signal<Integer>("reactInterm1", () -> {
      System.out.println("reactInterm1: " + obIntStart.get());
      if (obIntStart.get() == null) {
        return null;
      }
      return obIntStart.get() * 2;
    } , obIntStart);

    final Signal<Integer> reactInterm2 = new Signal<Integer>("reactInterm2", () -> {
      System.out.println("reactInterm2: " + reactInterm1.get());
      if (reactInterm1.get() == null) {
        return null;
      }
      return reactInterm1.get() * 2;
    } , reactInterm1);

    final Signal<Integer> reactFinal = new Signal<Integer>("reactFinal", () -> {
      System.out.println("reactFinal: " + reactInterm1.get() + " " + reactInterm2.get());
      if (reactInterm1.get() == null || reactInterm2.get() == null) {
        return null;
      }
      return reactInterm1.get() + reactInterm2.get();
    } , reactInterm1, reactInterm2);

    /*
     * Signal<Integer> reactFinal2 = new Signal<Integer>("reactFinal2", () -> {
     * if(reactInterm1.get() == null || obIntStart.get() == null) return null;
     * return reactInterm1.get() + obIntStart.get(); }, reactInterm1,
     * obIntStart);
     */

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    obIntStart.set(100);

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
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
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
  }

  private final void startTokenServiceIfNeeded() {
    if (!tokenServiceStarted) {
      final String serverAddress = "reds-tcp:localhost:9000";
      final Set<String> addresses = new HashSet<String>();
      addresses.add(serverAddress);
      TokenServiceLauncher.start(addresses);
      tokenServiceStarted = true;
    }
    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }
  }
}
