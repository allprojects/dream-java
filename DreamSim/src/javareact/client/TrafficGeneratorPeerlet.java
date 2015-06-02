package javareact.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javareact.common.Consts;
import javareact.common.types.Proxy;
import javareact.common.types.RemoteVar;
import javareact.common.types.Types;
import javareact.common.types.Var;
import javareact.common.types.Signal;
import javareact.common.types.Observable;
import javareact.experiments.JavaReactConfiguration;
import javareact.generator.GraphGeneratorListener;
import javareact.generator.GraphsGenerator;
import javareact.generator.RandomGenerator;
import protopeer.BasePeerlet;
import protopeer.Peer;
import protopeer.time.Timer;
import protopeer.time.TimerListener;
import protopeer.util.quantities.Time;

public class TrafficGeneratorPeerlet extends BasePeerlet implements GraphGeneratorListener {
  private static int clientIdCount = 0;

  private int clientId;
  private final List<Observable> observables = new ArrayList<Observable>();

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
    startSendingEvents();
  }

  @Override
  public void notifyObservable(String observableName, Types type) {
    switch (type) {
    case INT:
      observables.add(new Var<Integer>(getPeer(), observableName, 0));
      break;
    case DOUBLE:
      observables.add(new Var<Double>(getPeer(), observableName, 0.0));
      break;
    case BOOL:
      observables.add(new Var<Boolean>(getPeer(), observableName, false));
      break;
    case STRING:
      observables.add(new Var<String>(getPeer(), observableName, ""));
      break;
    default:
      assert false;
    }
  }

  @Override
  public <T> void notifyReactive(String observableName, Function<List<RemoteVar<T>>, T> expression, List<String> proxyNames) {
	List<RemoteVar<T>> proxyList = proxyNames.stream().map((s) -> new RemoteVar<T>(getPeer(), s)).collect(Collectors.toList());
	Proxy[] proxies = new Proxy[proxyNames.size()];
	proxies = proxyList.toArray(proxies);
    new Signal<T>(getPeer(), observableName, () -> expression.apply(proxyList), proxies);
  }

  public int getClientId() {
    return clientId;
  }

  private final void registerToGraphsGenerator() {
    Timer registerGraphTimer = getPeer().getClock().createNewTimer();
    registerGraphTimer.addTimerListener(new TimerListener() {
      @Override
      public void timerExpired(Timer timer) {
        GraphsGenerator.get().addGraphGeneratorListener(TrafficGeneratorPeerlet.this, clientId);
      }
    });
    registerGraphTimer.schedule(Time.inSeconds(Consts.registerToGraphsGeneratorAtSecond));
  }

  private final void startGraphsGeneration() {
    Timer graphsGenTimer = getPeer().getClock().createNewTimer();
    graphsGenTimer.addTimerListener(new TimerListener() {
      @Override
      public void timerExpired(Timer timer) {
        GraphsGenerator.get().generateGraphs(clientId);
      }
    });
    graphsGenTimer.schedule(Time.inSeconds(Consts.startGraphCreationAtSecond));
  }

  private final void notifyGraphs() {
    Timer notifyGraphsTimer = getPeer().getClock().createNewTimer();
    notifyGraphsTimer.addTimerListener(new TimerListener() {
      @Override
      public void timerExpired(Timer timer) {
        GraphsGenerator.get().notifyListeners(clientId);
      }
    });
    notifyGraphsTimer.schedule(Time.inSeconds(Consts.startNotifyGraphsAtSecond));
  }

  private final void startSendingEvents() {
    // Nothing to do if the client does not contain any observable
    Timer eventTimer = getPeer().getClock().createNewTimer();
    eventTimer.addTimerListener(new TimerListener() {
      @Override
      public void timerExpired(Timer timer) {
        if (observables.isEmpty()) return;
        Var<Integer> obsInt = getObservableToUpdate();
        int value = getNewObservableValue();
        obsInt.set(value);
        timer.schedule(Time.inMilliseconds(getUpdateTimeInMs()));
      }
    });
    eventTimer.schedule(Time.inSeconds(Consts.startSendingEventsAtSecond));
  }

  private final double getUpdateTimeInMs() {
    JavaReactConfiguration conf = JavaReactConfiguration.getSingleton();
    Random rand = RandomGenerator.get();
    int minTime = conf.minTimeBetweenEventsInMs;
    int maxTime = conf.maxTimeBetweenEventsInMs;
    return (minTime == maxTime) ? maxTime : minTime + rand.nextInt(maxTime - minTime);
  }

  private final Var<Integer> getObservableToUpdate() {
    Random rand = RandomGenerator.get();
    int numObservables = observables.size();
    int index = rand.nextInt(numObservables);
    Observable obs = observables.get(index);
    //assert (obs instanceof Var<Integer>);
    return (Var<Integer>) obs;
  }

  private final int getNewObservableValue() {
    int maxObservableValue = JavaReactConfiguration.getSingleton().maxObservableValue;
    Random rand = RandomGenerator.get();
    return rand.nextInt(maxObservableValue);
  }
}
