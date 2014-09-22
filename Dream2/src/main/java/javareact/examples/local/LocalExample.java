package javareact.examples.local;

import javareact.common.types.Var;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;

public class LocalExample {

  public static void main(String args[]) {
    Var<Integer> obInt = new Var<>("obInt", 1);
    Var<Double> obDouble = new Var<>("obDouble", 1.0);
    Var<Boolean> obBool = new Var<>("obBool", false);
    Var<String> obString1 = new Var<>("obString1", "");
    Var<String> obString2 = new Var<>("obString2", "");

    final RemoteVar<Integer> obIntProxy = obInt.getProxy();
    final RemoteVar<Double> obDoubleProxy = obDouble.getProxy();
    final RemoteVar<Boolean> obBoolProxy = obBool.getProxy();
    final RemoteVar<String> obString1Proxy = obString1.getProxy();
    final RemoteVar<String> obString2Proxy = obString2.getProxy();

    Signal<Integer> reactInt = new Signal<Integer>("reactInt",
    		() -> 10 - 2 + ((obIntProxy.get() * 2) + obIntProxy.get()) / 2,
    		obIntProxy);

    Signal<Double> reactDouble = new Signal<Double>("reactDouble", 
    		() -> obDoubleProxy.get() + obDoubleProxy.get() * 2,
    		obDoubleProxy);

    Signal<String> reactString = new Signal<String>("reactString",
    		() -> obString1Proxy.get() + obString2Proxy.get(),
    		obString1Proxy, obString2Proxy);

    Signal<Boolean> reactBool = new Signal<Boolean>("reactBool", 
    		() -> !obBoolProxy.get(), 
    		obBoolProxy);

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    obInt.set(100);
    obDouble.set(1.6);
    obBool.set(true);
    obString1.set("Hello ");
    obString2.set("World!");

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("reactInt: " + reactInt.get() + " (correct value: 158)");
    System.out.println("reactDouble: " + reactDouble.get() + " (correct value: 4.8)");
    System.out.println("reactBool: " + reactBool.get() + " (correct value: false)");
    System.out.println("reactString: " + reactString.get() + " (correct value: Hello World!)");

  }
}
