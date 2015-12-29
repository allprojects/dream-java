package javareact.examples.local;

import java.util.ArrayList;
import java.util.List;

import javareact.common.types.Signal;
import javareact.common.types.Var;

public class LocalExample3 {

  public static void main(String args[]) {
    final Var<List<Integer>> obList = new Var<>("obList", new ArrayList<Integer>());
    final Signal<Integer> reactInt = new Signal<Integer>("reactInt", () -> 1000 + obList.get().size(), obList);

    reactInt.addReactiveChangeListener(newValue -> System.out.println(newValue));

    obList.modify(self -> self.add(10));
    obList.modify(self -> self.add(20));
    obList.modify(self -> self.add(30));
    obList.modify(self -> self.remove(1));
    obList.modify(self -> self.clear());

  }

}
