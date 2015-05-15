package protopeer.network;

public class IrrecoverableNetworkException extends NetworkException {

	public IrrecoverableNetworkException() {
		super();	
	}

	public IrrecoverableNetworkException(String message, Throwable cause) {
		super(message, cause);
	}

	public IrrecoverableNetworkException(String message) {
		super(message);
	}

	public IrrecoverableNetworkException(Throwable cause) {
		super(cause);
	}

}
