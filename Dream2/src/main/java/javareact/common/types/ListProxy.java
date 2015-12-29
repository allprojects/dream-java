package javareact.common.types;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class ListProxy<T extends Serializable> extends RemoteVar<List<T>> {
  public ListProxy(String host, String object) {
    super(host, object);
  }

  public ListProxy(String object) {
    super(object);
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

  public boolean containsAll(Collection<?> c) {
    return super.get().containsAll(c);
  }

  public T get(int index) {
    return super.get().get(index);
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
