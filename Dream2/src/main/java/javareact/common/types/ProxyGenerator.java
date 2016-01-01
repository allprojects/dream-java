package javareact.common.types;

/**
 * A ProxyGenerator can return a proxy which gets automatically notified when
 * its state changes.
 */
public interface ProxyGenerator {

	/**
	 * Return a proxy for the object.
	 * 
	 * @return a proxy for the object.
	 */
	public Proxy getProxy();

}
