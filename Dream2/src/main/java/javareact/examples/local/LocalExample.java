package javareact.examples.local;

import javareact.common.types.Signal;
import javareact.common.types.Var;

public class LocalExample {

  public static void main(String args[]) {
    final Var<Integer> varInt = new Var<>("obInt", 1);
    final Var<Double> varDouble = new Var<>("obDouble", 1.0);
    final Var<Boolean> varBool = new Var<>("obBool", false);
    final Var<String> varString1 = new Var<>("obString1", "");
    final Var<String> varString2 = new Var<>("obString2", "");

    final Signal<Integer> signalInt = new Signal<Integer>("reactInt", () -> 10 - 2 + (varInt.get() * 2 + varInt.get()) / 2, varInt);
    final Signal<Double> signalDouble = new Signal<Double>("reactDouble", () -> varDouble.get() + varDouble.get() * 2, varDouble);
    final Signal<String> signalString = new Signal<String>("reactString", () -> varString1.get() + varString2.get(), varString1, varString2);
    final Signal<Boolean> signalBool = new Signal<Boolean>("reactBool", () -> !varBool.get(), varBool);

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    varInt.set(100);
    varDouble.set(1.6);
    varBool.set(true);
    varString1.set("Hello ");
    varString2.set("World!");

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("signalInt: " + signalInt.get() + " (correct value: 158)");
    System.out.println("signalDouble: " + signalDouble.get() + " (correct value: 4.8)");
    System.out.println("signalBool: " + signalBool.get() + " (correct value: false)");
    System.out.println("signalString: " + signalString.get() + " (correct value: Hello World!)");

  }
}
