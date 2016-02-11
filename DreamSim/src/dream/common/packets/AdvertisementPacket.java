package dream.common.packets;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import dream.common.packets.content.AdvType;
import dream.common.packets.content.Advertisement;
import dream.common.packets.content.Subscription;
import protopeer.network.Message;
import protopeer.util.quantities.Data;

/**
 * Packets used to advertise the presence of a given observer.
 *
 * If the packet is advertising an object the depends on other objects, it also
 * includes the set of subscriptions that define such dependencies.
 */
public class AdvertisementPacket extends Message implements Serializable {
  private static final long serialVersionUID = 5219175796450319466L;
  public static final String subject = "__DREAM_ADVERTISEMENT_PACKET_SUBJECT";

  private final Advertisement advertisement;
  private final AdvType advType;
  private final Set<Subscription> subscriptions = new HashSet<>();

  public AdvertisementPacket(Advertisement advertisement, AdvType advType, Set<Subscription> subscriptions) {
    this(advertisement, advType);
    this.subscriptions.addAll(subscriptions);
  }

  public AdvertisementPacket(Advertisement advertisement, AdvType advType) {
    this.advertisement = advertisement;
    this.advType = advType;
  }

  public Advertisement getAdvertisement() {
    return advertisement;
  }

  public AdvType getAdvType() {
    return advType;
  }

  public final Set<Subscription> getSubscriptions() {
    return subscriptions;
  }

  @Override
  public Data getSize() {
    // TODO: estimate the real size
    return Data.inKByte(1);
  }

  @Override
  public String toString() {
    if (subscriptions != null) {
      return advType + ": " + advertisement.toString() + " - Depending on: " + subscriptions;
    } else {
      return advType + ": " + advertisement.toString();
    }
  }

}
