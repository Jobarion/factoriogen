// Generated from C:/Users/jonas/Documents/Java/FactorioSimulator/src/main/antlr4/me/joba/factorio/lang\Language.g4 by ANTLR 4.9.2
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LanguageParser}.
 */
public interface LanguageListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LanguageParser#file}.
	 * @param ctx the parse tree
	 */
	void enterFile(LanguageParser.FileContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#file}.
	 * @param ctx the parse tree
	 */
	void exitFile(LanguageParser.FileContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(LanguageParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(LanguageParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#functionHeader}.
	 * @param ctx the parse tree
	 */
	void enterFunctionHeader(LanguageParser.FunctionHeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#functionHeader}.
	 * @param ctx the parse tree
	 */
	void exitFunctionHeader(LanguageParser.FunctionHeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#functionModifiers}.
	 * @param ctx the parse tree
	 */
	void enterFunctionModifiers(LanguageParser.FunctionModifiersContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#functionModifiers}.
	 * @param ctx the parse tree
	 */
	void exitFunctionModifiers(LanguageParser.FunctionModifiersContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#functionModifier}.
	 * @param ctx the parse tree
	 */
	void enterFunctionModifier(LanguageParser.FunctionModifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#functionModifier}.
	 * @param ctx the parse tree
	 */
	void exitFunctionModifier(LanguageParser.FunctionModifierContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#functionParams}.
	 * @param ctx the parse tree
	 */
	void enterFunctionParams(LanguageParser.FunctionParamsContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#functionParams}.
	 * @param ctx the parse tree
	 */
	void exitFunctionParams(LanguageParser.FunctionParamsContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#functionParam}.
	 * @param ctx the parse tree
	 */
	void enterFunctionParam(LanguageParser.FunctionParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#functionParam}.
	 * @param ctx the parse tree
	 */
	void exitFunctionParam(LanguageParser.FunctionParamContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#signalList}.
	 * @param ctx the parse tree
	 */
	void enterSignalList(LanguageParser.SignalListContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#signalList}.
	 * @param ctx the parse tree
	 */
	void exitSignalList(LanguageParser.SignalListContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(LanguageParser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(LanguageParser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(LanguageParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(LanguageParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(LanguageParser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(LanguageParser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#arrayAssignment}.
	 * @param ctx the parse tree
	 */
	void enterArrayAssignment(LanguageParser.ArrayAssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#arrayAssignment}.
	 * @param ctx the parse tree
	 */
	void exitArrayAssignment(LanguageParser.ArrayAssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#ifExpr}.
	 * @param ctx the parse tree
	 */
	void enterIfExpr(LanguageParser.IfExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#ifExpr}.
	 * @param ctx the parse tree
	 */
	void exitIfExpr(LanguageParser.IfExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#elseStatement}.
	 * @param ctx the parse tree
	 */
	void enterElseStatement(LanguageParser.ElseStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#elseStatement}.
	 * @param ctx the parse tree
	 */
	void exitElseStatement(LanguageParser.ElseStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(LanguageParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#ifStatement}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(LanguageParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#whileExpr}.
	 * @param ctx the parse tree
	 */
	void enterWhileExpr(LanguageParser.WhileExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#whileExpr}.
	 * @param ctx the parse tree
	 */
	void exitWhileExpr(LanguageParser.WhileExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#loopBody}.
	 * @param ctx the parse tree
	 */
	void enterLoopBody(LanguageParser.LoopBodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#loopBody}.
	 * @param ctx the parse tree
	 */
	void exitLoopBody(LanguageParser.LoopBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(LanguageParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#returnStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(LanguageParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCall(LanguageParser.FunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#functionCall}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCall(LanguageParser.FunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void enterArgumentList(LanguageParser.ArgumentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#argumentList}.
	 * @param ctx the parse tree
	 */
	void exitArgumentList(LanguageParser.ArgumentListContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterExpr(LanguageParser.ExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitExpr(LanguageParser.ExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void enterSimpleExpr(LanguageParser.SimpleExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#simpleExpr}.
	 * @param ctx the parse tree
	 */
	void exitSimpleExpr(LanguageParser.SimpleExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#exprList}.
	 * @param ctx the parse tree
	 */
	void enterExprList(LanguageParser.ExprListContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#exprList}.
	 * @param ctx the parse tree
	 */
	void exitExprList(LanguageParser.ExprListContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void enterBoolExpr(LanguageParser.BoolExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#boolExpr}.
	 * @param ctx the parse tree
	 */
	void exitBoolExpr(LanguageParser.BoolExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(LanguageParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(LanguageParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#typeList}.
	 * @param ctx the parse tree
	 */
	void enterTypeList(LanguageParser.TypeListContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#typeList}.
	 * @param ctx the parse tree
	 */
	void exitTypeList(LanguageParser.TypeListContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#arrayDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterArrayDeclaration(LanguageParser.ArrayDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#arrayDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitArrayDeclaration(LanguageParser.ArrayDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#fixedpLiteral}.
	 * @param ctx the parse tree
	 */
	void enterFixedpLiteral(LanguageParser.FixedpLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#fixedpLiteral}.
	 * @param ctx the parse tree
	 */
	void exitFixedpLiteral(LanguageParser.FixedpLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#intLiteral}.
	 * @param ctx the parse tree
	 */
	void enterIntLiteral(LanguageParser.IntLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#intLiteral}.
	 * @param ctx the parse tree
	 */
	void exitIntLiteral(LanguageParser.IntLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#decimalLiteral}.
	 * @param ctx the parse tree
	 */
	void enterDecimalLiteral(LanguageParser.DecimalLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#decimalLiteral}.
	 * @param ctx the parse tree
	 */
	void exitDecimalLiteral(LanguageParser.DecimalLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#hexLiteral}.
	 * @param ctx the parse tree
	 */
	void enterHexLiteral(LanguageParser.HexLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#hexLiteral}.
	 * @param ctx the parse tree
	 */
	void exitHexLiteral(LanguageParser.HexLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#boolLiteral}.
	 * @param ctx the parse tree
	 */
	void enterBoolLiteral(LanguageParser.BoolLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#boolLiteral}.
	 * @param ctx the parse tree
	 */
	void exitBoolLiteral(LanguageParser.BoolLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#varName}.
	 * @param ctx the parse tree
	 */
	void enterVarName(LanguageParser.VarNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#varName}.
	 * @param ctx the parse tree
	 */
	void exitVarName(LanguageParser.VarNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#functionName}.
	 * @param ctx the parse tree
	 */
	void enterFunctionName(LanguageParser.FunctionNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#functionName}.
	 * @param ctx the parse tree
	 */
	void exitFunctionName(LanguageParser.FunctionNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link LanguageParser#signalName}.
	 * @param ctx the parse tree
	 */
	void enterSignalName(LanguageParser.SignalNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link LanguageParser#signalName}.
	 * @param ctx the parse tree
	 */
	void exitSignalName(LanguageParser.SignalNameContext ctx);
}