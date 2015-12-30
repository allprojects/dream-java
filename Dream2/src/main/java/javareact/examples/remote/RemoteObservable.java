package javareact.examples.remote;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javareact.common.Consts;
import javareact.common.types.Var;

public class RemoteObservable {

  public static void main(String args[]) {
    Consts.hostName = "Remote";
    final Var<Integer> obInt = new Var<Integer>("remoteInt", 1);
    final Var<String> obString1 = new Var<String>("remoteString1", "a");
    final Var<String> obString2 = new Var<String>("remoteString2", "b");
    final Var<List<Integer>> obList = new Var<List<Integer>>("remoteList", new ArrayList<Integer>());
    final Random random = new Random();

    while (true) {
      obInt.set(random.nextInt(1000));
      obString1.set(String.valueOf(random.nextInt(10)) + " ");
      obString2.set(String.valueOf(random.nextInt(10)) + "!");
      obList.modify(t -> t.add(random.nextInt(1000)));
      try {
        Thread.sleep(1000);
      } catch (final InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

}
