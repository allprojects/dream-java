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

  public ArrayList<T> subList(int fromIndex, int toIndex) {
    return new ArrayList<T>(val.subList(fromIndex, toIndex));
  }

}
