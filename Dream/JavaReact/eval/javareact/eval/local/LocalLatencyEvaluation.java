package javareact.eval.local;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;

import javareact.client.ClientEventForwarder;
import javareact.common.ConsistencyType;
import javareact.common.Consts;
import javareact.common.packets.content.Value;
import javareact.common.types.observable.ObservableDouble;
import javareact.common.types.reactive.ReactiveFactory;
import javareact.eval.common.EvalParams;
import javareact.eval.common.ParamHandler;
import javareact.eval.common.ReactiveDoubleLatencyImpl;
import javareact.server.ServerLauncher;
import javareact.token_service.TokenServiceLauncher;

public class LocalLatencyEvaluation {
  private static final int MILLI_IN_NANO = 1000000;

  public static void main(String args[]) {
    LocalLatencyEvaluation eval = new LocalLatencyEvaluation();
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
    String filename = burst ? "defaultBurst" : "defaultNoBurst";
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
    String filename = "numOps" + consistencyType.toString();
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
    String filename = "length" + consistencyType.toString();
    for (int length = 2; length <= 20; length += 2) {
      ParamHandler.getInstance().resetToDefault();
      ParamHandler.getInstance().setConsistencyType(consistencyType);
      runReactiveChainEval(filename, length, numRepetitions, skipFirst);
    }
  }

  private final void runEval(String filename, String label, int numRepetitions, int skipFirst) throws Exception {
    startServers(ParamHandler.getInstance().getConsistencyType());
    ObservableDouble obDouble = new ObservableDouble("obDouble", System.nanoTime());
    String expression = getExpression("obDouble.get()");
    ReactiveDoubleLatencyImpl reactDouble = new ReactiveDoubleLatencyImpl(expression, new Value(0), "reactDouble", true);
    reactDouble.setSkipFirst(skipFirst);
    Thread.sleep(1000);
    for (int i = 0; i < numRepetitions; i++) {
      if (!ParamHandler.getInstance().getBurst()) {
        Thread.sleep(EvalParams.SLEEP_TIME);
      }
      obDouble.set(System.nanoTime());
    }
    Thread.sleep(1000);
    stopServers();
    printToFile(filename, label, reactDouble);
  }

  private final void runReactiveChainEval(String filename, int length, int numRepetitions, int skipFirst) throws Exception {
    startServers(ParamHandler.getInstance().getConsistencyType());
    ObservableDouble obDouble = new ObservableDouble("obDouble0", System.nanoTime());
    for (int i = 1; i <= length; i++) {
      String observeExpression = "obDouble" + String.valueOf(i - 1) + ".get()";
      String name = "obDouble" + String.valueOf(i);
      ReactiveFactory.getDouble(observeExpression, 0, name, true);
    }
    ReactiveDoubleLatencyImpl reactDouble = new ReactiveDoubleLatencyImpl("obDouble" + length + ".get()", new Value(0), "reactDouble", true);
    reactDouble.setSkipFirst(skipFirst);
    Thread.sleep(1000);
    for (int i = 0; i < numRepetitions; i++) {
      if (!ParamHandler.getInstance().getBurst()) {
        Thread.sleep(EvalParams.SLEEP_TIME);
      }
      obDouble.set(System.nanoTime());
    }
    Thread.sleep(1000);
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
