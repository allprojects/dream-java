package protopeer.network.mina;

import java.net.*;
import java.util.*;

import org.apache.log4j.*;
import org.apache.mina.common.*;
import org.apache.mina.transport.socket.nio.*;

import protopeer.measurement.*;
import protopeer.network.*;
import protopeer.queues.*;

public class SocketNetworkInterface extends BaseMinaNetworkInterface {

	private static final Logger logger = Logger.getLogger(MinaNetworkInterface.class);

	private MinaAddress localAddress;

	private SocketConnectorConfig connectorConfig;

	private SocketAcceptorConfig acceptorConfig;

	private SocketAcceptor acceptor;

	private SocketConnector connector;

	private MinaIoHandler handler;

	private PerDestinationMessageQueue outgoingMessageQueue = new PerDestinationMessageQueue();

	private HashSet<SocketAddress> pendingConnectRequests = new HashSet<SocketAddress>();

	public SocketNetworkInterface(MeasurementLogger measurementLogger, SocketConnectorConfig socketConnectorConfig,
			SocketAcceptorConfig socketAcceptorConfig, MinaAddress localAddress) {
		super(measurementLogger);
		this.connectorConfig = socketConnectorConfig;
		this.acceptorConfig = socketAcceptorConfig;
		this.localAddress = localAddress;
		this.acceptor = new SocketAcceptor();
		this.connector = new SocketConnector();
		this.handler = new MinaIoHandler(this);
	}

	public void bringUp() {
		try {
			connector.setDefaultConfig(connectorConfig);
			acceptor.setDefaultConfig(acceptorConfig);
			InetSocketAddress desiredAddress = localAddress.getSocketAddress();
			acceptor.bind(desiredAddress, handler);
			InetSocketAddress boundAddress = (InetSocketAddress) acceptor.getManagedServiceAddresses().iterator()
					.next();
			if (logger.isDebugEnabled()) {
				logger.debug("desiredAddress: " + desiredAddress + " boundAddress: " + boundAddress);
			}
			localAddress = new MinaAddress(boundAddress);
			outgoingMessageQueue.addPerDesitnationMessageQueueListener(new PerDestinationMessageQueueListener() {

				public void messageAvailable(NetworkAddress destination) {
					SocketAddress destinationSocketAddress = ((MinaAddress) destination).getSocketAddress();
					if (logger.isDebugEnabled()) {
						logger.debug("new message available: destination: " + destinationSocketAddress);
					}
					IoSession connectedSession = null;
					// find some connected session and use it
					for (IoSession session : connector.getManagedSessions(destinationSocketAddress)) {
						if (session.isConnected()) {
							connectedSession = session;
							break;
						}
					}
					if (logger.isDebugEnabled()) {
						logger.debug("connectedSession: " + connectedSession);
					}
					if (connectedSession == null) {
						logger.error("connectedSession ==null");
					}
					if (connectedSession != null) {
						processQueue(connectedSession);
					}
				}

			});
			connector.addListener(new IoServiceListener() {

				public void serviceActivated(IoService service, SocketAddress serviceAddress, IoHandler handler,
						IoServiceConfig config) {
					logger.debug("serviceActivated: " + serviceAddress);

				}

				public void serviceDeactivated(IoService service, SocketAddress serviceAddress, IoHandler handler,
						IoServiceConfig config) {
					// TODO Auto-generated method stub

				}

				public void sessionCreated(IoSession session) {
					logger.debug("sessionCreated: " + session);
					// TODO Auto-generated method stub

				}

				public void sessionDestroyed(IoSession session) {
					// TODO Auto-generated method stub

				}
			});
			fireInterfaceUp();
		} catch (Exception e) {
			logger.warn("", e);
			fireExceptionHappened(null, null, e);
		}
	}

	@Override
	public void bringDown() {
		fireInterfaceDown();
	}

	@Override
	public NetworkAddress getNetworkAddress() {
		return localAddress;
	}

	private void processQueue(IoSession connectedSession) {
		Message message = null;
		MinaAddress destination = new MinaAddress((InetSocketAddress) connectedSession.getRemoteAddress());
		try {
			MessageQueue queue = outgoingMessageQueue.getQueue(destination);
			if (logger.isDebugEnabled()) {
				logger.debug("processQueue - destination:" + destination + " session: " + connectedSession
						+ " queue size: " + queue.size());
			}
			while (true) {
				message = queue.dequeue();
				if (message == null) {
					break;
				}
				connectedSession.write(message);
			}
		} catch (Exception e) {
			logger.warn("", e);
			fireExceptionHappened(destination, message, e);
		}
	}

	private void enqueueMessage(Message message) {
		outgoingMessageQueue.enqueue(message);
	}

	private void requestConnection() {

	}

	private IoSession getConnectedSession(SocketAddress destinationSocketAddress) {
		IoSession connectedSession = null;
		// find some connected session and use it
		for (IoSession session : connector.getManagedSessions(destinationSocketAddress)) {
			if (session.isConnected()) {
				connectedSession = session;
				break;
			}
		}
		return connectedSession;
	}

	@Override
	public void sendMessage(NetworkAddress destination, Message message) {
		try {			
			message.setSourceAddress(localAddress);
			message.setDestinationAddress(destination);
			enqueueMessage(message);
			MinaAddress minaDestination = (MinaAddress) destination;
			InetSocketAddress socketAddress = minaDestination.getSocketAddress();
			IoSession connectedSession=getConnectedSession(socketAddress);
			if (connectedSession == null) {
				logger.error("No connected session for address: " + socketAddress);
				synchronized (pendingConnectRequests) {
					logger.error("contains?: " + socketAddress + " " + pendingConnectRequests.contains(socketAddress));
					if (!pendingConnectRequests.contains(socketAddress)) {
						if (logger.isDebugEnabled()) {
							logger.debug("no open session for: " + destination + " message: " + message
									+ ", calling connect...");
						}
						logger.error("adding: " + socketAddress);
						pendingConnectRequests.add(socketAddress);
						// connect if no connected session exists
						ConnectFuture future = connector.connect(socketAddress, handler);
						future.addListener(new IoFutureListener() {
							public void operationComplete(IoFuture future) {
								logger.error("operationComplete: " + ((ConnectFuture) future).isConnected() + "\t"
										+ future.isReady() + "\t" + future.getSession().isConnected() + "\t"
										+ future.getSession());
								if (logger.isDebugEnabled()) {
									logger.debug("finished connecting for: " + future.getSession().getRemoteAddress());
								}
								SocketAddress socketAddress = future.getSession().getRemoteAddress();
								synchronized (pendingConnectRequests) {
									logger.error("removing: " + socketAddress);
									pendingConnectRequests.remove(socketAddress);
								}
								if (((ConnectFuture) future).isConnected()) {
									processQueue(future.getSession());
								} else {
									// when still not connected then drop all
									// messages for that destination
									outgoingMessageQueue.getQueue(new MinaAddress((InetSocketAddress) socketAddress))
											.dropAll();
								}
							}
						});
					}
				}
			} 
		} catch (Exception e) {
			logger.warn("", e);
			fireExceptionHappened(destination, message, e);
		}
	}

	@Override
	public void broadcastMessage(Message message) {
		NetworkException nex = new NetworkException("Broadcast not implemented");
		logger.error("", nex);
		fireExceptionHappened(null, message, nex);
	}

}
