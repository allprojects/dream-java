package dream.client;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import dream.common.Consts;
import dream.common.packets.content.Subscription;
import dream.experiments.DreamConfiguration;
import dream.generator.GraphGenerator;
import dream.generator.GraphGeneratorListener;
import dream.generator.RandomGenerator;
import protopeer.BasePeerlet;
import protopeer.Peer;
import protopeer.time.Timer;
import protopeer.util.quantities.Time;

public class TrafficGeneratorPeerlet extends BasePeerlet implements GraphGeneratorListener {
  private static int clientIdCount = 0;

  private int clientId;

  public static final void resetCount() {
    clientIdCount = 0;
  }

  @Override
  public void init(Peer peer) {
    super.init(peer);
    clientId = clientIdCount;
    clientIdCount++;
    startGraphGeneration();
    registerToGraphGenerator();
    notifyGraph();
  }

  @Override
  public void notifyVar(String name) {
    final Var var = new Var(getPeer(), name.split("@")[1], name.split("@")[0]);
    final Timer timer = getPeer().getClock().createNewTimer();

    timer.addTimerListener(t -> {
      var.modify();
      t.schedule(Time.inMilliseconds(getTimeBetweenVarUpdateInMs()));
    });

    timer.schedule(Time.inSeconds(Consts.startSendingEventsAtSecond));
  }

  @Override
  public void notifySignal(String signalName, Set<String> dependencies) {
    final Set<Subscription> subs = dependencies.stream()//
        .map(dep -> new Subscription(dep.split("@")[1], dep.split("@")[0]))//
        .collect(Collectors.toSet());
    final Signal s = new Signal(getPeer(), signalName.split("@")[1], signalName.split("@")[0], subs);

    if (DreamConfiguration.get().consistencyType == DreamConfiguration.ATOMIC) {
      final Timer timer = getPeer().getClock().createNewTimer();

      timer.addTimerListener(t -> {
        s.atomicRead();
        t.schedule(Time.inMilliseconds(getTimeBetweenSignalReadInMs()));
      });

      timer.schedule(Time.inSeconds(Consts.startReadingSignalsAtSecond));
    }
  }

  public int getClientId() {
    return clientId;
  }

  private final void registerToGraphGenerator() {
    final Timer registerGraphTimer = getPeer().getClock().createNewTimer();
    registerGraphTimer.addTimerListener(timer -> {
      GraphGenerator.get().addGraphGeneratorListener(TrafficGeneratorPeerlet.this, clientId);
    });
    registerGraphTimer.schedule(Time.inSeconds(Consts.registerToGraphsGeneratorAtSecond));
  }

  private final void startGraphGeneration() {
    final Timer graphGenTimer = getPeer().getClock().createNewTimer();
    graphGenTimer.addTimerListener(timer -> {
      GraphGenerator.get().generateGraphs(clientId);
    });
    graphGenTimer.schedule(Time.inSeconds(Consts.startGraphCreationAtSecond));
  }

  private final void notifyGraph() {
    final Timer notifyGraphTimer = getPeer().getClock().createNewTimer();
    notifyGraphTimer.addTimerListener(timer -> {
      GraphGenerator.get().notifyListeners(clientId);
    });
    notifyGraphTimer.schedule(Time.inSeconds(Consts.startNotifyGraphAtSecond));
  }

  private final double getTimeBetweenVarUpdateInMs() {
    final DreamConfiguration conf = DreamConfiguration.get();
    final Random rand = RandomGenerator.get();
    final int minTime = conf.minTimeBetweenEventsInMs;
    final int maxTime = conf.maxTimeBetweenEventsInMs;
    return minTime == maxTime ? maxTime : minTime + rand.nextInt(maxTime - minTime);
  }

  private final double getTimeBetweenSignalReadInMs() {
    final DreamConfiguration conf = DreamConfiguration.get();
    final Random rand = RandomGenerator.get();
    final int minTime = conf.minTimeBetweenSignalReadsInMs;
    final int maxTime = conf.maxTimeBetweenSignalReadsInMs;
    return minTime == maxTime ? maxTime : minTime + rand.nextInt(maxTime - minTime);
  }

}
