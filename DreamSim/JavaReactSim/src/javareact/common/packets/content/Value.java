package javareact.common.packets.content;

import java.io.Serializable;

public class Value implements Serializable {
  private static final long serialVersionUID = -4909992361317067576L;

  private final int intVal;
  private final double doubleVal;
  private final String stringVal;
  private final boolean boolVal;
  private final ValueType type;

  public Value(int val) {
    intVal = val;
    doubleVal = 0;
    stringVal = null;
    boolVal = false;
    type = ValueType.INT;
  }

  public Value(double val) {
    intVal = 0;
    doubleVal = val;
    stringVal = null;
    boolVal = false;
    type = ValueType.DOUBLE;
  }

  public Value(String val) {
    intVal = 0;
    doubleVal = 0;
    stringVal = val;
    boolVal = false;
    type = ValueType.STRING;
  }

  public Value(boolean val) {
    intVal = 0;
    doubleVal = 0;
    stringVal = null;
    boolVal = val;
    type = ValueType.BOOL;
  }

  public final ValueType getType() {
    return type;
  }

  public final int intVal() {
    return intVal;
  }

  public final double doubleVal() {
    return doubleVal;
  }

  public final String stringVal() {
    return stringVal;
  }

  public final boolean boolVal() {
    return boolVal;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (boolVal ? 1231 : 1237);
    long temp;
    temp = Double.doubleToLongBits(doubleVal);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + intVal;
    result = prime * result + ((stringVal == null) ? 0 : stringVal.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    if (!(obj instanceof Value)) {
      return false;
    }
    Value other = (Value) obj;
    if (boolVal != other.boolVal) {
      return false;
    }
    if (Double.doubleToLongBits(doubleVal) != Double.doubleToLongBits(other.doubleVal)) {
      return false;
    }
    if (intVal != other.intVal) {
      return false;
    }
    if (stringVal == null) {
      if (other.stringVal != null) {
        return false;
      }
    } else if (!stringVal.equals(other.stringVal)) {
      return false;
    }
    if (type != other.type) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    switch (type) {
    case INT:
      return String.valueOf(intVal);
    case DOUBLE:
      return String.valueOf(doubleVal);
    case STRING:
      return stringVal;
    case BOOL:
      return String.valueOf(boolVal);
    default:
      assert false : type;
      return "err";
    }
  }

}
