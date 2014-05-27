package javareact.common.types.reactive;

import javareact.common.types.ReactiveChangeListener;

/**
 * A Reactive object enables the registration and deregistration of ReactiveChangeListeners.
 */
public interface Reactive {

  /**
   * Add the given listener.
   * 
   * @param listener the listener.
   */
  public void addReactiveChangeListener(ReactiveChangeListener listener);

  /**
   * Remove the given listener.
   * 
   * @param listener the listener.
   */
  public void removeReactiveChangeListener(ReactiveChangeListener listener);

}