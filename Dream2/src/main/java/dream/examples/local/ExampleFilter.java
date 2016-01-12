package dream.examples.local;

import dream.common.datatypes.Signal;
import dream.common.datatypes.Var;

public class ExampleFilter {
  public static void main(String args[]) {

    final Var<Integer> varInt = new Var<>("varInt", 1);
    final Signal<Integer> signalInt = new Signal<>("signalInt", () -> varInt.get() + 1, varInt.filter(val -> val > 10));
    final Signal<Integer> signalInt2 = new Signal<>("signalInt2", () -> signalInt.get() + 1, signalInt.filter(val -> val > 20));

    signalInt.addValueChangeListener(val -> System.out.println("SignalInt: " + val));
    signalInt2.addValueChangeListener(val -> System.out.println("SignalInt2: " + val));

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    varInt.set(1);

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    varInt.set(2);

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    varInt.set(11);

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    varInt.set(20);

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    varInt.set(10);

  }
}
