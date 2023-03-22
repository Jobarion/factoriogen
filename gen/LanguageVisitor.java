// Generated from C:/Users/jonas/Documents/Java/FactorioSimulator/src/main/antlr4/me/joba/factorio/lang\Language.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LanguageParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LanguageVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LanguageParser#file}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFile(LanguageParser.FileContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(LanguageParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#functionHeader}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionHeader(LanguageParser.FunctionHeaderContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#functionModifiers}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionModifiers(LanguageParser.FunctionModifiersContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#functionModifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionModifier(LanguageParser.FunctionModifierContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#functionParams}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionParams(LanguageParser.FunctionParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#functionParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionParam(LanguageParser.FunctionParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#signalList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSignalList(LanguageParser.SignalListContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(LanguageParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(LanguageParser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(LanguageParser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#arrayAssignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayAssignment(LanguageParser.ArrayAssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#ifExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfExpr(LanguageParser.IfExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#elseStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElseStatement(LanguageParser.ElseStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#ifStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfStatement(LanguageParser.IfStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#whileExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitWhileExpr(LanguageParser.WhileExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#loopBody}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLoopBody(LanguageParser.LoopBodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#returnStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturnStatement(LanguageParser.ReturnStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#functionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionCall(LanguageParser.FunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#argumentList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgumentList(LanguageParser.ArgumentListContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(LanguageParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#simpleExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleExpr(LanguageParser.SimpleExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#exprList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExprList(LanguageParser.ExprListContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#boolExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolExpr(LanguageParser.BoolExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(LanguageParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#typeList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeList(LanguageParser.TypeListContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#arrayDeclaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArrayDeclaration(LanguageParser.ArrayDeclarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#fixedpLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFixedpLiteral(LanguageParser.FixedpLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#intLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntLiteral(LanguageParser.IntLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#decimalLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecimalLiteral(LanguageParser.DecimalLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#hexLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitHexLiteral(LanguageParser.HexLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#boolLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolLiteral(LanguageParser.BoolLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#varName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarName(LanguageParser.VarNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#functionName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionName(LanguageParser.FunctionNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link LanguageParser#signalName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSignalName(LanguageParser.SignalNameContext ctx);
}