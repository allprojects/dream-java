package dream.examples.chat.fifo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class Chat extends dream.examples.chat.core.Chat {

	private HashMap<Integer, VectorClock> roomClocks = new HashMap<>();
	private HashMap<Integer, List<CachedMessage>> messageCache = new HashMap<>();

	public Chat(String username, int window_x, int window_y) {
		super(username, window_x, window_y);
	}

	@Override
	protected void sendChatMessage(int roomNumber, String message) {
		// increment VectorClock by 1
		VectorClock roomClock = roomClocks.get(roomNumber);
		roomClock.incrementClock(getHostName());
		// attach VectorClock to message
		String newMessage = roomClock.toString() + "|" + message;
		logger.fine("Sending message with VectorClock " + roomClock + ": " + message);
		super.sendChatMessage(roomNumber, newMessage);
	}

	@Override
	protected void receivedChatMessage(int roomNumber, String sender, String message) {
		String[] temp = message.split("\\|", 2);
		VectorClock messageClock = new VectorClock(temp[0]);
		String newMessage = temp[1];
		cacheMessage(messageClock, roomNumber, sender, newMessage);
		processCachedMessages(roomNumber);
	}

	private void cacheMessage(VectorClock messageClock, int roomNumber, String sender, String message) {
		messageCache.get(roomNumber).add(new CachedMessage(messageClock, roomNumber, sender, message));
	}

	private void processCachedMessages(int roomNumber) {
		List<CachedMessage> messages = messageCache.get(roomNumber);
		VectorClock roomClock = roomClocks.get(roomNumber);
		boolean removed = false;
		for (CachedMessage cm : messages) {
			if (cm.canSend(roomClock)) {
				// increase received messages from the sender
				roomClock.incrementClock(cm.getSender());
				// deliver the message
				super.receivedChatMessage(cm.getRoom(), cm.getSender(), cm.getMessage());
				// remove message from cache
				messages.remove(cm);
			}
		}
		if (removed)
			processCachedMessages(roomNumber);
	}

	@Override
	protected String newRoom(String roomName, int roomNumber) {
		roomClocks.put(roomNumber, new VectorClock());
		messageCache.put(roomNumber, new ArrayList<>());
		return super.newRoom(roomName, roomNumber);
	}
}

class CachedMessage {
	private VectorClock clock;
	private int room;
	private String sender;
	private String message;

	public CachedMessage(VectorClock messageClock, int roomNumber, String sender, String message) {
		this.clock = messageClock;
		this.room = roomNumber;
		this.sender = sender;
		this.message = message;
	}

	public boolean canSend(VectorClock roomClock) {
		// delivered every message that the sender has sent before this message
		boolean senderSent = clock.get(sender) == roomClock.get(sender) + 1;
		// delivered every message that the sender has delivered before this
		// message
		boolean senderDelivered = true;
		for (Entry<String, Integer> e : clock.entrySet()) {
			if (!e.getKey().equals(sender)) {
				if (clock.get(e.getKey()) > roomClock.get(e.getKey()))
					senderDelivered = false;
			}
		}
		return senderSent && senderDelivered;
	}

	public VectorClock getClock() {
		return clock;
	}

	public int getRoom() {
		return room;
	}

	public String getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}
}
