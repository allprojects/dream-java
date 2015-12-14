package javareact.common.types;

/**
 * Represents a generic reactive object.
 * 
 * In particular, it exposes an evaluate() method which is automatically invoked whenever one of the observable methods
 * it depends on changes.
 * 
 */
public interface Reactive<T> extends ProxyGenerator {

  /**
   * The evaluate method is automatically invoked whenever one of the observable methods this object depends on changes.
   */
  public T evaluate();

}
