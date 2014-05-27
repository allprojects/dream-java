package javareact.common.packets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Set;

import javareact.common.packets.content.SubType;
import javareact.common.packets.content.Subscription;
import protopeer.network.Message;
import protopeer.util.quantities.Data;

/**
 * Packets used to deliver a subscription, which expresses an interest in some specific events.
 */
public class SubscriptionPacket extends Message implements Iterable<Subscription> {
  private static final long serialVersionUID = -9026500933220636540L;
  public static final String subject = "__JAVA_REACT_SUBSCRIPTION_PACKET_SUBJECT";

  private final Set<Subscription> subscriptions;
  private final SubType subType;

  public SubscriptionPacket(Set<Subscription> subscriptions, SubType subType) {
    this.subscriptions = subscriptions;
    this.subType = subType;
  }

  public final SubType getSubType() {
    return subType;
  }

  @Override
  public Iterator<Subscription> iterator() {
    return subscriptions.iterator();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((subType == null) ? 0 : subType.hashCode());
    result = prime * result + ((subscriptions == null) ? 0 : subscriptions.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof SubscriptionPacket)) {
      return false;
    }
    SubscriptionPacket other = (SubscriptionPacket) obj;
    if (subType != other.subType) {
      return false;
    }
    if (subscriptions == null) {
      if (other.subscriptions != null) {
        return false;
      }
    } else if (!subscriptions.equals(other.subscriptions)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return subType + ": " + subscriptions.toString();
  }

  @Override
  public Data getSize() {
    ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream;
    try {
      objectOutputStream = new ObjectOutputStream(byteOutputStream);
      objectOutputStream.writeObject(this);
      objectOutputStream.flush();
      objectOutputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Data.inByte(byteOutputStream.toByteArray().length);
  }

}
