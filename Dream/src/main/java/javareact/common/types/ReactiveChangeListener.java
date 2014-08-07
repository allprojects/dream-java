package javareact.common.types;

import javareact.common.packets.content.Value;

/**
 * A ReactiveChangeListener can register to a reactive object R and gets notified whenever the value of R changes.
 */
public interface ReactiveChangeListener {

  public void notifyReactiveChanged(Value val);

}
