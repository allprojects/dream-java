package dream.client;

@FunctionalInterface
public interface ChangeEventHandler<T> {
	public void handle(T oldVal, T newVal);
}
