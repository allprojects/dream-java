package javareact.common.types;

import java.util.List;
import java.util.UUID;

import javareact.common.SerializablePredicate;

/**
 * A ProxyRegistrar enables ProxyChangeListeners to register to the proxy of an
 * object.
 */
public interface ProxyRegistrar<T> {

  /**
   * Registers proxyChangeListener to the proxy.
   *
   * @param proxyChangeListener
   *          the ProxyChangeListener.
   */
  void addProxyChangeListener(ProxyChangeListener proxyChangeListener);

  /**
   * Unregisters proxyChangeListener from the proxy.
   *
   * @param proxyChangeListener
   *          the ProxyChangeListener.
   */
  void removeProxyChangeListener(ProxyChangeListener proxyChangeListener);

  /**
   * Return a ProxyRegistrar for the filtered object.
   *
   * @param constraint
   *          the constraint used to filter.
   * @return a ProxyRegistrar for the filtered object.
   */
  public ProxyRegistrar<T> filter(SerializablePredicate<T> constraint);

  /**
   * Return the host of the object.
   *
   * @return the host of the object.
   */
  String getHost();

  /**
   * Return the id of the object.
   *
   * @return the id of the object.
   */
  String getObject();

  /**
   * Return the id of the proxy.
   *
   * @return the id of the proxy.
   */
  UUID getProxyID();

  /**
   * Return the constraints of the proxy.
   *
   * @return the constraints of the proxy.
   */
  List<SerializablePredicate> getConstraints();

}
