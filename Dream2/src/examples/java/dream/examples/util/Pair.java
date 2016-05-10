package dream.examples.util;

import java.io.Serializable;

public class Pair<S, T> implements Serializable {
	private final S first;
	private final T second;

	public Pair(S a, T b) {
		this.first = a;
		this.second = b;
	}

	public S getFirst() {
		return first;
	}

	public T getSecond() {
		return second;
	}
}