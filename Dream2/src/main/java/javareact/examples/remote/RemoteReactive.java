package javareact.examples.remote;

import javareact.common.Consts;
import javareact.common.types.IntegerProxy;
import javareact.common.types.ListProxy;
import javareact.common.types.ReactiveInteger;
import javareact.common.types.ReactiveString;
import javareact.common.types.StringProxy;

public class RemoteReactive {

  public static void main(String args[]) {
    Consts.hostName = "Reactive";

    final IntegerProxy obIntProxy = new IntegerProxy("Remote", "obInt");
    final StringProxy obString1Proxy = new StringProxy("Remote", "obString1");
    final StringProxy obString2Proxy = new StringProxy("Remote", "obString2");
    final ListProxy<Integer> obListProxy = new ListProxy<Integer>("Remote", "obList");

    ReactiveInteger react1 = new ReactiveInteger("react1", obIntProxy, obString1Proxy) {
      @Override
      public Integer evaluate() {
        return obIntProxy.get() + obString1Proxy.get().length();
      }
    };

    ReactiveInteger react2 = new ReactiveInteger("react2", obIntProxy) {
      @Override
      public Integer evaluate() {
        return obIntProxy.get();
      }
    };

    ReactiveString react3 = new ReactiveString("react3", obString1Proxy, obString2Proxy) {
      @Override
      public String evaluate() {
        return obString1Proxy.get() + obString2Proxy.get();
      }
    };

    ReactiveInteger react4 = new ReactiveInteger("react4", obString1Proxy, obListProxy) {
      @Override
      public Integer evaluate() {
        return obString1Proxy.length() + obListProxy.size();
      }
    };

    while (true) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("React1: " + react1.get());
      System.out.println("React2: " + react2.get());
      System.out.println("React3: " + react3.get());
      System.out.println("React4: " + react4.get());

    }

  }
}
