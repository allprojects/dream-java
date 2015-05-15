package protopeer.network.mina;

import java.io.*;

import org.apache.mina.common.*;
import org.apache.mina.filter.codec.*;

import protopeer.*;
import protopeer.network.*;
import protopeer.util.*;

/**
 * A {@link ProtocolDecoder} which deserializes {@link Serializable} Java
 * objects using {@link ByteBuffer#getObject(ClassLoader)}.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007) $
 */
public class LightweightObjectSerializationDecoder extends CumulativeProtocolDecoder {
	private final ClassLoader classLoader;

	private int maxObjectSize = 1048576; // 1MB

	/**
	 * Creates a new instance with the {@link ClassLoader} of the current
	 * thread.
	 */
	public LightweightObjectSerializationDecoder() {
		this(Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Creates a new instance with the specified {@link ClassLoader}.
	 */
	public LightweightObjectSerializationDecoder(ClassLoader classLoader) {
		if (classLoader == null) {
			throw new NullPointerException("classLoader");
		}
		this.classLoader = classLoader;
	}

	/**
	 * Returns the allowed maximum size of the object to be decoded. If the size
	 * of the object to be decoded exceeds this value, this decoder will throw a
	 * {@link BufferDataException}. The default value is <tt>1048576</tt>
	 * (1MB).
	 */
	public int getMaxObjectSize() {
		return maxObjectSize;
	}

	/**
	 * Sets the allowed maximum size of the object to be decoded. If the size of
	 * the object to be decoded exceeds this value, this decoder will throw a
	 * {@link BufferDataException}. The default value is <tt>1048576</tt>
	 * (1MB).
	 */
	public void setMaxObjectSize(int maxObjectSize) {
		if (maxObjectSize <= 0) {
			throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
		}

		this.maxObjectSize = maxObjectSize;
	}

	private Object getLightweightSerializedObject(ByteBuffer in) throws IOException {
		int length = in.getInt();
		byte[] bytes = new byte[length];
		in.get(bytes);
		return LightweightSerialization.getSingleton().deserializeObject(bytes);
	}

	protected boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) throws Exception {
		if (!in.prefixedDataAvailable(4, maxObjectSize)) {
			return false;
		}

		Object message = null;
		int positionBefore=in.position();
		if (MainConfiguration.getSingleton().enableLightweightSerialization) {
			message = getLightweightSerializedObject(in);
		} else {
			message = in.getObject(classLoader);
		}
		
		int objectSize=in.position()-positionBefore;
		if (message instanceof Message) {
			((Message) message).setMessageSize(objectSize);
		}

		out.write(message);

		return true;
	}
}
