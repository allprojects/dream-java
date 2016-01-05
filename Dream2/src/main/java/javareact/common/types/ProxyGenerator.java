package javareact.common.types;

import javareact.common.SerializablePredicate;

/**
 * A ProxyGenerator can return a proxy which gets automatically notified when
 * its state changes.
 */
public interface ProxyGenerator<T> {

  /**
   * Return a proxy for the object.
   *
   * @return a proxy for the object.
   */
  public Proxy getProxy();

  /**
   * Return a ProxyGenerator for the filtered object.
   *
   * @param constraint
   *          the constraint used to filter.
   * @return a ProxyGenerator for the filtered object.
   */
  public ProxyGenerator<T> filter(SerializablePredicate<T> constraint);

}
