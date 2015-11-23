package javareact.common.types;

/**
 * A ReactiveChangeListener can register to a reactive object R and gets notified whenever the value of R changes.
 */
public interface ReactiveChangeListener<T> {

  public void notifyReactiveChanged(T oldValue, T newValue, String host);

}