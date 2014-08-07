package javareact.examples.local;

import javareact.common.types.observable.ObservableBool;
import javareact.common.types.observable.ObservableDouble;
import javareact.common.types.observable.ObservableInteger;
import javareact.common.types.observable.ObservableString;
import javareact.common.types.reactive.ReactiveBool;
import javareact.common.types.reactive.ReactiveDouble;
import javareact.common.types.reactive.ReactiveFactory;
import javareact.common.types.reactive.ReactiveInteger;
import javareact.common.types.reactive.ReactiveString;

public class LocalExample {

  public static void main(String args[]) {
    ObservableInteger obInt = new ObservableInteger("obInt", 1);
    ObservableDouble obDouble = new ObservableDouble("obDouble", 1.0);
    ObservableBool obBool = new ObservableBool("obBool", false);
    ObservableString obString1 = new ObservableString("obString1", "");
    ObservableString obString2 = new ObservableString("obString2", "");

    ReactiveInteger reactInt = ReactiveFactory.getInteger("10-2+((obInt.get()*2)+ obInt.get())/2", 10, "reactInt");
    ReactiveInteger reactInt2 = ReactiveFactory.getInteger("reactInt.get()*2", 10, "reactInt2");
    ReactiveDouble reactDouble = ReactiveFactory.getDouble("obDouble.get() + obDouble.get()*2.0", 10.0, "reactDouble");
    ReactiveString reactString = ReactiveFactory.getString("((obString1.get()) + obString2.get())", "", "reactString");
    ReactiveBool reactBool = ReactiveFactory.getBool("!(obBool.get() & obBool.get())", true, "reactBool");

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
    System.out.println("reactInt2: " + reactInt2.get() + " (correct value: 316)");
    System.out.println("reactDouble: " + reactDouble.get() + " (correct value: 4.8)");
    System.out.println("reactBool: " + reactBool.get() + " (correct value: false)");
    System.out.println("reactString: " + reactString.get() + " (correct value: Hello World!)");

  }
}
