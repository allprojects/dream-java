package dream.examples.remote;

import java.util.ArrayList;
import java.util.Random;

import dream.client.Var;
import dream.common.Consts;

public class RemoteVarExample {

  public static void main(String args[]) {
    Consts.hostName = "Remote";
    final Var<Integer> remoteInt = new Var<Integer>("remoteInt", 1);
    final Var<String> remoteString1 = new Var<String>("remoteString1", "a");
    final Var<String> remoteString2 = new Var<String>("remoteString2", "b");
    final Var<ArrayList<Integer>> remoteList = new Var<ArrayList<Integer>>("remoteList", new ArrayList<Integer>());
    final Random random = new Random();

    try {
      Thread.sleep(5000);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    while (true) {
      remoteInt.set(random.nextInt(1000));
      remoteString1.set(String.valueOf(random.nextInt(10)) + " ");
      remoteString2.set(String.valueOf(random.nextInt(10)) + "!");
      remoteList.modify(t -> t.add(random.nextInt(1000)));
      try {
        Thread.sleep(2000);
      } catch (final InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

}
