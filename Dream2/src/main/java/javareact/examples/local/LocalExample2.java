package javareact.examples.local;

import javareact.common.types.ReactiveChangeListener;
import javareact.common.types.Signal;
import javareact.common.types.Var;

public class LocalExample2 implements ReactiveChangeListener<Double> {

  public static void main(String args[]) {
    final LocalExample2 example = new LocalExample2();
    example.launch();
  }

  public void launch() {
    final Var<Double> obDouble1 = new Var<>("obDouble1", 1.0);
    final Var<Double> obDouble2 = new Var<>("obDouble2", 1.0);

    final Signal<Double> reactDouble1 = new Signal<Double>("reactDouble1", () -> obDouble1.get() * obDouble2.get(), obDouble1, obDouble2);
    final Signal<Double> reactDouble2 = new Signal<Double>("reactDouble2", () -> obDouble1.get() / obDouble2.get(), obDouble1, obDouble2);

    new Signal<Double>("sub", () -> reactDouble1.get() - reactDouble2.get(), reactDouble1, reactDouble2).addReactiveChangeListener(this);

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    final double obDouble2V0 = 1;

    final double obDouble1V1 = 10;
    final double obDouble2V1 = 20;

    final double obDouble1V2 = 20;
    final double obDouble2V2 = 30;

    final double obDouble1V3 = 40;
    final double obDouble2V3 = 50;

    System.out.println("Expected values: ");
    System.out.println("1) " + (obDouble1V1 * obDouble2V0 - obDouble1V1 / obDouble2V0));
    System.out.println("2) " + (obDouble1V1 * obDouble2V1 - obDouble1V1 / obDouble2V1));
    System.out.println("3) " + (obDouble1V2 * obDouble2V1 - obDouble1V2 / obDouble2V1));
    System.out.println("4) " + (obDouble1V2 * obDouble2V2 - obDouble1V2 / obDouble2V2));
    System.out.println("5) " + (obDouble1V3 * obDouble2V2 - obDouble1V3 / obDouble2V2));
    System.out.println("6) " + (obDouble1V3 * obDouble2V3 - obDouble1V3 / obDouble2V3));

    obDouble1.set(obDouble1V1);
    obDouble2.set(obDouble2V1);

    obDouble1.set(obDouble1V2);
    obDouble2.set(obDouble2V2);

    obDouble1.set(obDouble1V3);
    obDouble2.set(obDouble2V3);
  }

  @Override
  public void notifyReactiveChanged(Double changedReactive) {
    System.out.println(changedReactive);
  }

}
