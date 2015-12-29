package javareact.examples.local;

import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;
import javareact.common.types.Var;

public class LocalExample {

  public static void main(String args[]) {
    final Var<Integer> obInt = new Var<>("obInt", 1);
    final Var<Double> obDouble = new Var<>("obDouble", 1.0);
    final Var<Boolean> obBool = new Var<>("obBool", false);
    final Var<String> obString1 = new Var<>("obString1", "");
    final Var<String> obString2 = new Var<>("obString2", "");

    final RemoteVar<Integer> obIntProxy = obInt.getProxy();
    final RemoteVar<Double> obDoubleProxy = obDouble.getProxy();
    final RemoteVar<Boolean> obBoolProxy = obBool.getProxy();
    final RemoteVar<String> obString1Proxy = obString1.getProxy();
    final RemoteVar<String> obString2Proxy = obString2.getProxy();

    final Signal<Integer> reactInt = new Signal<Integer>("reactInt", () -> 10 - 2 + (obIntProxy.get() * 2 + obIntProxy.get()) / 2, obIntProxy);

    final Signal<Double> reactDouble = new Signal<Double>("reactDouble", () -> obDoubleProxy.get() + obDoubleProxy.get() * 2, obDoubleProxy);

    final Signal<String> reactString = new Signal<String>("reactString", () -> obString1Proxy.get() + obString2Proxy.get(), obString1Proxy, obString2Proxy);

    final Signal<Boolean> reactBool = new Signal<Boolean>("reactBool", () -> !obBoolProxy.get(), obBoolProxy);

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    obInt.set(100);
    obDouble.set(1.6);
    obBool.set(true);
    obString1.set("Hello ");
    obString2.set("World!");

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("reactInt: " + reactInt.get() + " (correct value: 158)");
    System.out.println("reactDouble: " + reactDouble.get() + " (correct value: 4.8)");
    System.out.println("reactBool: " + reactBool.get() + " (correct value: false)");
    System.out.println("reactString: " + reactString.get() + " (correct value: Hello World!)");

  }
}
