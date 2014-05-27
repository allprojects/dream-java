package javareact.common.expressions.visitors;

import java.util.Map;

import javareact.client.TrafficGeneratorPeerlet;
import javareact.common.Consts;
import javareact.common.expressions.antlr_grammars.StringsBaseVisitor;
import javareact.common.expressions.antlr_grammars.StringsParser;
import javareact.common.packets.content.Value;
import protopeer.Peer;

public class StringsEvaluatorVisitor extends StringsBaseVisitor<Value> {
  private final Map<String, Value> values;
  private final Peer peer;

  public StringsEvaluatorVisitor(Peer peer, Map<String, Value> values) {
    super();
    this.peer = peer;
    this.values = values;
  }

  @Override
  public Value visitIdentifier(StringsParser.IdentifierContext ctx) {
    int clientId = ((TrafficGeneratorPeerlet) peer.getPeerletOfType(TrafficGeneratorPeerlet.class)).getClientId();
    String hostId = (ctx.hostId() == null) ? Consts.hostPrefix + clientId : ctx.hostId().getText();
    String observableId = ctx.observableId().getText();
    String method = ctx.method().getText();
    String id = hostId + "." + observableId + "." + method;
    return values.get(id);
  }

  @Override
  public Value visitParens(StringsParser.ParensContext ctx) {
    return visit(ctx.stringExpr());
  }

  @Override
  public Value visitConcat(StringsParser.ConcatContext ctx) {
    String left = visit(ctx.stringExpr(0)).stringVal();
    String right = visit(ctx.stringExpr(1)).stringVal();
    return new Value(left + right);
  }

  @Override
  public Value visitId(StringsParser.IdContext ctx) {
    return visit(ctx.identifier());
  }
}
