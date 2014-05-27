// Generated from antlr_grammars/Integers.g4 by ANTLR 4.1
package javareact.common.expressions.antlr_grammars;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link IntegersParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface IntegersVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link IntegersParser#observableId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObservableId(@NotNull IntegersParser.ObservableIdContext ctx);

	/**
	 * Visit a parse tree produced by {@link IntegersParser#Parens}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParens(@NotNull IntegersParser.ParensContext ctx);

	/**
	 * Visit a parse tree produced by {@link IntegersParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(@NotNull IntegersParser.StartContext ctx);

	/**
	 * Visit a parse tree produced by {@link IntegersParser#AddSub}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddSub(@NotNull IntegersParser.AddSubContext ctx);

	/**
	 * Visit a parse tree produced by {@link IntegersParser#hostId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHostId(@NotNull IntegersParser.HostIdContext ctx);

	/**
	 * Visit a parse tree produced by {@link IntegersParser#method}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethod(@NotNull IntegersParser.MethodContext ctx);

	/**
	 * Visit a parse tree produced by {@link IntegersParser#Id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(@NotNull IntegersParser.IdContext ctx);

	/**
	 * Visit a parse tree produced by {@link IntegersParser#Int}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInt(@NotNull IntegersParser.IntContext ctx);

	/**
	 * Visit a parse tree produced by {@link IntegersParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(@NotNull IntegersParser.IdentifierContext ctx);

	/**
	 * Visit a parse tree produced by {@link IntegersParser#MulDiv}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulDiv(@NotNull IntegersParser.MulDivContext ctx);
}