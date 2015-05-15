package protopeer.network.mina;

import org.apache.mina.common.*;
import org.apache.mina.filter.codec.*;
import org.apache.mina.filter.codec.serialization.*;

/**
 * A {@link ProtocolCodecFactory} that serializes and deserializes Java objects.
 * This codec is very useful when you have to prototype your application rapidly
 * without any specific codec.
 *
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 * @version $Rev: 555855 $, $Date: 2007-07-13 12:19:00 +0900 (Fri, 13 Jul 2007) $
 */
public class LightweightObjectSerializationCodecFactory implements ProtocolCodecFactory {
    private final LightweightObjectSerializationEncoder encoder;

    private final LightweightObjectSerializationDecoder decoder;

    /**
     * Creates a new instance with the {@link ClassLoader} of
     * the current thread.
     */
    public LightweightObjectSerializationCodecFactory() {
        this(Thread.currentThread().getContextClassLoader());
    }

    /**
     * Creates a new instance with the specified {@link ClassLoader}.
     */
    public LightweightObjectSerializationCodecFactory(ClassLoader classLoader) {
        encoder = new LightweightObjectSerializationEncoder();
        decoder = new LightweightObjectSerializationDecoder(classLoader);
    }

    public ProtocolEncoder getEncoder() {
        return encoder;
    }

    public ProtocolDecoder getDecoder() {
        return decoder;
    }

    /**
     * Returns the allowed maximum size of the encoded object.
     * If the size of the encoded object exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.  The default value
     * is {@link Integer#MAX_VALUE}.
     * <p>
     * This method does the same job with {@link ObjectSerializationEncoder#getMaxObjectSize()}.
     */
    public int getEncoderMaxObjectSize() {
        return encoder.getMaxObjectSize();
    }

    /**
     * Sets the allowed maximum size of the encoded object.
     * If the size of the encoded object exceeds this value, the encoder
     * will throw a {@link IllegalArgumentException}.  The default value
     * is {@link Integer#MAX_VALUE}.
     * <p>
     * This method does the same job with {@link ObjectSerializationEncoder#setMaxObjectSize(int)}.
     */
    public void setEncoderMaxObjectSize(int maxObjectSize) {
        encoder.setMaxObjectSize(maxObjectSize);
    }

    /**
     * Returns the allowed maximum size of the object to be decoded.
     * If the size of the object to be decoded exceeds this value, the
     * decoder will throw a {@link BufferDataException}.  The default
     * value is <tt>1048576</tt> (1MB).
     * <p>
     * This method does the same job with {@link ObjectSerializationDecoder#getMaxObjectSize()}.
     */
    public int getDecoderMaxObjectSize() {
        return decoder.getMaxObjectSize();
    }

    /**
     * Sets the allowed maximum size of the object to be decoded.
     * If the size of the object to be decoded exceeds this value, the
     * decoder will throw a {@link BufferDataException}.  The default
     * value is <tt>1048576</tt> (1MB).
     * <p>
     * This method does the same job with {@link ObjectSerializationDecoder#setMaxObjectSize(int)}.
     */
    public void setDecoderMaxObjectSize(int maxObjectSize) {
        decoder.setMaxObjectSize(maxObjectSize);
    }
}
