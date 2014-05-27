package protopeer.network;

/**
 * Thrown on exceptions in the <code>protopeer.network</code> package.
 * 
 * @author galuba
 * 
 */
public class NetworkException extends Exception {

	public NetworkException() {
		super();		
	}

	public NetworkException(String message, Throwable cause) {
		super(message, cause);
	}

	public NetworkException(String message) {
		super(message);
	}

	public NetworkException(Throwable cause) {
		super(cause); 
	}

	

}
