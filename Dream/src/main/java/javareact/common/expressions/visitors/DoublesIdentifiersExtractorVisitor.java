package javareact.common.expressions.visitors;

import java.util.ArrayList;
import java.util.Collection;

import javareact.common.Consts;
import javareact.common.expressions.antlr_grammars.DoublesBaseVisitor;
import javareact.common.expressions.antlr_grammars.DoublesParser;

public class DoublesIdentifiersExtractorVisitor extends DoublesBaseVisitor<Void> {
  private final Collection<String> identifiers = new ArrayList<String>();

  @Override
  public Void visitIdentifier(DoublesParser.IdentifierContext ctx) {
    String hostId = (ctx.hostId() == null) ? Consts.hostName : ctx.hostId().getText();
    String observableId = ctx.observableId().getText();
    String method = ctx.method().getText();
    String id = hostId + "." + observableId + "." + method;
    identifiers.add(id);
    return null;
  }

  public Collection<String> getIdentifiers() {
    return identifiers;
  }

}
