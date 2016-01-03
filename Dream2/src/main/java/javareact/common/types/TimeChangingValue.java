package javareact.common.types;

import javareact.common.ValueChangeListener;

/**
 * Represents a generic time changing value.
 *
 * In particular, it exposes an evaluate() method which is automatically invoked
 * whenever one of the values it depends on changes.
 *
 * It also exposes methods to register and unregister ValueChangeListener, which
 * are automatically notified when the value changes.
 */
interface TimeChangingValue<T> extends ProxyGenerator {

  /**
   * The evaluate method is automatically invoked whenever one of the observable
   * methods this object depends on changes.
   */
  public T evaluate();

  /**
   * Register a new ValueChangeListener.
   *
   * @param listener
   *          the listener to add.
   */
  public void addValueChangeListener(ValueChangeListener<T> listener);

  /**
   * Unregister a ValueChangeListener.
   *
   * @param listener
   *          the listener to remove.
   */
  public void removeValueChangeListener(ValueChangeListener<T> listener);

}
