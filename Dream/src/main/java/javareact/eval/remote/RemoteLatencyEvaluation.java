package javareact.eval.remote;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import javareact.client.ClientEventForwarder;
import javareact.common.ConsistencyType;
import javareact.common.Consts;
import javareact.common.packets.content.Value;
import javareact.common.types.reactive.ReactiveFactory;
import javareact.eval.common.EvalParams;
import javareact.eval.common.ParamHandler;
import javareact.eval.common.ReactiveDoubleLatencyImpl;
import javareact.server.ServerLauncher;
import javareact.token_service.TokenServiceLauncher;

public class RemoteLatencyEvaluation {
  private static final int MILLI_IN_NANO = 1000000;

  public static void main(String args[]) {
    RemoteLatencyEvaluation eval = new RemoteLatencyEvaluation();
    try {
      eval.runAll();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private final void runAll() throws Exception {
    runDefault(EvalParams.NUM_REPETITIONS, EvalParams.SKIP_FIRST, true);
    runDefault(EvalParams.NUM_REPETITIONS, EvalParams.SKIP_FIRST, false);
    runNumOps(EvalParams.NUM_REPETITIONS, EvalParams.SKIP_FIRST);
    runReactiveChainLength(EvalParams.NUM_REPETITIONS, EvalParams.SKIP_FIRST);
  }

  private final void runDefault(int numRepetitions, int skipFirst, boolean burst) throws Exception {
    String filename = burst ? "remoteDefaultBurst" : "remoteDefaultNoBurst";
    ParamHandler.getInstance().setBurst(burst);
    ParamHandler.getInstance().setConsistencyType(ConsistencyType.CAUSAL);
    runEval(filename, "1", numRepetitions, skipFirst);
    ParamHandler.getInstance().setConsistencyType(ConsistencyType.GLITCH_FREE);
    runEval(filename, "2", numRepetitions, skipFirst);
    ParamHandler.getInstance().setConsistencyType(ConsistencyType.ATOMIC);
    runEval(filename, "3", numRepetitions, skipFirst);
  }

  private final void runNumOps(int numRepetitions, int skipFirst) throws Exception {
    runNumOps(numRepetitions, skipFirst, ConsistencyType.CAUSAL);
    runNumOps(numRepetitions, skipFirst, ConsistencyType.GLITCH_FREE);
    runNumOps(numRepetitions, skipFirst, ConsistencyType.ATOMIC);
  }

  private final void runReactiveChainLength(int numRepetitions, int skipFirst) throws Exception {
    runReactiveChainLength(numRepetitions, skipFirst, ConsistencyType.CAUSAL);
    runReactiveChainLength(numRepetitions, skipFirst, ConsistencyType.GLITCH_FREE);
    runReactiveChainLength(numRepetitions, skipFirst, ConsistencyType.ATOMIC);
  }

  private final void runNumOps(int numRepetitions, int skipFirst, ConsistencyType consistencyType) throws Exception {
    String filename = "remoteNumOps" + consistencyType.toString();
    for (int numOps = 1; numOps <= 1000;) {
      ParamHandler.getInstance().resetToDefault();
      ParamHandler.getInstance().setNumOperations(numOps);
      ParamHandler.getInstance().setConsistencyType(consistencyType);
      runEval(filename, String.valueOf(numOps), numRepetitions, skipFirst);
      if (numOps < 10) {
        numOps += 3;
      } else if (numOps < 100) {
        numOps += 30;
      } else {
        numOps += 300;
      }
    }
  }

  private final void runReactiveChainLength(int numRepetitions, int skipFirst, ConsistencyType consistencyType) throws Exception {
    String filename = "remoteLength" + consistencyType.toString();
    for (int length = 2; length <= 20; length += 2) {
      ParamHandler.getInstance().resetToDefault();
      ParamHandler.getInstance().setConsistencyType(consistencyType);
      runReactiveChainEval(filename, length, numRepetitions, skipFirst);
    }
  }

  private final void runEval(String filename, String label, int numRepetitions, int skipFirst) throws Exception {
    startServers(ParamHandler.getInstance().getConsistencyType());
    Thread.sleep(500);
    Process proc = new ProcessBuilder("/opt/local/bin/ant", "TrafficGenerator", ParamHandler.getInstance().getConsistencyType().toString()).start();
    Thread.sleep(2000);
    String expression = getExpression("TrafficGenerator.obDouble0.get()");
    ReactiveDoubleLatencyImpl reactDouble = new ReactiveDoubleLatencyImpl(expression, new Value(0), "reactDouble", true);
    reactDouble.setSkipFirst(skipFirst);
    Thread.sleep((long) (numRepetitions * EvalParams.SLEEP_TIME * 1.5));
    proc.destroy();
    stopServers();
    printToFile(filename, label, reactDouble);
  }

  private final void runReactiveChainEval(String filename, int length, int numRepetitions, int skipFirst) throws Exception {
    startServers(ParamHandler.getInstance().getConsistencyType());
    Thread.sleep(500);
    Process proc = new ProcessBuilder("/opt/local/bin/ant", "TrafficGenerator", ParamHandler.getInstance().getConsistencyType().toString()).start();
    Thread.sleep(2000);
    for (int i = 1; i <= length; i++) {
      String observeExpression = i == 1 ? "TrafficGenerator." : "";
      observeExpression = observeExpression + "obDouble" + String.valueOf(i - 1) + ".get()";
      String name = "obDouble" + String.valueOf(i);
      ReactiveFactory.getDouble(observeExpression, 0, name);
    }
    ReactiveDoubleLatencyImpl reactDouble = new ReactiveDoubleLatencyImpl("obDouble" + length + ".get()", new Value(0), "reactDouble", true);
    reactDouble.setSkipFirst(skipFirst);
    Thread.sleep((long) (numRepetitions * EvalParams.SLEEP_TIME * 1.5));
    proc.destroy();
    stopServers();
    printToFile(filename, String.valueOf(length), reactDouble);
  }

  private final String getExpression(String operand) {
    int numOps = ParamHandler.getInstance().getNumOperations();
    boolean even = false;
    StringBuilder builder = new StringBuilder();
    builder.append(operand);
    for (int i = 2; i <= numOps; i += 2) {
      if (even) {
        builder.append(" + " + operand + " - " + operand);
      } else {
        builder.append(" * " + operand + " / " + operand);
      }
      even = !even;
    }
    return builder.toString();
  }

  private final void startServers(ConsistencyType consistencyType) throws Exception {
    Consts.consistencyType = consistencyType;
    String serverAddr = "reds-tcp:localhost:9000";
    Collection<String> addresses = new ArrayList<String>(1);
    addresses.add(serverAddr);
    ServerLauncher.start();
    TokenServiceLauncher.start(addresses);
    Thread.sleep(1000);
  }

  private final void stopServers() throws Exception {
    ClientEventForwarder.stop();
    ServerLauncher.stop();
    TokenServiceLauncher.stop();
    Thread.sleep(1000);
  }

  private final void printToFile(String filename, String label, ReactiveDoubleLatencyImpl reactive) throws Exception {
    FileOutputStream fos = new FileOutputStream(new File(EvalParams.evalDir + "/" + filename), true);
    int numUpdates = reactive.getUpdatesCount();
    double avgLatency = reactive.getAverageLatencyInNano() / MILLI_IN_NANO;
    String line = label + "\t" + numUpdates + "\t" + avgLatency + "\n";
    fos.write(line.getBytes());
    fos.close();
  }

}
