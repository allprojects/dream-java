package javareact.eval.common;

class EvalInteger {
  private int val;

  EvalInteger(int val) {
    this.val = val;
  }

  final int getVal() {
    return val;
  }

  final void setVal(int val) {
    this.val = val;
  }

  @Override
  public String toString() {
    return "LocalInteger [val=" + val + "]";
  }
}
