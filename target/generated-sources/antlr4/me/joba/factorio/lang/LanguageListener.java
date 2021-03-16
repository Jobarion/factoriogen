// Generated from me\joba\factorio\lang\Language.g4 by ANTLR 4.3
package me.joba.factorio.lang;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LanguageParser}.
 */
public interface LanguageListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LanguageParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void enterBlockStatement(@NotNull LanguageParser.BlockStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#blockStatement}.
	 * @param ctx the parse tree
	 */
	void exitBlockStatement(@NotNull LanguageParser.BlockStatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link LanguageParser#completeExpression}.
	 * @param ctx the parse tree
	 */
	void enterCompleteExpression(@NotNull LanguageParser.CompleteExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#completeExpression}.
	 * @param ctx the parse tree
	 */
	void exitCompleteExpression(@NotNull LanguageParser.CompleteExpressionContext ctx);

	/**
	 * Enter a parse tree produced by {@link LanguageParser#whileExpr}.
	 * @param ctx the parse tree
	 */
	void enterWhileExpr(@NotNull LanguageParser.WhileExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#whileExpr}.
	 * @param ctx the parse tree
	 */
	void exitWhileExpr(@NotNull LanguageParser.WhileExprContext ctx);

	/**
	 * Enter a parse tree produced by {@link LanguageParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(@NotNull LanguageParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(@NotNull LanguageParser.AssignmentContext ctx);

	/**
	 * Enter a parse tree produced by {@link LanguageParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(@NotNull LanguageParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(@NotNull LanguageParser.StatementContext ctx);

	/**
	 * Enter a parse tree produced by {@link LanguageParser#ifExpr}.
	 * @param ctx the parse tree
	 */
	void enterIfExpr(@NotNull LanguageParser.IfExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#ifExpr}.
	 * @param ctx the parse tree
	 */
	void exitIfExpr(@NotNull LanguageParser.IfExprContext ctx);

	/**
	 * Enter a parse tree produced by {@link LanguageParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(@NotNull LanguageParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(@NotNull LanguageParser.ExprContext ctx);

	/**
	 * Enter a parse tree produced by {@link LanguageParser#boolExprComponent}.
	 * @param ctx the parse tree
	 */
	void enterBoolExprComponent(@NotNull LanguageParser.BoolExprComponentContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#boolExprComponent}.
	 * @param ctx the parse tree
	 */
	void exitBoolExprComponent(@NotNull LanguageParser.BoolExprComponentContext ctx);

	/**
	 * Enter a parse tree produced by {@link LanguageParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(@NotNull LanguageParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(@NotNull LanguageParser.BlockContext ctx);

	/**
	 * Enter a parse tree produced by {@link LanguageParser#elseExpr}.
	 * @param ctx the parse tree
	 */
	void enterElseExpr(@NotNull LanguageParser.ElseExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#elseExpr}.
	 * @param ctx the parse tree
	 */
	void exitElseExpr(@NotNull LanguageParser.ElseExprContext ctx);

	/**
	 * Enter a parse tree produced by {@link LanguageParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void enterBoolExpr(@NotNull LanguageParser.BoolExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void exitBoolExpr(@NotNull LanguageParser.BoolExprContext ctx);
}