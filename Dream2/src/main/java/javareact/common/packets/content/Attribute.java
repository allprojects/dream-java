package javareact.common.packets.content;

import java.io.Serializable;

public class Attribute<T> implements Serializable {
  private static final long serialVersionUID = -1970460317346993023L;

  private final String name;
  private final T value;

  public Attribute(String name, T value) {
    this.name = name;
    this.value = value;
  }

  public final String getName() {
    return name;
  }

  public final T getValue() {
    return value;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (name == null ? 0 : name.hashCode());
    result = prime * result + (value == null ? 0 : value.hashCode());
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
    final Attribute<?> other = (Attribute<?>) obj;
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

  public static <T> Attribute<T> of(String name, T value) {
    return new Attribute<T>(name, value);
  }

}
