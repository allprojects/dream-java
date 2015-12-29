package javareact.common.types;

public class DoubleProxy extends RemoteVar<Double> {
  public DoubleProxy(String host, String object) {
    super(host, object);
  }

  public DoubleProxy(String object) {
    super(object);
  }
}
