package javareact.common.expressions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javareact.common.expressions.antlr_grammars.BoolsParser;
import javareact.common.expressions.antlr_grammars.DoublesParser;
import javareact.common.expressions.antlr_grammars.IntegersLexer;
import javareact.common.expressions.antlr_grammars.IntegersParser;
import javareact.common.expressions.antlr_grammars.StringsParser;
import javareact.common.expressions.visitors.BoolsEvaluatorVisitor;
import javareact.common.expressions.visitors.BoolsIdentifiersExtractorVisitor;
import javareact.common.expressions.visitors.BoolsSubscriptionsGeneratorVisitor;
import javareact.common.expressions.visitors.DoublesEvaluatorVisitor;
import javareact.common.expressions.visitors.DoublesIdentifiersExtractorVisitor;
import javareact.common.expressions.visitors.DoublesSubscriptionsGeneratorVisitor;
import javareact.common.expressions.visitors.IntegersEvaluatorVisitor;
import javareact.common.expressions.visitors.IntegersIdentifiersExtractorVisitor;
import javareact.common.expressions.visitors.IntegersSubscriptionsGeneratorVisitor;
import javareact.common.expressions.visitors.StringsEvaluatorVisitor;
import javareact.common.expressions.visitors.StringsIdentifiersExtractorVisitor;
import javareact.common.expressions.visitors.StringsSubscriptionsGeneratorVisitor;
import javareact.common.packets.content.Subscription;
import javareact.common.packets.content.Value;
import javareact.common.types.Types;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class ExpressionsHandler {
  private final ParseTree parseTree;

  public ExpressionsHandler(Types type, String expression) {
    ANTLRInputStream input = new ANTLRInputStream(expression);
    IntegersLexer lexer = new IntegersLexer(input);
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    switch (type) {
    case INT:
      IntegersParser integersParser = new IntegersParser(tokens);
      parseTree = integersParser.start();
      break;
    case DOUBLE:
      DoublesParser doublesParser = new DoublesParser(tokens);
      parseTree = doublesParser.start();
      break;
    case BOOL:
      BoolsParser boolParser = new BoolsParser(tokens);
      parseTree = boolParser.start();
      break;
    case STRING:
      StringsParser stringParser = new StringsParser(tokens);
      parseTree = stringParser.start();
      break;
    default:
      assert false : type;
      parseTree = null;
    }
  }

  public final Set<Subscription> buildSubscriptions(Types type) {
    switch (type) {
    case INT:
      IntegersSubscriptionsGeneratorVisitor intVisitor = new IntegersSubscriptionsGeneratorVisitor();
      intVisitor.visit(parseTree);
      return intVisitor.getSubscriptions();
    case DOUBLE:
      DoublesSubscriptionsGeneratorVisitor doubleVisitor = new DoublesSubscriptionsGeneratorVisitor();
      doubleVisitor.visit(parseTree);
      return doubleVisitor.getSubscriptions();
    case BOOL:
      BoolsSubscriptionsGeneratorVisitor boolVisitor = new BoolsSubscriptionsGeneratorVisitor();
      boolVisitor.visit(parseTree);
      return boolVisitor.getSubscriptions();
    case STRING:
      StringsSubscriptionsGeneratorVisitor stringVisitor = new StringsSubscriptionsGeneratorVisitor();
      stringVisitor.visit(parseTree);
      return stringVisitor.getSubscriptions();
    default:
      assert false : type;
      return new HashSet<Subscription>();
    }
  }

  public final Collection<String> extractVariableNames(Types type) {
    switch (type) {
    case INT:
      IntegersIdentifiersExtractorVisitor intVisitor = new IntegersIdentifiersExtractorVisitor();
      intVisitor.visit(parseTree);
      return intVisitor.getIdentifiers();
    case DOUBLE:
      DoublesIdentifiersExtractorVisitor doubleVisitor = new DoublesIdentifiersExtractorVisitor();
      doubleVisitor.visit(parseTree);
      return doubleVisitor.getIdentifiers();
    case BOOL:
      BoolsIdentifiersExtractorVisitor boolVisitor = new BoolsIdentifiersExtractorVisitor();
      boolVisitor.visit(parseTree);
      return boolVisitor.getIdentifiers();
    case STRING:
      StringsIdentifiersExtractorVisitor stringVisitor = new StringsIdentifiersExtractorVisitor();
      stringVisitor.visit(parseTree);
      return stringVisitor.getIdentifiers();
    default:
      assert false : type;
      return new ArrayList<String>();
    }
  }

  public final Value evaluateExpression(Map<String, Value> values, Types type) {
    switch (type) {
    case INT:
      IntegersEvaluatorVisitor intVisitor = new IntegersEvaluatorVisitor(values);
      return intVisitor.visit(parseTree);
    case DOUBLE:
      DoublesEvaluatorVisitor doubleVisitor = new DoublesEvaluatorVisitor(values);
      return doubleVisitor.visit(parseTree);
    case BOOL:
      BoolsEvaluatorVisitor boolVisitor = new BoolsEvaluatorVisitor(values);
      return boolVisitor.visit(parseTree);
    case STRING:
      StringsEvaluatorVisitor strVisitor = new StringsEvaluatorVisitor(values);
      return strVisitor.visit(parseTree);
    default:
      assert false : type;
      return null;
    }
  }

}
