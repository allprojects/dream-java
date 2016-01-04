package javareact.examples.local;

import java.util.ArrayList;

import javareact.common.types.Signal;
import javareact.common.types.vars.SignalList;
import javareact.common.types.vars.VarList;

public class LocalExample5 {

  public static void main(String args[]) {
    final VarList<Integer> varList = new VarList<>("varList", new ArrayList<Integer>());
    varList.add(Integer.valueOf(10));
    varList.add(Integer.valueOf(30));
    varList.add(Integer.valueOf(20));

    final SignalList<Integer> signalList = new SignalList<>("signalList", () -> varList.subList(0, 2), varList);
    final Signal<Boolean> signalBool = new Signal<>("signalBool", () -> signalList.contains(20), signalList);

    signalList.addValueChangeListener(System.out::println);
    signalBool.addValueChangeListener(System.out::println);

    System.out.println("Expected results: ");
    System.out.println("[10, 30]");
    System.out.println(false);
    System.out.println("[10, 30]");
    System.out.println(false);
    System.out.println("[30, 20]");
    System.out.println(true);
    System.out.println("[30, 40]");
    System.out.println(false);
    System.out.println();

    try {
      Thread.sleep(500);
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    varList.add(Integer.valueOf(40));
    varList.add(Integer.valueOf(50));
    varList.remove(Integer.valueOf(10));
    varList.remove(Integer.valueOf(20));

  }

}
