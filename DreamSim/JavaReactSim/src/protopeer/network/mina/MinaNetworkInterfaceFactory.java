package protopeer.network.mina;

import org.apache.log4j.*;
import org.apache.mina.filter.*;
import org.apache.mina.filter.codec.*;
import org.apache.mina.transport.socket.nio.*;

import protopeer.measurement.*;
import protopeer.network.*;

public class MinaNetworkInterfaceFactory implements NetworkInterfaceFactory {

	private static final Logger logger = Logger.getLogger(MinaNetworkInterfaceFactory.class);

	private SocketConnectorConfig socketConnectorConfig;

	private DatagramConnectorConfig datagramConnectorConfig;

	private SocketAcceptorConfig socketAcceptorConfig;

	private DatagramAcceptorConfig datagramAcceptorConfig;

	private MeasurementLogger measurementLogger;	

	public MinaNetworkInterfaceFactory(MeasurementLogger measurementLogger) {		
		this.measurementLogger = measurementLogger;
		init();
	}

	private ProtocolCodecFactory createSerializationCodecInstance() {
		return new LightweightObjectSerializationCodecFactory();
	}

	private void init() {
		socketConnectorConfig = new SocketConnectorConfig();
		// socketConnectorConfig.setThreadModel(ThreadModel.MANUAL);
		socketConnectorConfig.setConnectTimeout(30);
		socketConnectorConfig.getFilterChain().addLast("logger", new LoggingFilter());
		socketConnectorConfig.getFilterChain().addLast("serialization_codec",
				new ProtocolCodecFilter(createSerializationCodecInstance()));

		datagramConnectorConfig = new DatagramConnectorConfig();
		// datagramConnectorConfig.setThreadModel(ThreadModel.MANUAL);
		datagramConnectorConfig.setConnectTimeout(30);
		datagramConnectorConfig.getFilterChain().addLast("logger", new LoggingFilter());
		datagramConnectorConfig.getFilterChain().addLast("serialization_codec",
				new ProtocolCodecFilter(createSerializationCodecInstance()));

		socketAcceptorConfig = new SocketAcceptorConfig();
		// socketAcceptorConfig.setThreadModel(ThreadModel.MANUAL);
		socketAcceptorConfig.setReuseAddress(true);
		socketAcceptorConfig.getFilterChain().addLast("logger", new LoggingFilter());
		socketAcceptorConfig.getFilterChain().addLast("serialization_codec",
				new ProtocolCodecFilter(createSerializationCodecInstance()));

		datagramAcceptorConfig = new DatagramAcceptorConfig();
		// datagramAcceptorConfig.setThreadModel(ThreadModel.MANUAL);
		datagramAcceptorConfig.getFilterChain().addLast("logger", new LoggingFilter());
		datagramAcceptorConfig.getFilterChain().addLast("serialization_codec",
				new ProtocolCodecFilter(createSerializationCodecInstance()));

	}

	public NetworkInterface createNewNetworkInterface(MeasurementLogger measurementLogger, NetworkAddress addressToBindTo) {
		if (addressToBindTo==null || !(addressToBindTo instanceof MinaAddress)) {
			logger.error("addressToBindTo must be a non-null MinaAddress, is: " + addressToBindTo);
			throw new RuntimeException("addressToBindTo must be a non-null MinaAddress, is: " +addressToBindTo);
		}
		return new MinaNetworkInterface(measurementLogger, socketConnectorConfig, datagramConnectorConfig,
				socketAcceptorConfig, datagramAcceptorConfig, (MinaAddress)addressToBindTo);		
	}

}
