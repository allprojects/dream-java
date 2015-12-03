package javareact.common.types;

@FunctionalInterface
public interface ChangeEventHandler<T> {
	public void handle(T oldVal, T newVal);
}
