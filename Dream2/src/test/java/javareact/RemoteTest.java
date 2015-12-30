package javareact;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import javareact.common.Consts;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;

public class RemoteTest {

  @Test
  public void remoteTest() {
    Consts.hostName = "abc";

    final RemoteVar<Integer> a = new RemoteVar<Integer>("a@def");
    final Signal<Integer> signal = new Signal<>("signal", () -> a.get() * 2, a);

    signal.addReactiveChangeListener(val -> assertTrue(val == a.get() * 2));
  }
}
