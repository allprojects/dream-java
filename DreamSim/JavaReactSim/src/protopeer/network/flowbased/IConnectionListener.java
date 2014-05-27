package protopeer.network.flowbased;

public interface IConnectionListener {
	
	public void connectionEstablished(Connection connection);
	public void connectionTerminated(Connection connection);
	
}
