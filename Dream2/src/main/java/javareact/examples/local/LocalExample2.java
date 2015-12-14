package javareact.examples.local;

import javareact.common.types.RemoteVar;
import javareact.common.types.Var;
import javareact.common.types.Signal;

public class LocalExample2 {

  public static void main(String args[]) {
    LocalExample2 example = new LocalExample2();
    example.launch();
  }

  public void launch() {
    Var<Double> obDouble1 = new Var<>("obDouble1", 1.0);
    Var<Double> obDouble2 = new Var<>("obDouble2", 1.0);

    final RemoteVar<Double> obDouble1Proxy = obDouble1.getProxy();
    final RemoteVar<Double> obDouble2Proxy = obDouble2.getProxy();

    Signal<Double> reactDouble1 = new Signal<Double>("reactDouble1",
    		() -> obDouble1Proxy.get() * obDouble2Proxy.get(),
    		obDouble1Proxy, obDouble2Proxy);

    Signal<Double> reactDouble2 = new Signal<Double>("reactDouble2",
    		() -> obDouble1Proxy.get() / obDouble2Proxy.get(),
    		obDouble1Proxy, obDouble2Proxy);

    final RemoteVar<Double> reactDouble1Proxy = reactDouble1.getProxy();
    final RemoteVar<Double> reactDouble2Proxy = reactDouble2.getProxy();

    new Signal<Double>("sub",
    		() -> reactDouble1Proxy.get() - reactDouble2Proxy.get(),
    		reactDouble1Proxy, reactDouble2Proxy).change().addHandler(
              (oldValue, newValue) -> System.out.println(newValue));

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    obDouble1.set(10.0);
    obDouble2.set(20.0);

    obDouble1.set(20.0);
    obDouble2.set(30.0);

    obDouble1.set(40.0);
    obDouble2.set(50.0);
  }
}
