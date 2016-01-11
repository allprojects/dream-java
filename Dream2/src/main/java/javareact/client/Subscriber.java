package javareact.client;

import javareact.common.packets.EventPacket;

/**
 * A subscriber receives events that express some change in the value of an time
 * changing value.
 */
public interface Subscriber {

  /**
   * Notifies the subscriber that the given event has occurred.
   *
   * @param event
   *          the occurred event.
   */
  public void notifyEventReceived(EventPacket event);

}
