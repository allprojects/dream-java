package protopeer.network;

/**
 * Corresponding unchecked exception to <code>NetworkException</code>.
 * 
 */
public class NetworkRuntimeException extends RuntimeException {

	public NetworkRuntimeException() {
		super();		
	}

	public NetworkRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public NetworkRuntimeException(String message) {
		super(message);
	}

	public NetworkRuntimeException(Throwable cause) {
		super(cause); 
	}

}
