package javareact.examples.remote;

import java.util.Random;

import javareact.common.Consts;
import javareact.common.types.observable.ObservableInteger;
import javareact.common.types.observable.ObservableString;

public class RemoteObservable {

  public static void main(String args[]) {
    Consts.hostName = "Remote";
    ObservableInteger obInt = new ObservableInteger("obInt", true, 1);
    ObservableString obString1 = new ObservableString("obString1", true, "a");
    ObservableString obString2 = new ObservableString("obString2", true, "b");
    Random random = new Random();

    while (true) {
      obInt.set(random.nextInt(1000));
      obString1.set(String.valueOf(random.nextInt(10)) + " ");
      obString2.set(String.valueOf(random.nextInt(10)) + "!");
      try {
        Thread.sleep(500);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

}
