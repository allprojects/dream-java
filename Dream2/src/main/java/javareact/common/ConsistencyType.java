package javareact.common;

public enum ConsistencyType {
  CAUSAL {
    @Override
    public final String toString() {
      return "Causal";
    }
  },
  SINGLE_SOURCE_GLITCH_FREE {
    @Override
    public final String toString() {
      return "Single source glitch free";
    }
  },
  COMPLETE_GLITCH_FREE {
    @Override
    public final String toString() {
      return "Complete glitch free";
    }
  },
  ATOMIC {
    @Override
    public final String toString() {
      return "Atomic";
    }
  }
}
