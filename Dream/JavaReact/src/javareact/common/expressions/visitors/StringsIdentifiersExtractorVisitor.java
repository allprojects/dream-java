package javareact.common.expressions.visitors;

import java.util.ArrayList;
import java.util.Collection;

import javareact.common.Consts;
import javareact.common.expressions.antlr_grammars.StringsBaseVisitor;
import javareact.common.expressions.antlr_grammars.StringsParser;

public class StringsIdentifiersExtractorVisitor extends StringsBaseVisitor<Void> {
  private final Collection<String> identifiers = new ArrayList<String>();

  @Override
  public Void visitIdentifier(StringsParser.IdentifierContext ctx) {
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
