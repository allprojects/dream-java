package dream.common.packets.discovery;

import java.io.Serializable;

/**
 * This packet is used by a server to introduce itself whenever a client
 * connects to it.
 */
public class LockManagerHelloPacket implements Serializable {
	private static final long serialVersionUID = -7563002622651694641L;

	public static final String subject = "__DREAM_LOCK_MANAGER_HELLO_PACKET_SUBJECT";
}
