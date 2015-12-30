package javareact.examples.remote;

import java.util.List;

import javareact.common.Consts;
import javareact.common.types.RemoteVar;
import javareact.common.types.Signal;

public class RemoteReactive {

  public static void main(String args[]) {
    Consts.hostName = "Reactive";

    final RemoteVar<Integer> remoteInt = new RemoteVar<Integer>("Remote", "remoteInt");
    final RemoteVar<String> remoteString1 = new RemoteVar<String>("Remote", "remoteString1");
    final RemoteVar<String> remoteString2 = new RemoteVar<String>("Remote", "remoteString2");
    final RemoteVar<List<Integer>> remoteList = new RemoteVar<List<Integer>>("Remote", "remoteList");

    final Signal<Integer> react1 = new Signal<Integer>("react1", () -> remoteInt.get() + remoteString1.get().length(), remoteInt, remoteString1);
    final Signal<Integer> react2 = new Signal<Integer>("react2", () -> remoteInt.get(), remoteInt);
    final Signal<String> react3 = new Signal<String>("react3", () -> remoteString1.get() + remoteString2.get(), remoteString1, remoteString2);
    final Signal<Integer> react4 = new Signal<Integer>("react4", () -> remoteString1.get().length() + remoteList.get().size(), remoteString1, remoteList);

    react1.addReactiveChangeListener(val -> System.out.println("React1: " + val));
    react2.addReactiveChangeListener(val -> System.out.println("React2: " + val));
    react3.addReactiveChangeListener(val -> System.out.println("React3: " + val));
    react4.addReactiveChangeListener(val -> System.out.println("React4: " + val));
  }
}
