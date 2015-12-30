package javareact;

import java.util.HashSet;
import java.util.Set;

import javareact.common.Consts;
import javareact.common.types.Var;
import javareact.server.ServerLauncher;
import javareact.token_service.TokenServiceLauncher;

public class RemoteTestSupport {
  private boolean serverStarted = false;
  private boolean tokenServiceStarted = false;

  public static void main(String[] args) {
    new RemoteTestSupport().start();
  }

  public void start() {
    Consts.hostName = "def";

    startServerIfNeeded();
    startTokenServiceIfNeeded();

    final Var<Integer> a = new Var<Integer>("a", 1);

    for (int i = 0; i < 20; i++) {
      a.set(a.get() + 1);
      System.out.println("a: " + a.get());
      try {
        Thread.sleep(1000);
      } catch (final InterruptedException e) {
        e.printStackTrace();
      }
    }
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