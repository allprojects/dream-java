package javareact.common.types.vars;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import javareact.common.types.Var;

/**
 * Example of ad-hoc Var. We currently consider only two observable methods:
 * size, isEmpty.
 *
 * TODO: how to extend to methods that require one or more parameters?
 */
public class VarList<T extends Serializable> extends Var<List<T>> {

  public VarList(String objectId, List<T> val) {
    super(objectId, val);
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

  public boolean add(T e) {
    final boolean result = val.add(e);
    impactOn("size", () -> size());
    impactOn("isEmpty", () -> isEmpty());
    return result;
  }

  public boolean remove(Object o) {
    final boolean result = val.remove(o);
    impactOn("size", () -> size());
    impactOn("isEmpty", () -> isEmpty());
    return result;
  }

  public boolean addAll(Collection<? extends T> c) {
    final boolean result = val.addAll(c);
    impactOn("size", () -> size());
    impactOn("isEmpty", () -> isEmpty());
    return result;
  }

  public boolean addAll(int index, Collection<? extends T> c) {
    final boolean result = val.addAll(index, c);
    impactOn("size", () -> size());
    impactOn("isEmpty", () -> isEmpty());
    return result;
  }

  public boolean removeAll(Collection<?> c) {
    final boolean result = val.removeAll(c);
    impactOn("size", () -> size());
    impactOn("isEmpty", () -> isEmpty());
    return result;
  }

  public boolean retainAll(Collection<?> c) {
    final boolean result = val.retainAll(c);
    impactOn("size", () -> size());
    impactOn("isEmpty", () -> isEmpty());
    return result;
  }

  public void replaceAll(UnaryOperator<T> operator) {
    val.replaceAll(operator);
    impactOn("size", () -> size());
    impactOn("isEmpty", () -> isEmpty());
  }

  public boolean removeIf(Predicate<? super T> filter) {
    final boolean result = val.removeIf(filter);
    impactOn("size", () -> size());
    impactOn("isEmpty", () -> isEmpty());
    return result;
  }

  public void clear() {
    val.clear();
    impactOn("size", () -> size());
    impactOn("isEmpty", () -> isEmpty());
  }

  public T set(int index, T element) {
    final T result = val.set(index, element);
    impactOn("size", () -> size());
    impactOn("isEmpty", () -> isEmpty());
    return result;
  }

  public void add(int index, T element) {
    impactOn("size", () -> size());
    impactOn("isEmpty", () -> isEmpty());
    val.add(index, element);
  }

  public T remove(int index) {
    final T result = val.remove(index);
    impactOn("size", () -> size());
    impactOn("isEmpty", () -> isEmpty());
    return result;
  }

}
