package javareact.common.types.vars;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import javareact.common.types.Var;

/**
 * Example of ad-hoc Var.
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
    impactOnGet();
    return result;
  }

  public boolean remove(Object o) {
    final boolean result = val.remove(o);
    impactOnGet();
    return result;
  }

  public boolean addAll(Collection<? extends T> c) {
    final boolean result = val.addAll(c);
    impactOnGet();
    return result;
  }

  public boolean addAll(int index, Collection<? extends T> c) {
    final boolean result = val.addAll(index, c);
    impactOnGet();
    return result;
  }

  public boolean removeAll(Collection<?> c) {
    final boolean result = val.removeAll(c);
    impactOnGet();
    return result;
  }

  public boolean retainAll(Collection<?> c) {
    final boolean result = val.retainAll(c);
    impactOnGet();
    return result;
  }

  public void replaceAll(UnaryOperator<T> operator) {
    val.replaceAll(operator);
    impactOnGet();
  }

  public boolean removeIf(Predicate<? super T> filter) {
    final boolean result = val.removeIf(filter);
    impactOnGet();
    return result;
  }

  public void clear() {
    val.clear();
    impactOnGet();
  }

  public T set(int index, T element) {
    final T result = val.set(index, element);
    impactOnGet();
    return result;
  }

  public void add(int index, T element) {
    impactOnGet();
    val.add(index, element);
  }

  public T remove(int index) {
    final T result = val.remove(index);
    impactOnGet();
    return result;
  }

  public List<T> subList(int fromIndex, int toIndex) {
    return val.subList(fromIndex, toIndex);
  }

}
