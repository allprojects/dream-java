package javareact.common;

public enum ConsistencyType {
  CAUSAL {
    @Override
    public final String toString() {
      return "Causal";
    }
  },
  GLITCH_FREE {
    @Override
    public final String toString() {
      return "GlitchFree";
    }
  },
  ATOMIC {
    @Override
    public final String toString() {
      return "Atomic";
    }
  }
}
