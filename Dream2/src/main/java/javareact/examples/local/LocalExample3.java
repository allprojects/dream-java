package javareact.examples.local;

import java.util.ArrayList;
import java.util.List;

import javareact.common.types.Signal;
import javareact.common.types.Var;

public class LocalExample3 {

  public static void main(String args[]) {
    final Var<List<Integer>> varList = new Var<>("varList", new ArrayList<Integer>());
    final Signal<Integer> signalInt = new Signal<Integer>("signalInt", () -> 1000 + varList.get().size(), varList);

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

    varList.modify(self -> self.add(10));
    varList.modify(self -> self.add(20));
    varList.modify(self -> self.add(30));
    varList.modify(self -> self.remove(1));
    varList.modify(self -> self.clear());

  }

}
