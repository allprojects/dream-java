package dream.common.packets;

import java.io.Serializable;

import dream.common.packets.content.SubType;
import dream.common.packets.content.Subscription;
import protopeer.network.Message;
import protopeer.util.quantities.Data;

/**
 * Packets used to deliver a subscription, which expresses an interest in some
 * specific events.
 */
public class SubscriptionPacket extends Message implements Serializable {
	private static final long serialVersionUID = -9026500933220636540L;
	public static final String subject = "__DREAM_SUBSCRIPTION_PACKET_SUBJECT";

	private final Subscription subscription;
	private final SubType subType;

	public SubscriptionPacket(Subscription subscription, SubType subType) {
		this.subscription = subscription;
		this.subType = subType;
	}

	public final SubType getSubType() {
		return subType;
	}

	public final Subscription getSubscription() {
		return subscription;
	}

	@Override
	public Data getSize() {
		// TODO: estimate the real size
		return Data.inKByte(1);
	}

	@Override
	public String toString() {
		return subType + " " + subscription.toString();
	}

}
