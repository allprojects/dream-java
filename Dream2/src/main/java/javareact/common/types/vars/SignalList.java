package javareact.common.types.vars;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import javareact.common.types.ProxyGenerator;
import javareact.common.types.Signal;

/**
 * Example of ad-hoc Signal.
 */
public class SignalList<T extends Serializable> extends Signal<List<T>> {

  public SignalList(String name, Supplier<List<T>> evaluation, ProxyGenerator... vars) {
    super(name, evaluation, vars);
  }

  public int size() {
    return val.size();
  }

  public boolean isEmpty() {
    return val.isEmpty();
  }

  public boolean contains(Object o) {
    return val.contains(o);
  }

  public boolean containsAll(Collection<?> c) {
    return val.containsAll(c);
  }

  public T get(int index) {
    return val.get(index);
  }

  public List<T> subList(int fromIndex, int toIndex) {
    return val.subList(fromIndex, toIndex);
  }

}
