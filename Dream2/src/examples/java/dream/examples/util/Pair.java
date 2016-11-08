package dream.examples.util;

import java.io.Serializable;

public class Pair<S, T> implements Serializable {
	private static final long serialVersionUID = 8685298368867903814L;
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

	@Override
	public String toString() {
		return "(" + first + ", " + second + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair))
			return false;
		Pair p2 = (Pair) obj;
		return first.equals(p2.first) && second.equals(p2.second);
	}
}