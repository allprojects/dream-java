package dream.common.packets;

import java.io.Serializable;

import dream.common.packets.content.SubType;
import dream.common.packets.content.Subscription;

/**
 * Packets used to deliver a subscription, which expresses an interest in some
 * specific events.
 */
public class SubscriptionPacket implements Serializable {
  private static final long serialVersionUID = -9026500933220636540L;
  public static final String subject = "__DREAM_SUBSCRIPTION_PACKET_SUBJECT";

  private final Subscription<?> subscription;
  private final SubType subType;

  public SubscriptionPacket(Subscription<?> subscription, SubType subType) {
    this.subscription = subscription;
    this.subType = subType;
  }

  public final SubType getSubType() {
    return subType;
  }

  public final Subscription<?> getSubscription() {
    return subscription;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (subType == null ? 0 : subType.hashCode());
    result = prime * result + (subscription == null ? 0 : subscription.hashCode());
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
    final SubscriptionPacket other = (SubscriptionPacket) obj;
    if (subType != other.subType) {
      return false;
    }
    if (subscription == null) {
      if (other.subscription != null) {
        return false;
      }
    } else if (!subscription.equals(other.subscription)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return subType + " " + subscription.toString();
  }

}
