package javareact.eval.common;

import javareact.common.packets.EventPacket;
import javareact.common.packets.content.Value;
import javareact.common.types.Types;
import javareact.common.types.reactive.AbstractReactive;
import javareact.common.types.reactive.ReactiveDouble;

/**
 * An implementation of ReactiveDouble defined to measure the latency of event propagation. It assumes that the value of
 * the stored double represents the time (in nanoseconds) when the observable has been updates.
 */
public final class ReactiveDoubleLatencyImpl extends AbstractReactive implements ReactiveDouble {

  double totalLatencyInNano = 0;
  int updatesCount = 0;
  int skipFirst = 0;

  public ReactiveDoubleLatencyImpl(String expression, String name, boolean isPublic) {
    super(expression, Types.DOUBLE, name, isPublic);
  }

  public ReactiveDoubleLatencyImpl(String expression, Value startingValue, String name, boolean isPublic) {
    super(expression, startingValue, Types.DOUBLE, name, isPublic);
  }

  public final void setSkipFirst(int skipFirst) {
    this.skipFirst = skipFirst;
  }

  @Override
  public synchronized double get() {
    requiresUpdatedValue();
    return value.doubleVal();
  }

  @Override
  public synchronized void notifyValueChanged(EventPacket evPkt) {
    super.notifyValueChanged(evPkt);
    updatesCount++;
    if (updatesCount > skipFirst) {
      double startTime = value.doubleVal();
      double endTime = System.nanoTime();
      double latency = endTime - startTime;
      totalLatencyInNano += latency;
    }
  }

  public final int getUpdatesCount() {
    return updatesCount;
  }

  /**
   * Return the average latency in nanoseconds.
   */
  public final double getAverageLatencyInNano() {
    return totalLatencyInNano / (updatesCount - skipFirst);
  }
}