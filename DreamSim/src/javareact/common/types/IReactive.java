package javareact.common.types;

/**
 * Represents a generic reactive object.
 * 
 * In particular, it exposes an evaluate() method which is automatically invoked whenever one of the observable methods
 * it depends on changes.
 * 
 * It also exposes methods to register and unregister ReactiveChangeListener, which are automatically notified when the
 * value of the reactive object changes.
 */
public interface IReactive<T> extends ProxyGenerator {

  /**
   * The evaluate method is automatically invoked whenever one of the observable methods this object depends on changes.
   */
  public T evaluate();

  /**
   * Register a new ReactiveChangeListener.
   * 
   * @param listener the listener to add.
   */
  public void addReactiveChangeListener(ReactiveChangeListener<T> listener);

  /**
   * Unregister a ReactiveChangeListener.
   * 
   * @param listener the listener to remove.
   */
  public void removeReactiveChangeListener(ReactiveChangeListener<T> listener);

}
