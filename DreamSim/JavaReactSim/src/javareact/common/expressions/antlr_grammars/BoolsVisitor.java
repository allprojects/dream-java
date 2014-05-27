// Generated from antlr_grammars/Bools.g4 by ANTLR 4.1
package javareact.common.expressions.antlr_grammars;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link BoolsParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface BoolsVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link BoolsParser#observableId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObservableId(@NotNull BoolsParser.ObservableIdContext ctx);

	/**
	 * Visit a parse tree produced by {@link BoolsParser#AndOr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndOr(@NotNull BoolsParser.AndOrContext ctx);

	/**
	 * Visit a parse tree produced by {@link BoolsParser#Parens}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParens(@NotNull BoolsParser.ParensContext ctx);

	/**
	 * Visit a parse tree produced by {@link BoolsParser#Bool}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBool(@NotNull BoolsParser.BoolContext ctx);

	/**
	 * Visit a parse tree produced by {@link BoolsParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(@NotNull BoolsParser.StartContext ctx);

	/**
	 * Visit a parse tree produced by {@link BoolsParser#hostId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHostId(@NotNull BoolsParser.HostIdContext ctx);

	/**
	 * Visit a parse tree produced by {@link BoolsParser#method}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethod(@NotNull BoolsParser.MethodContext ctx);

	/**
	 * Visit a parse tree produced by {@link BoolsParser#Id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(@NotNull BoolsParser.IdContext ctx);

	/**
	 * Visit a parse tree produced by {@link BoolsParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(@NotNull BoolsParser.IdentifierContext ctx);

	/**
	 * Visit a parse tree produced by {@link BoolsParser#Not}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNot(@NotNull BoolsParser.NotContext ctx);
}