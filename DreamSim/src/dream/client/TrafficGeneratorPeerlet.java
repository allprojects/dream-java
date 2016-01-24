package dream.client;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import dream.common.Consts;
import dream.common.packets.content.Subscription;
import dream.experiments.DreamConfiguration;
import dream.generator.GraphGeneratorListener;
import dream.generator.GraphsGenerator;
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
    startGraphsGeneration();
    registerToGraphsGenerator();
    notifyGraphs();
  }

  @Override
  public void notifyVar(String name) {
    final Var var = new Var(getPeer(), name.split("@")[1], name.split("@")[0]);
    final Timer timer = getPeer().getClock().createNewTimer();

    timer.addTimerListener(t -> {
      var.modify();
      t.schedule(Time.inMilliseconds(getUpdateTimeInMs()));
    });

    timer.schedule(Time.inSeconds(Consts.startSendingEventsAtSecond));
  }

  @Override
  public void notifySignal(String signalName, Set<String> dependencies) {
    final Set<Subscription> subs = dependencies.stream()//
        .map(dep -> new Subscription(dep.split("@")[1], dep.split("@")[0]))//
        .collect(Collectors.toSet());
    new Signal(getPeer(), signalName.split("@")[1], signalName.split("@")[0], subs);
  }

  public int getClientId() {
    return clientId;
  }

  private final void registerToGraphsGenerator() {
    final Timer registerGraphTimer = getPeer().getClock().createNewTimer();
    registerGraphTimer.addTimerListener(timer -> {
      GraphsGenerator.get().addGraphGeneratorListener(TrafficGeneratorPeerlet.this, clientId);
    });
    registerGraphTimer.schedule(Time.inSeconds(Consts.registerToGraphsGeneratorAtSecond));
  }

  private final void startGraphsGeneration() {
    final Timer graphsGenTimer = getPeer().getClock().createNewTimer();
    graphsGenTimer.addTimerListener(timer -> {
      GraphsGenerator.get().generateGraphs(clientId);
    });
    graphsGenTimer.schedule(Time.inSeconds(Consts.startGraphCreationAtSecond));
  }

  private final void notifyGraphs() {
    final Timer notifyGraphsTimer = getPeer().getClock().createNewTimer();
    notifyGraphsTimer.addTimerListener(timer -> {
      GraphsGenerator.get().notifyListeners(clientId);
    });
    notifyGraphsTimer.schedule(Time.inSeconds(Consts.startNotifyGraphsAtSecond));
  }

  private final double getUpdateTimeInMs() {
    final DreamConfiguration conf = DreamConfiguration.get();
    final Random rand = RandomGenerator.get();
    final int minTime = conf.minTimeBetweenEventsInMs;
    final int maxTime = conf.maxTimeBetweenEventsInMs;
    return minTime == maxTime ? maxTime : minTime + rand.nextInt(maxTime - minTime);
  }

}
