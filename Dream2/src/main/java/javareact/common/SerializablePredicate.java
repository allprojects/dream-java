package javareact.common;

import java.io.Serializable;
import java.util.function.Predicate;

public interface SerializablePredicate<T> extends Serializable, Predicate<T> {

}
