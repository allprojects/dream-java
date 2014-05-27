package protopeer.network.mina;

import java.io.*;

import org.apache.mina.common.*;
import org.apache.mina.filter.codec.*;

import protopeer.*;
import protopeer.network.*;
import protopeer.util.*;

/**
 * A {@link ProtocolEncoder} which serializes {@link Serializable} Java objects
 * using {@link ByteBuffer#putObject(Object)}.
 * 
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007) $
 */
public class LightweightObjectSerializationEncoder extends ProtocolEncoderAdapter {
	private int maxObjectSize = Integer.MAX_VALUE; // 2GB

	/**
	 * Creates a new instance.
	 */
	public LightweightObjectSerializationEncoder() {
	}

	/**
	 * Returns the allowed maximum size of the encoded object. If the size of
	 * the encoded object exceeds this value, this encoder will throw a
	 * {@link IllegalArgumentException}. The default value is
	 * {@link Integer#MAX_VALUE}.
	 */
	public int getMaxObjectSize() {
		return maxObjectSize;
	}

	/**
	 * Sets the allowed maximum size of the encoded object. If the size of the
	 * encoded object exceeds this value, this encoder will throw a
	 * {@link IllegalArgumentException}. The default value is
	 * {@link Integer#MAX_VALUE}.
	 */
	public void setMaxObjectSize(int maxObjectSize) {
		if (maxObjectSize <= 0) {
			throw new IllegalArgumentException("maxObjectSize: " + maxObjectSize);
		}

		this.maxObjectSize = maxObjectSize;
	}

	public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
		if (!(message instanceof Serializable)) {
			throw new NotSerializableException();
		}

		ByteBuffer buf = ByteBuffer.allocate(64);
		buf.setAutoExpand(true);
		if (MainConfiguration.getSingleton().enableLightweightSerialization) {
			byte[] serializedMessage = LightweightSerialization.getSingleton().serializeObject(message);
			buf.putInt(serializedMessage.length);
			buf.put(serializedMessage);
		} else {
			buf.putObject(message);
		}

		int objectSize = buf.position() - 4;
		if (objectSize > maxObjectSize) {
			buf.release();
			throw new IllegalArgumentException("The encoded object is too big: " + objectSize + " (> " + maxObjectSize
					+ ')');
		}

		if (message instanceof Message) {
			((Message) message).setMessageSize(objectSize + 4);
		}

		buf.flip();
		out.write(buf);
	}
}
