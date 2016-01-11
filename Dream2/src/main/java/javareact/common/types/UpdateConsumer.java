package javareact.common.types;

import javareact.common.packets.EventPacket;

/**
 * A TaskConsumer consumes update tasks from a TaskProducer.
 */
public interface UpdateConsumer {

  /**
   * Notifies a local update coming from the given signal.
   *
   * @param packet
   *          the packet containing the update.
   * @param producer
   *          the producer that generated the update.
   */
  void updateFromProducer(EventPacket packet, UpdateProducer<?> producer);
}
