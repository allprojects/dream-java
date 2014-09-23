package javareact;

import javareact.common.types.Var;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;
import javareact.server.ServerLauncher;
import javareact.token_service.TokenServiceLauncher;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import javareact.common.Consts;
import javareact.common.types.ObservableInteger;
import javareact.common.types.ObservableList;
import javareact.common.types.ObservableString;

public class RemoteObservable implements Runnable {

  @Override
  public void run() {
	System.out.println("yaay");
    Consts.hostName = "def";
    Var<Integer> a = new Var<>("a", 1);
    Var<String> someString = new Var<>("someString", "test string");
    
    Var<List<Integer>> intList = new Var<>("intList", new ArrayList<Integer>());
    Random random = new Random();

    while (true) {
      a.set(random.nextInt(1000));
      someString.set(String.valueOf(random.nextInt(10)) + " ");
      intList.modify(self -> self.add(random.nextInt(1000)));
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

  }

}