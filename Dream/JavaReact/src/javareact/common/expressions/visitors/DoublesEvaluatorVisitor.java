package javareact.common.expressions.visitors;

import java.util.Map;

import javareact.common.Consts;
import javareact.common.expressions.antlr_grammars.DoublesBaseVisitor;
import javareact.common.expressions.antlr_grammars.DoublesParser;
import javareact.common.packets.content.Value;

public class DoublesEvaluatorVisitor extends DoublesBaseVisitor<Value> {
  private final Map<String, Value> values;

  public DoublesEvaluatorVisitor(Map<String, Value> values) {
    super();
    this.values = values;
  }

  @Override
  public Value visitIdentifier(DoublesParser.IdentifierContext ctx) {
    String hostId = (ctx.hostId() == null) ? Consts.hostName : ctx.hostId().getText();
    String observableId = ctx.observableId().getText();
    String method = ctx.method().getText();
    String id = hostId + "." + observableId + "." + method;
    return values.get(id);
  }

  @Override
  public Value visitParens(DoublesParser.ParensContext ctx) {
    return visit(ctx.doubleExpr());
  }

  @Override
  public Value visitAddSub(DoublesParser.AddSubContext ctx) {
    double left = visit(ctx.doubleExpr(0)).doubleVal();
    double right = visit(ctx.doubleExpr(1)).doubleVal();
    if (ctx.op.getType() == DoublesParser.ADD) {
      return new Value(left + right);
    } else {
      return new Value(left - right);
    }
  }

  @Override
  public Value visitId(DoublesParser.IdContext ctx) {
    return visit(ctx.identifier());
  }

  @Override
  public Value visitDouble(DoublesParser.DoubleContext ctx) {
    return new Value(Double.parseDouble(ctx.getText()));
  }

  @Override
  public Value visitMulDiv(DoublesParser.MulDivContext ctx) {
    double left = visit(ctx.doubleExpr(0)).doubleVal();
    double right = visit(ctx.doubleExpr(1)).doubleVal();
    if (ctx.op.getType() == DoublesParser.MUL) {
      return new Value(left * right);
    } else {
      return new Value(left / right);
    }
  }
}
