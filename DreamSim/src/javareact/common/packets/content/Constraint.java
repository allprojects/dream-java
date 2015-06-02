package javareact.common.packets.content;

import java.io.Serializable;

public class Constraint<T> implements Serializable {
  private static final long serialVersionUID = 2361519551421250914L;

  private final String name;
  private final ConstraintOp op;
  private final T val;

  public Constraint(String name, ConstraintOp op, T val) {
    this.name = name;
    this.op = op;
    this.val = val;
  }

  public Constraint(String name) {
    this.name = name;
    op = ConstraintOp.ANY;
    val = null;
  }

  public final boolean isSatisfiedBy(Event ev) {
    if (!ev.hasAttribute(name)) return false;
    Attribute attr = ev.getAttributeFor(name);
    
    if (val == null) {
      // ANY
      return true;
    }
    
    if (attr.getValue() instanceof Integer && val instanceof Integer) {
      Integer intConstrVal = (Integer)val;
	  Integer intEvVal = (Integer)attr.getValue();
	  switch (op) {
		case EQ:
		  return intEvVal.equals(intConstrVal);
		case DF:
		  return !intEvVal.equals(intConstrVal);
		case GT:
		  return intEvVal.compareTo(intConstrVal) > 0;
		case LT:
		  return intEvVal.compareTo(intConstrVal) < 0;
		default:
		  assert false : op;
		  return false;
	  }
    }
    
    if (attr.getValue() instanceof Double && val instanceof Double) {
      Double doubleConstrVal = (Double)val;
	  Double doubleEvVal = (Double)attr.getValue();
	  switch (op) {
		case EQ:
		  return doubleEvVal.equals(doubleConstrVal);
		case DF:
		  return doubleEvVal.equals(doubleConstrVal);
		case GT:
		  return doubleEvVal.compareTo(doubleConstrVal) > 0;
		case LT:
		  return doubleEvVal.compareTo(doubleConstrVal) < 0;
		default:
		  assert false : op;
		  return false;
	  }
    }
    
    if (attr.getValue() instanceof String && val instanceof String) {
      String stringConstrVal = (String)val;
	  String stringEvVal = (String)attr.getValue();
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
    }
    
    if (attr.getValue() instanceof Boolean && val instanceof Boolean) {
      Boolean boolConstrVal = (Boolean)val;
	  Boolean boolEvVal = (Boolean)attr.getValue();
	  switch (op) {
		case EQ:
		  return boolEvVal.equals(boolConstrVal);
		case DF:
		  return !boolEvVal.equals(boolConstrVal);
		default:
		  assert false : op;
		  return false;
	  }
    }
    
    return false;
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
