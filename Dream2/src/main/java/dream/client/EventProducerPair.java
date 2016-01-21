package dream.client;

import dream.common.packets.EventPacket;

public class EventProducerPair {
  private final EventPacket eventPacket;
  private final UpdateProducer producer;

  public EventProducerPair(EventPacket eventPacket, UpdateProducer producer) {
    super();
    this.eventPacket = eventPacket;
    this.producer = producer;
  }

  public final EventPacket getEventPacket() {
    return eventPacket;
  }

  public final UpdateProducer getUpdateProducer() {
    return producer;
  }

  @Override
  public String toString() {
    return "ComputationHandler [eventPacket=" + eventPacket + ", producer=" + producer + "]";
  }

}
