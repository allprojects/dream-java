package javareact.examples.local;

import javareact.common.types.Signal;
import javareact.common.types.Var;

public class LocalExample2 {

  public static void main(String args[]) {
    final LocalExample2 example = new LocalExample2();
    example.launch();
  }

  public void launch() {
    final Var<Double> var1 = new Var<>("var1", 1.0);
    final Var<Double> var2 = new Var<>("var2", 1.0);

    final Signal<Double> signal1 = new Signal<Double>("signal1", () -> var1.get() * var2.get(), var1, var2);
    final Signal<Double> signal2 = new Signal<Double>("signal2", () -> var1.get() / var2.get(), var1, var2);

    final Signal<Double> finalResult = new Signal<Double>("sub", () -> signal1.get() - signal2.get(), signal1, signal2);
    finalResult.addReactiveChangeListener(System.out::println);

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    final double var2V0 = 1;

    final double var1V1 = 10;
    final double var2V1 = 20;

    final double var1V2 = 20;
    final double var2V2 = 30;

    final double var1V3 = 40;
    final double var2V3 = 50;

    System.out.println("Expected values: ");
    System.out.println("1) " + (var1V1 * var2V0 - var1V1 / var2V0));
    System.out.println("2) " + (var1V1 * var2V1 - var1V1 / var2V1));
    System.out.println("3) " + (var1V2 * var2V1 - var1V2 / var2V1));
    System.out.println("4) " + (var1V2 * var2V2 - var1V2 / var2V2));
    System.out.println("5) " + (var1V3 * var2V2 - var1V3 / var2V2));
    System.out.println("6) " + (var1V3 * var2V3 - var1V3 / var2V3));

    var1.set(var1V1);
    var2.set(var2V1);

    var1.set(var1V2);
    var2.set(var2V2);

    var1.set(var1V3);
    var2.set(var2V3);
  }

}
