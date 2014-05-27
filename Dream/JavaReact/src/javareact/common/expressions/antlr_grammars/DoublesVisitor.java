// Generated from antlr_grammars/Doubles.g4 by ANTLR 4.1
package javareact.common.expressions.antlr_grammars;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link DoublesParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface DoublesVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link DoublesParser#observableId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObservableId(@NotNull DoublesParser.ObservableIdContext ctx);

	/**
	 * Visit a parse tree produced by {@link DoublesParser#Double}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDouble(@NotNull DoublesParser.DoubleContext ctx);

	/**
	 * Visit a parse tree produced by {@link DoublesParser#Parens}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParens(@NotNull DoublesParser.ParensContext ctx);

	/**
	 * Visit a parse tree produced by {@link DoublesParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(@NotNull DoublesParser.StartContext ctx);

	/**
	 * Visit a parse tree produced by {@link DoublesParser#AddSub}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddSub(@NotNull DoublesParser.AddSubContext ctx);

	/**
	 * Visit a parse tree produced by {@link DoublesParser#hostId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHostId(@NotNull DoublesParser.HostIdContext ctx);

	/**
	 * Visit a parse tree produced by {@link DoublesParser#method}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethod(@NotNull DoublesParser.MethodContext ctx);

	/**
	 * Visit a parse tree produced by {@link DoublesParser#Id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(@NotNull DoublesParser.IdContext ctx);

	/**
	 * Visit a parse tree produced by {@link DoublesParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(@NotNull DoublesParser.IdentifierContext ctx);

	/**
	 * Visit a parse tree produced by {@link DoublesParser#MulDiv}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMulDiv(@NotNull DoublesParser.MulDivContext ctx);
}