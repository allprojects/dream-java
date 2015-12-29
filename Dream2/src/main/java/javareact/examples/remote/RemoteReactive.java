package javareact.examples.remote;

import javareact.common.Consts;
import javareact.common.types.IntegerProxy;
import javareact.common.types.ListProxy;
import javareact.common.types.Signal;
import javareact.common.types.StringProxy;

public class RemoteReactive {

  public static void main(String args[]) {
    Consts.hostName = "Reactive";

    final IntegerProxy obIntProxy = new IntegerProxy("Remote", "obInt");
    final StringProxy obString1Proxy = new StringProxy("Remote", "obString1");
    final StringProxy obString2Proxy = new StringProxy("Remote", "obString2");
    final ListProxy<Integer> obListProxy = new ListProxy<Integer>("Remote", "obList");

    final Signal<Integer> react1 = new Signal<Integer>("react1", () -> obIntProxy.get() + obString1Proxy.get().length(), obIntProxy, obString1Proxy);
    final Signal<Integer> react2 = new Signal<Integer>("react2", () -> obIntProxy.get(), obIntProxy);
    final Signal<String> react3 = new Signal<String>("react3", () -> obString1Proxy.get() + obString2Proxy.get(), obString1Proxy, obString2Proxy);
    final Signal<Integer> react4 = new Signal<Integer>("react4", () -> obString1Proxy.length() + obListProxy.size(), obString1Proxy, obListProxy);

    while (true) {
      try {
        Thread.sleep(1000);
      } catch (final InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("React1: " + react1.get());
      System.out.println("React2: " + react2.get());
      System.out.println("React3: " + react3.get());
      System.out.println("React4: " + react4.get());

    }

  }
}
