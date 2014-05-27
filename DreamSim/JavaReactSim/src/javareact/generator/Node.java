package javareact.generator;

public class Node {
  private final String hostName;
  private final String name;

  public Node(String hostName, String name) {
    super();
    this.hostName = hostName;
    this.name = name;
  }

  public final String getHostName() {
    return hostName;
  }

  public final String getName() {
    return name;
  }

  public final String getCompleteName() {
    return hostName + "." + name + ".get()";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((hostName == null) ? 0 : hostName.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
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
    if (!(obj instanceof Node)) {
      return false;
    }
    Node other = (Node) obj;
    if (hostName == null) {
      if (other.hostName != null) {
        return false;
      }
    } else if (!hostName.equals(other.hostName)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return hostName + "." + name;
  }

}
