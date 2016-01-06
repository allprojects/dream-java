package javareact.examples.local;

import javareact.common.types.Signal;
import javareact.common.types.Var;

public class ExampleGlitch {

  public static void main(String args[]) {
    final ExampleGlitch example = new ExampleGlitch();
    example.launch();
  }

  public void launch() {
    final Var<Double> var1 = new Var<>("var1", 1.0);

    final Signal<Double> signal1 = new Signal<>("signal1", () -> var1.get() * 2, var1);
    final Signal<Double> signal2 = new Signal<>("signal2", () -> var1.get() * 3, var1);

    final Signal<Double> finalResult = new Signal<>("sub", () -> signal1.get() + signal2.get(), signal1, signal2);
    finalResult.addValueChangeListener(System.out::println);

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    final double v1 = 10;
    final double v2 = 20;
    final double v3 = 30;

    System.out.println("Expected values (with glitch freedom): ");
    System.out.println("1) " + (v1 * 2 + v1 * 3));
    System.out.println("2) " + (v2 * 2 + v2 * 3));
    System.out.println("3) " + (v3 * 2 + v3 * 3));

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    var1.set(v1);
    var1.set(v2);
    var1.set(v3);

  }

}
