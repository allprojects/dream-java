package dream.client;

/**
 * A ValueChangeListener can register to a time changing object O and gets
 * notified whenever the value of O changes.
 */
public interface ValueChangeListener<T> {

  public void notifyValueChanged(T newValue);

}
