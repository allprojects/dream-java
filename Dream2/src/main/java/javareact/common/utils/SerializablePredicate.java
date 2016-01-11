package javareact.common.utils;

import java.io.Serializable;
import java.util.function.Predicate;

public interface SerializablePredicate<T> extends Serializable, Predicate<T> {

}
