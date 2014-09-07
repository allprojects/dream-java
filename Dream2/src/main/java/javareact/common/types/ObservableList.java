package javareact.common.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ObservableList<T extends Serializable> extends Var<List<T>> {
  public ObservableList(String observableId, boolean persistent, List<T> val) {
    super(observableId, persistent, val);
  }

  public ObservableList(String observableId, List<T> val) {
    super(observableId, val);
  }

  public int size() {
    return super.get().size();
  }

  public boolean isEmpty() {
    return super.get().isEmpty();
  }

  public boolean contains(Object o) {
    return super.get().contains(o);
  }

  public synchronized boolean add(T e) {
    boolean result = false;
    modify(self -> self.add(e));
    return result;
  }

  public synchronized boolean remove(Object o) {
    boolean result = false;
    modify(self -> self.remove(o));
    return result;
  }

  public boolean containsAll(Collection<?> c) {
    return super.get().containsAll(c);
  }

  public synchronized boolean addAll(Collection<? extends T> c) {
    boolean result = false;
    modify(self -> self.addAll(c));
    return result;
  }

  public synchronized boolean addAll(int index, Collection<? extends T> c) {
    boolean result = false;
    modify(self -> self.addAll(index, c));
    return result;
  }

  public synchronized boolean removeAll(Collection<?> c) {
    boolean result = false;
    modify(self -> self.removeAll(c));
    return result;
  }

  public synchronized boolean retainAll(Collection<?> c) {
    boolean result = false;
    modify(self -> self.retainAll(c));
    return result;
  }

  public synchronized void clear() {
    modify(self -> self.clear());
  }

  public T get(int index) {
    return super.get().get(index);
  }

  public synchronized T set(int index, T element) {
    T result = null;
    modify(self -> self.set(index, element));
    return result;
  }

  public synchronized void add(int index, T element) {
    modify(self -> self.add(index, element));
  }

  public synchronized T remove(int index) {
    T result = null;
    modify(self -> self.remove(index));
    return result;
  }

  public int indexOf(Object o) {
    return super.get().indexOf(o);
  }

  public int lastIndexOf(Object o) {
    return super.get().lastIndexOf(o);
  }

  public List<T> subList(int fromIndex, int toIndex) {
    return super.get().subList(fromIndex, toIndex);
  }
}
