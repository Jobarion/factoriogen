package me.joba.factorio.lang.expr;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.game.entities.ArithmeticCombinator;
import me.joba.factorio.game.entities.ArithmeticOperator;
import me.joba.factorio.lang.*;
import me.joba.factorio.lang.types.PrimitiveType;

import java.util.Optional;

public class BooleanExpressionResolver extends ExpressionResolver<LanguageParser.BoolExprContext, ArithmeticOperator> {

    public BooleanExpressionResolver() {
        super(PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN);
    }

    @Override
    public ArithmeticOperator getOperation(LanguageParser.BoolExprContext ruleContext) {
        return switch(ruleContext.op.getText()) {
            case "&&" -> ArithmeticOperator.AND;
            case "||" -> ArithmeticOperator.OR;
            case "^" -> ArithmeticOperator.XOR;
            default -> throw new UnsupportedOperationException("Unknown operation '" + ruleContext.op.getText() + "'");
        };
    }

    @Override
    public Optional<Constant> computeConstExpr(Constant[] constants, ArithmeticOperator operation) {
        int result = operation.applyAsInt(constants[0].getVal()[0], constants[1].getVal()[0]);
        return Optional.of(new Constant(PrimitiveType.BOOLEAN, result));
    }

    @Override
    public int generateCombinators(Symbol[] symbols, ArithmeticOperator operation, FactorioSignal[] outSymbol, CombinatorGroup group, FunctionContext context) {
        var connected = ArithmeticCombinator.withLeftRight(symbols[0].toAccessor()[0],  symbols[1].toAccessor()[0], outSymbol[0], operation);
        connected.setGreenIn(group.getInput());
        connected.setGreenOut(group.getOutput());
        group.getCombinators().add(connected);
        return 1;
    }
}
