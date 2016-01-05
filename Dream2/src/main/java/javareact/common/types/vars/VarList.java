package javareact.common.types.vars;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import javareact.common.types.Var;

/**
 * Example of ad-hoc Var.
 */
public class VarList<T> extends Var<ArrayList<T>> {

  public VarList(String objectId, ArrayList<T> val) {
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
    propagateChange();
    return result;
  }

  public boolean remove(Object o) {
    final boolean result = val.remove(o);
    propagateChange();
    return result;
  }

  public boolean addAll(Collection<? extends T> c) {
    final boolean result = val.addAll(c);
    propagateChange();
    return result;
  }

  public boolean addAll(int index, Collection<? extends T> c) {
    final boolean result = val.addAll(index, c);
    propagateChange();
    return result;
  }

  public boolean removeAll(Collection<?> c) {
    final boolean result = val.removeAll(c);
    propagateChange();
    return result;
  }

  public boolean retainAll(Collection<?> c) {
    final boolean result = val.retainAll(c);
    propagateChange();
    return result;
  }

  public void replaceAll(UnaryOperator<T> operator) {
    val.replaceAll(operator);
    propagateChange();
  }

  public boolean removeIf(Predicate<? super T> filter) {
    final boolean result = val.removeIf(filter);
    propagateChange();
    return result;
  }

  public void clear() {
    val.clear();
    propagateChange();
  }

  public T set(int index, T element) {
    final T result = val.set(index, element);
    propagateChange();
    return result;
  }

  public void add(int index, T element) {
    propagateChange();
    val.add(index, element);
  }

  public T remove(int index) {
    final T result = val.remove(index);
    propagateChange();
    return result;
  }

  public ArrayList<T> subList(int fromIndex, int toIndex) {
    return new ArrayList<T>(val.subList(fromIndex, toIndex));
  }

}
