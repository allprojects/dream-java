package javareact.common.packets;

import java.io.Serializable;
import java.util.Set;

import javareact.common.packets.content.AdvType;
import javareact.common.packets.content.Advertisement;
import javareact.common.packets.content.Subscription;

/**
 * Packets used to advertise the presence of a given observer.
 * 
 * If the packet is advertising a reactive observable object, it also includes the set of subscriptions required to
 * define all the observable methods the reactive object depends from.
 */
public class AdvertisementPacket implements Serializable {
  private static final long serialVersionUID = 5219175796450319466L;
  public static final String subject = "__JAVA_REACT_ADVERTISEMENT_PACKET_SUBJECT";

  private final Advertisement advertisement;
  private final AdvType advType;
  private final Set<Subscription> subscriptions;
  private final boolean isPublic;

  public AdvertisementPacket(Advertisement advertisement, AdvType advType, Set<Subscription> subscriptions, boolean isPublic) {
    this.advertisement = advertisement;
    this.advType = advType;
    this.subscriptions = subscriptions;
    this.isPublic = isPublic;
  }

  public AdvertisementPacket(Advertisement advertisement, AdvType advType, boolean isPublic) {
    this(advertisement, advType, null, isPublic);
  }

  public Advertisement getAdvertisement() {
    return advertisement;
  }

  public AdvType getAdvType() {
    return advType;
  }

  public final boolean containtsSubscriptions() {
    return subscriptions != null;
  }

  public final Set<Subscription> getSubscriptions() {
    return subscriptions;
  }

  public final boolean isPublic() {
    return isPublic;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((advType == null) ? 0 : advType.hashCode());
    result = prime * result + ((advertisement == null) ? 0 : advertisement.hashCode());
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
    if (!(obj instanceof AdvertisementPacket)) {
      return false;
    }
    AdvertisementPacket other = (AdvertisementPacket) obj;
    if (advType != other.advType) {
      return false;
    }
    if (advertisement == null) {
      if (other.advertisement != null) {
        return false;
      }
    } else if (!advertisement.equals(other.advertisement)) {
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
    if (subscriptions != null) {
      return advType + ": " + advertisement.toString() + " - Depending from: " + subscriptions;
    } else {
      return advType + ": " + advertisement.toString();
    }
  }

}
