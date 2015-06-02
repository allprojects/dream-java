package protopeer.network.flowbased;

import protopeer.network.flowbased.Connection;

/**
 * Interface for a listener to a link. The link will report all connections that
 * are using this link to the listener.
 * 
 */
public interface ILinkListener {

	void addedConnection(Connection connection);

	void removedConnection(Connection connection);
}
