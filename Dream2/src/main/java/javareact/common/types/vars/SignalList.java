package javareact.common.types.vars;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import javareact.common.types.ProxyGenerator;
import javareact.common.types.Signal;

/**
 * Example of ad-hoc Signal.
 */
public class SignalList<T> extends Signal<ArrayList<T>> {

  public SignalList(String name, Supplier<ArrayList<T>> evaluation, ProxyGenerator... vars) {
    super(name, evaluation, vars);
  }

  public int size() {
    return proxy.get().size();
  }

  public boolean isEmpty() {
    return proxy.get().isEmpty();
  }

  public boolean contains(Object o) {
    return proxy.get().contains(o);
  }

  public boolean containsAll(Collection<?> c) {
    return proxy.get().containsAll(c);
  }

  public T get(int index) {
    return proxy.get().get(index);
  }

  public ArrayList<T> subList(int fromIndex, int toIndex) {
    return new ArrayList<T>(proxy.get().subList(fromIndex, toIndex));
  }

}
