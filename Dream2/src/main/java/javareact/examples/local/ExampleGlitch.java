package javareact.examples.local;

import javareact.common.types.Signal;
import javareact.common.types.Var;

public class ExampleGlitch {

  public static void main(String args[]) {
    final ExampleGlitch example = new ExampleGlitch();
    example.launch();
  }

  public void launch() {
    final Var<Double> var = new Var<>("var", 1.0);

    final Signal<Double> mid1 = new Signal<>("mid1", () -> var.get() * 2, var);
    final Signal<Double> mid2 = new Signal<>("mid2", () -> var.get() * 3, var);

    final Signal<Double> finalResult = new Signal<>("final", () -> mid1.get() + mid2.get(), mid1, mid2);
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

    var.set(v1);
    var.set(v2);
    var.set(v3);

  }

}
