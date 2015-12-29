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

    obDouble1.set(10.0);
    obDouble2.set(20.0);

    obDouble1.set(20.0);
    obDouble2.set(30.0);

    obDouble1.set(40.0);
    obDouble2.set(50.0);
  }

  @Override
  public void notifyReactiveChanged(Double changedReactive) {
    System.out.println(changedReactive);
  }

}
