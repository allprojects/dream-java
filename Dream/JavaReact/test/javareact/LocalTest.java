package javareact;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import javareact.common.types.observable.ObservableInteger;
import javareact.common.types.observable.ObservableString;
import javareact.common.types.reactive.ReactiveFactory;
import javareact.common.types.reactive.ReactiveInteger;
import javareact.common.types.reactive.ReactiveString;
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
    ObservableInteger obInt = new ObservableInteger("obInt", 1);
    ObservableString obString1 = new ObservableString("obString1", "");
    ObservableString obString2 = new ObservableString("obString2", "");

    ReactiveInteger reactInt = ReactiveFactory.getInteger("10-2+((obInt.get()*2)+ obInt.get())/2", 10, "reactInt");
    ReactiveString reactString = ReactiveFactory.getString("((obString1.get()) + obString2.get())", "", "reactString");

    ReactiveInteger reactInt2 = ReactiveFactory.getInteger("reactInt.get()*2", 10, "reactInt2");

    ObservableInteger obIntStart = new ObservableInteger("obIntStart", 1);
    ReactiveInteger reactInterm1 = ReactiveFactory.getInteger("obIntStart.get()*2", 10, "reactInterm1");
    ReactiveInteger reactInterm2 = ReactiveFactory.getInteger("obIntStart.get()*2", 10, "reactInterm2");
    ReactiveInteger reactFinal = ReactiveFactory.getInteger("reactInterm1.get() + reactInterm2.get()", 10, "reactFinal");
    ReactiveInteger reactFinal2 = ReactiveFactory.getInteger("reactInterm1.get() + obIntStart.get()", 10, "reactFinal2");

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

    assertEquals(reactInt.get(), 158);
    assertEquals(reactString.get(), "Hello World!");
    assertEquals(reactInt2.get(), 316);

    assertEquals(reactInterm1.get(), 200);
    assertEquals(reactInterm2.get(), 200);
    assertEquals(reactFinal.get(), 400);
    assertEquals(reactFinal2.get(), 300);
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
