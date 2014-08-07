package javareact.examples.local;

import javareact.common.ConsistencyType;
import javareact.common.Consts;
import javareact.common.packets.content.Value;
import javareact.common.types.ReactiveChangeListener;
import javareact.common.types.observable.ObservableDouble;
import javareact.common.types.reactive.ReactiveDouble;
import javareact.common.types.reactive.ReactiveFactory;

public class LocalExample2 implements ReactiveChangeListener {

  public static void main(String args[]) {
    LocalExample2 example = new LocalExample2();
    example.launch();
  }

  public void launch() {
    Consts.consistencyType = ConsistencyType.GLITCH_FREE;
    Consts.hostName = "aaa";
    ObservableDouble obDouble1 = new ObservableDouble("obDouble1", 1.0);
    ObservableDouble obDouble2 = new ObservableDouble("obDouble2", 1.0);

    ReactiveFactory.getDouble("obDouble1.get() * obDouble2.get()", 1.0, "mul");
    ReactiveFactory.getDouble("obDouble1.get() / obDouble2.get()", 1.0, "div");

    ReactiveDouble sub = ReactiveFactory.getDouble("mul.get() - div.get()", 1.0, "sub");

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    sub.addReactiveChangeListener(this);

    obDouble1.set(10);
    obDouble2.set(10);

    obDouble1.set(20);
    obDouble2.set(30);

    obDouble1.set(40);
    obDouble2.set(50);
  }

  @Override
  public void notifyReactiveChanged(Value val) {
    System.out.println("New value: " + val);
  }

}
