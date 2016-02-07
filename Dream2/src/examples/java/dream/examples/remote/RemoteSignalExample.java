package dream.examples.remote;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import dream.client.DreamClient;
import dream.client.RemoteVar;
import dream.client.Signal;
import dream.common.Consts;

public class RemoteSignalExample {

  public static void main(String args[]) {
    Consts.hostName = "Signal";

    final DreamClient client = DreamClient.instance;
    client.connect();

    final Set<String> relevantRemoteVars = new HashSet<>();
    relevantRemoteVars.add("remoteInt@Remote");
    relevantRemoteVars.add("remoteString1@Remote");
    relevantRemoteVars.add("remoteString2@Remote");
    relevantRemoteVars.add("remoteList@Remote");
    while (!client.listVariables().containsAll(relevantRemoteVars)) {
      try {
        Thread.sleep(100);
      } catch (final InterruptedException e) {
        e.printStackTrace();
      }
    }

    final RemoteVar<Integer> remoteInt = new RemoteVar<Integer>("Remote", "remoteInt");
    final RemoteVar<String> remoteString1 = new RemoteVar<String>("Remote", "remoteString1");
    final RemoteVar<String> remoteString2 = new RemoteVar<String>("Remote", "remoteString2");
    final RemoteVar<LinkedList<Integer>> remoteList = new RemoteVar<>("Remote", "remoteList");

    final Signal<Integer> signal1 = new Signal<Integer>("signal1", () -> remoteInt.get() + remoteString1.get().length(), remoteInt, remoteString1);
    final Signal<Integer> signal2 = new Signal<Integer>("signal2", () -> remoteInt.get(), remoteInt);
    final Signal<String> signal3 = new Signal<String>("signal3", () -> remoteString1.get() + remoteString2.get(), remoteString1, remoteString2);
    final Signal<Integer> signal4 = new Signal<Integer>("signal4", () -> remoteString1.get().length() + remoteList.get().size(), remoteString1, remoteList);

    signal1.addValueChangeListener(val -> System.out.println("Signal1: " + val));
    signal2.addValueChangeListener(val -> System.out.println("Signal2: " + val));
    signal3.addValueChangeListener(val -> System.out.println("Signal3: " + val));
    signal4.addValueChangeListener(val -> System.out.println("Signal4: " + val));
  }
}
