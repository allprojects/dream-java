package javareact.common.types;

public class IntegerProxy extends RemoteVar<Integer> {
  public IntegerProxy(String host, String object) {
    super(host, object);
  }

  public IntegerProxy(String object) {
    super(object);
  }
}
