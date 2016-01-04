package javareact.examples.local;

import java.util.ArrayList;

import javareact.common.types.Signal;
import javareact.common.types.vars.VarList;

public class LocalExample4 {

  public static void main(String args[]) {
    final VarList<Integer> varList = new VarList<>("varList", new ArrayList<Integer>());
    final Signal<Integer> signalInt = new Signal<Integer>("signalInt", () -> 1000 + varList.size(), varList);

    signalInt.addValueChangeListener(System.out::println);

    System.out.println("Expected results: ");
    System.out.println(1001);
    System.out.println(1002);
    System.out.println(1003);
    System.out.println(1002);
    System.out.println(1000);
    System.out.println();

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    varList.add(10);
    varList.add(20);
    varList.add(30);
    varList.remove(1);
    varList.clear();

  }

}
