package javareact.common.types;

public class BooleanProxy extends RemoteVar<Boolean> {
  public BooleanProxy(String host, String object) {
    super(host, object);
  }

  public BooleanProxy(String object) {
    super(object);
  }
}
