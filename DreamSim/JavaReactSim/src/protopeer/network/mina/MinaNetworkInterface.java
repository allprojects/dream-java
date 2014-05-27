package protopeer.network.mina;

import java.net.*;
import java.util.*;

import org.apache.log4j.*;
import org.apache.mina.common.*;
import org.apache.mina.transport.socket.nio.*;

import protopeer.measurement.*;
import protopeer.network.*;

public class MinaNetworkInterface extends BaseMinaNetworkInterface {

	class ImmediateSendConnectFutureListener implements IoFutureListener {

		private Message message;

		private MinaAddress destination;

		public ImmediateSendConnectFutureListener(MinaAddress destination, Message message) {
			this.message = message;
			this.destination = destination;
		}

		public void operationComplete(IoFuture future) {
			if (logger.isDebugEnabled()) {
				logger.debug("finished connecting for: " + destination + " message: " + message);
			}			
			sendMessageNoConnectCheck(future.getSession(), destination, message);
		}
	}

	private static final Logger logger = Logger.getLogger(MinaNetworkInterface.class);

	//private MinaAddress networkAddress;	

	private SocketConnectorConfig socketConnectorConfig;

	private DatagramConnectorConfig datagramConnectorConfig;

	private SocketAcceptorConfig socketAcceptorConfig;

	private DatagramAcceptorConfig datagramAcceptorConfig;

	private SocketAcceptor socketAcceptor;

	private DatagramAcceptor datagramAcceptor;

	private SocketConnector socketConnector;

	private DatagramConnector datagramConnector;

	private MinaIoHandler handler;

	public MinaNetworkInterface(MeasurementLogger measurementLogger, SocketConnectorConfig socketConnectorConfig,
			DatagramConnectorConfig datagramConnectorConfig, SocketAcceptorConfig socketAcceptorConfig,
			DatagramAcceptorConfig datagramAcceptorConfig, MinaAddress localAddress) {
		super(measurementLogger);
		this.socketConnectorConfig = socketConnectorConfig;
		this.datagramConnectorConfig = datagramConnectorConfig;
		this.socketAcceptorConfig = socketAcceptorConfig;
		this.datagramAcceptorConfig = datagramAcceptorConfig;
		this.networkAddress = localAddress;
		this.socketAcceptor = new SocketAcceptor();
		this.datagramAcceptor = new DatagramAcceptor();
		this.socketConnector = new SocketConnector();
		this.datagramConnector = new DatagramConnector();
		this.handler = new MinaIoHandler(this);
	}

	public void bringUp() {
		try {
			socketConnector.setDefaultConfig(socketConnectorConfig);
			datagramConnector.setDefaultConfig(datagramConnectorConfig);
			socketAcceptor.setDefaultConfig(socketAcceptorConfig);
			datagramAcceptor.setDefaultConfig(datagramAcceptorConfig);
			InetSocketAddress desiredAddress = ((MinaAddress)networkAddress).getSocketAddress();
			socketAcceptor.bind(desiredAddress, handler);
			InetSocketAddress boundAddress = (InetSocketAddress) socketAcceptor.getManagedServiceAddresses().iterator()
					.next();
			if (logger.isDebugEnabled()) {
				logger.debug("desiredAddress: " + desiredAddress + " boundAddress: " + boundAddress);
			}
			// bind the datagram acceptor to the same port and address as the
			// socket acceptor
			datagramAcceptor.bind(boundAddress, handler);
			// reassign, just in case port was ephemeral initially (indicated by
			// port==0 in the contructor)
			networkAddress = new MinaAddress(boundAddress);
			fireInterfaceUp();
		} catch (Exception e) {
			logger.warn("", e);
			exceptionHappened(null, null,  e);
		}
	}

	@Override
	public void bringDown() {
		fireInterfaceDown();
	}	

	@Override
	public NetworkAddress getNetworkAddress() {
		return networkAddress;
	}

	private void sendMessageNoConnectCheck(IoSession session, MinaAddress destination, Message message) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("sendMessageNoConnectCheck: " + destination + " message: " + message);
			}			
			session.write(message);			
		} catch (Exception e) {			
			logger.warn("", e);
			exceptionHappened(destination, message,  e);
		}
	}

	@Override
	public void sendMessage(NetworkAddress destination, Message message) {
		if (logger.isDebugEnabled()) {
			logger.debug("sendMessage(), destination: " + destination + " message: " + message);
		}
		try {
			message.setSourceAddress(networkAddress);
			message = message.clone();
			MinaAddress minaDestination = (MinaAddress) destination;
			InetSocketAddress socketAddress = minaDestination.getSocketAddress();
			IoConnector connector = message.isDatagram() ? datagramConnector : socketConnector;
			Set<IoSession> sessions = connector.getManagedSessions(socketAddress);

			IoSession connectedSession = null;
			// find the first connected session and use it
			for (IoSession session : sessions) {
				if (session.isConnected()) {
					connectedSession = session;
					break;
				}
			}

			if (connectedSession == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("no open session for: " + destination + " message: " + message);
				}
				// connect if no connected session exists
				ConnectFuture future = connector.connect(socketAddress, handler);
				future.addListener(new ImmediateSendConnectFutureListener(minaDestination, message));
			} else {
				sendMessageNoConnectCheck(connectedSession, minaDestination, message);
			}
		} catch (Exception e) {
			logger.warn("", e);
			exceptionHappened(destination, message, e);
		}
	}

	void exceptionHappened(NetworkAddress remoteAddress, Message message, Throwable cause) {
		fireExceptionHappened(remoteAddress, message, cause);
	}

	void messageReceived(NetworkAddress sourceAddress, Message message) {
		fireMessageReceived(sourceAddress, message);
	}

	void messageSent(NetworkAddress destinationAddress, Message message) {
		fireMessageSent(destinationAddress, message);
	}

	@Override
	public void broadcastMessage(Message message) {		
		NetworkException nex=new NetworkException("Broadcast not implemented");
		logger.error("",nex);
		fireExceptionHappened(null, message , nex);		
	}

}
