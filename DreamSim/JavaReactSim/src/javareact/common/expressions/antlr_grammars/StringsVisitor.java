// Generated from antlr_grammars/Strings.g4 by ANTLR 4.1
package javareact.common.expressions.antlr_grammars;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link StringsParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface StringsVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link StringsParser#observableId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitObservableId(@NotNull StringsParser.ObservableIdContext ctx);

	/**
	 * Visit a parse tree produced by {@link StringsParser#Parens}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParens(@NotNull StringsParser.ParensContext ctx);

	/**
	 * Visit a parse tree produced by {@link StringsParser#start}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStart(@NotNull StringsParser.StartContext ctx);

	/**
	 * Visit a parse tree produced by {@link StringsParser#hostId}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHostId(@NotNull StringsParser.HostIdContext ctx);

	/**
	 * Visit a parse tree produced by {@link StringsParser#method}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMethod(@NotNull StringsParser.MethodContext ctx);

	/**
	 * Visit a parse tree produced by {@link StringsParser#Concat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConcat(@NotNull StringsParser.ConcatContext ctx);

	/**
	 * Visit a parse tree produced by {@link StringsParser#Id}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitId(@NotNull StringsParser.IdContext ctx);

	/**
	 * Visit a parse tree produced by {@link StringsParser#identifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIdentifier(@NotNull StringsParser.IdentifierContext ctx);
}