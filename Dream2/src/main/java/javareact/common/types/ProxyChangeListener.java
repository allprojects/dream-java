package javareact.common.types;

/**
 * Interface used to register to a proxy. It exposes an update method called
 * whenever the proxy changes.
 */
interface ProxyChangeListener {

  /**
   * Method invoked from a proxy to trigger an update.
   */
  void update(EventProxyPair eventProxyPair);

}
