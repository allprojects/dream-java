package javareact.common.types;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ReactiveList<T extends Serializable> extends AbstractReactive<List<T>> {
	public ReactiveList(String name, Proxy... proxies) {
		super(name, proxies);
		val = new ArrayList<T>();
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

	public int indexOf(Object o) {
		return val.indexOf(o);
	}

	public List<T> subList(int fromIndex, int toIndex) {
		return val.subList(fromIndex, toIndex);
	}
}
