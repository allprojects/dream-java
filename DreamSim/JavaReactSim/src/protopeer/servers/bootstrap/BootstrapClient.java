package protopeer.servers.bootstrap;

import java.util.*;

import org.apache.log4j.*;

import protopeer.*;
import protopeer.network.*;

/**
 * On <code>init()</code> this peer uses the <code>idGenerator</code>
 * supplied in the constructor to initialize the peer's identifier. This peerlet
 * on <code>start()</code> contacts the {@link BootstrapServer} under the
 * <code>bootstrapServerAddress</code> supplied in the constructor and gets
 * the initial set of neighbors. After that it calls
 * <code>bootstrapCompleted</code> on all of its
 * {@link BottstrapClientListener}s and becomes idle.
 * 
 * 
 */
public class BootstrapClient extends BasePeerlet {

	private static final Logger logger = Logger.getLogger(BootstrapClient.class);

	private NetworkAddress bootstrapServerAddress;

	private int arrivalSequenceNum;

	private PeerIdentifierGenerator idGenerator;

	private LinkedList<BootstrapClientListener> listeners = new LinkedList<BootstrapClientListener>();

	public BootstrapClient(NetworkAddress bootstrapServerAddress, PeerIdentifierGenerator idGenerator) {
		this.idGenerator = idGenerator;
		this.bootstrapServerAddress = bootstrapServerAddress;
	}

	@Override
	public void handleIncomingMessage(Message message) {
		if (message instanceof InitializePeerMessage) {
			deliverInitializePeerMessage((InitializePeerMessage) message);
		}
	}

	public void addBootstrapClientListener(BootstrapClientListener listener) {
		listeners.add(listener);
	}

	public void removeBootstrapClientListener(BootstrapClientListener listener) {
		listeners.remove(listener);
	}

	public void fireBootstrapCompleted(Collection<Finger> bootstrapPeers) {
		for (BootstrapClientListener listener : listeners) {
			listener.bootstrapCompleted(bootstrapPeers);
		}
	}

	private void deliverInitializePeerMessage(InitializePeerMessage message) {
		if (logger.isDebugEnabled()) {
			logger.debug("InitializePeerMessage received: seqnum: " + message.getArrivalSequenceNum()
					+ " initialNeighbors: " + message.getInitialNeighbors());
		}
		setArrivalSequenceNum(message.getArrivalSequenceNum());
		getPeer().sendMessage(getBootstrapServerAddress(), new InitializationAck());
		fireBootstrapCompleted(message.getInitialNeighbors());
	}

	@Override
	public void init(Peer peer) {
		super.init(peer);
		getPeer().setIdentifier(idGenerator.generatePeerIdentifier(getPeer().getNetworkAddress()));
	}

	@Override
	public void start() {
		super.start();
		// getPeer().setIdentifier(idGenerator.generatePeerIdentifier(getPeer().getNetworkAddress()));
		// say hi to the bootstrap server
		BootstrapHello bootstrapHello=new BootstrapHello(getPeer().getNetworkAddress(), getPeer().getIdentifier());
		getPeer().sendMessage(getBootstrapServerAddress(), bootstrapHello);
		if (logger.isDebugEnabled()) {
			logger.debug("Sending " +bootstrapHello + " to " + getBootstrapServerAddress());
		}
	}

	public int getArrivalSequenceNum() {
		return arrivalSequenceNum;
	}

	private void setArrivalSequenceNum(int getArrivalSequenceNum) {
		this.arrivalSequenceNum = getArrivalSequenceNum;
	}

	private NetworkAddress getBootstrapServerAddress() {
		return bootstrapServerAddress;
	}
}
