package javareact.common.packets.content;

import java.io.Serializable;

public class Constraint implements Serializable {
  private static final long serialVersionUID = 2361519551421250914L;

  private final String name;
  private final ConstraintOp op;
  private final Value val;

  private Constraint(String name, ConstraintOp op, Value val) {
    this.name = name;
    this.op = op;
    this.val = val;
  }

  public Constraint(String name) {
    this.name = name;
    op = ConstraintOp.ANY;
    val = null;
  }

  public Constraint(String name, ConstraintOp op, int val) {
    this(name, op, new Value(val));
  }

  public Constraint(String name, ConstraintOp op, double val) {
    this(name, op, new Value(val));
  }

  public Constraint(String name, ConstraintOp op, String val) {
    this(name, op, new Value(val));
  }

  public Constraint(String name, ConstraintOp op, boolean val) {
    this(name, op, new Value(val));
  }

  public final boolean isSatisfiedBy(Event ev) {
    if (!ev.hasAttribute(name)) return false;
    Attribute attr = ev.getAttributeFor(name);
    if (val == null) {
      // ANY
      return true;
    }
    if (attr.getType() != val.getType()) {
      return false;
    }
    switch (attr.getType()) {
    case INT:
      int intConstrVal = val.intVal();
      int intEvVal = attr.intVal();
      switch (op) {
      case EQ:
        return intEvVal == intConstrVal;
      case DF:
        return intEvVal != intConstrVal;
      case GT:
        return intEvVal > intConstrVal;
      case LT:
        return intEvVal < intConstrVal;
      default:
        assert false : op;
        return false;
      }
    case DOUBLE:
      double doubleConstrVal = val.doubleVal();
      double doubleEvVal = attr.doubleVal();
      switch (op) {
      case EQ:
        return doubleEvVal == doubleConstrVal;
      case DF:
        return doubleEvVal != doubleConstrVal;
      case GT:
        return doubleEvVal > doubleConstrVal;
      case LT:
        return doubleEvVal < doubleConstrVal;
      default:
        assert false : op;
        return false;
      }
    case STRING:
      String stringConstrVal = val.stringVal();
      String stringEvVal = attr.stringVal();
      switch (op) {
      case EQ:
        return stringEvVal.equals(stringConstrVal);
      case DF:
        return !stringEvVal.equals(stringConstrVal);
      case IN:
        return stringEvVal.contains(stringConstrVal);
      case SW:
        return stringEvVal.startsWith(stringConstrVal);
      case EW:
        return stringEvVal.endsWith(stringConstrVal);
      default:
        assert false : op;
        return false;
      }
    case BOOL:
      boolean boolConstrVal = val.boolVal();
      boolean boolEvVal = attr.boolVal();
      switch (op) {
      case EQ:
        return boolEvVal == boolConstrVal;
      case DF:
        return boolEvVal != boolConstrVal;
      default:
        assert false : op;
        return false;
      }
    default:
      assert false : attr.getType();
      return false;
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((op == null) ? 0 : op.hashCode());
    result = prime * result + ((val == null) ? 0 : val.hashCode());
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
    if (!(obj instanceof Constraint)) {
      return false;
    }
    Constraint other = (Constraint) obj;
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (op != other.op) {
      return false;
    }
    if (val == null) {
      if (other.val != null) {
        return false;
      }
    } else if (!val.equals(other.val)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "<" + name + op + val + ">";
  }

}
