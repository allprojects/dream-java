package protopeer.network.mina;

import java.net.*;

import org.apache.log4j.*;
import org.apache.mina.common.*;

import protopeer.measurement.*;
import protopeer.network.*;
import protopeer.network.NetworkInterface;

public abstract class BaseMinaNetworkInterface extends NetworkInterface {

	static class MinaIoHandler extends IoHandlerAdapter {

		private static final Logger logger = Logger.getLogger(MinaIoHandler.class);

		private BaseMinaNetworkInterface iface;

		public MinaIoHandler(BaseMinaNetworkInterface iface) {
			this.iface = iface;
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("exceptionCaught", cause);
			}
			iface.fireExceptionHappened(new MinaAddress((InetSocketAddress) session.getRemoteAddress()), null, cause);
		}

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("messageReceived: sesion: " + session + " message: " + message);
			}
			iface.fireMessageReceived(new MinaAddress((InetSocketAddress) session.getRemoteAddress()), (Message) message);
		}

		@Override
		public void messageSent(IoSession session, Object message) throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("messageSent: sesion: " + session + " message: " + message);
			}
			iface.fireMessageSent(new MinaAddress((InetSocketAddress) session.getRemoteAddress()), (Message) message);
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("sessionClosed: sesion: " + session);
			}
			super.sessionClosed(session);
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("sessionCreated: sesion: " + session);
			}
			super.sessionCreated(session);
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("sessionIdle: sesion: " + session + " status: " + status);
			}
			super.sessionIdle(session, status);
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("sessionOpened: sesion: " + session);
			}
			session.setIdleTime(IdleStatus.BOTH_IDLE, 300);
			super.sessionOpened(session);
		}
	}

	public BaseMinaNetworkInterface(MeasurementLogger measurementLogger) {
		super(null,measurementLogger);
	}

}
