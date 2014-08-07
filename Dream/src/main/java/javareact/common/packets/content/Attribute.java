package javareact.common.packets.content;

import java.io.Serializable;

public class Attribute implements Serializable {
  private static final long serialVersionUID = -1970460317346993023L;

  private final String name;
  private final Value value;

  private Attribute(String name, Value value) {
    this.name = name;
    this.value = value;
  }

  // TODO build ad-hoc Exception
  public Attribute(String name, Object value) throws Exception {
    this.name = name;
    if (value instanceof Value) {
      this.value = (Value) value;
    } else if (value instanceof Integer) {
      this.value = new Value((Integer) value);
    } else if (value instanceof Double) {
      this.value = new Value((Double) value);
    } else if (value instanceof String) {
      this.value = new Value((String) value);
    } else if (value instanceof Boolean) {
      this.value = new Value((Boolean) value);
    } else {
      throw new Exception("Invalid value type");
    }
  }

  public Attribute(String name, int value) {
    this(name, new Value(value));
  }

  public Attribute(String name, double value) {
    this(name, new Value(value));
  }

  public Attribute(String name, String value) {
    this(name, new Value(value));
  }

  public Attribute(String name, boolean value) {
    this(name, new Value(value));
  }

  public final String getName() {
    return name;
  }

  public final ValueType getType() {
    return value.getType();
  }

  public final int intVal() {
    return value.intVal();
  }

  public final double doubleVal() {
    return value.doubleVal();
  }

  public final String stringVal() {
    return value.stringVal();
  }

  public final boolean boolVal() {
    return value.boolVal();
  }

  public final Value getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Attribute)) {
      return false;
    }
    Attribute other = (Attribute) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "<" + name + "=" + value + ">";
  }

}
