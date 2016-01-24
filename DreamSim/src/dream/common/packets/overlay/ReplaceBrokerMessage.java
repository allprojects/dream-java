package dream.common.packets.overlay;

import protopeer.network.Message;
import protopeer.network.NetworkAddress;

/**
 * This class represents a message for replacing an existing broker with a new
 * broker.
 *
 * @author Daniel Dubois <daniel@dubois.it>
 */
public class ReplaceBrokerMessage extends Message {

  private static final long serialVersionUID = 527500412680135179L;

  private final NetworkAddress existingBroker;
  private final NetworkAddress newBroker;

  /**
   * Create a message for replacing a broker.
   * 
   * @param existingBroker
   *          Existing broker to replace
   * @param newBroker
   *          New broker to replace the existing one
   */
  public ReplaceBrokerMessage(NetworkAddress existingBroker, NetworkAddress newBroker) {
    this.existingBroker = existingBroker;
    this.newBroker = newBroker;
  }

  /**
   * Get the address of the existing broker.
   * 
   * @return Address of the existing broker
   */
  public NetworkAddress getExistingBroker() {
    return existingBroker;
  }

  /**
   * Get the address of the new broker.
   * 
   * @return Address of the new broker
   */
  public NetworkAddress getNewBroker() {
    return newBroker;
  }

  @Override
  public String toString() {
    return "REPLACEBROKER (-" + existingBroker + " +" + newBroker + ")";
  }
}
