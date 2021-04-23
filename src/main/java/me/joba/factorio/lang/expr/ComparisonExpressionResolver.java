package me.joba.factorio.lang.expr;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.Writer;
import me.joba.factorio.game.entities.ArithmeticCombinator;
import me.joba.factorio.game.entities.ArithmeticOperator;
import me.joba.factorio.game.entities.DeciderCombinator;
import me.joba.factorio.game.entities.DeciderOperator;
import me.joba.factorio.lang.*;
import me.joba.factorio.lang.types.PrimitiveType;

import java.util.Optional;

public class ComparisonExpressionResolver extends ExpressionResolver<LanguageParser.BoolExprContext, DeciderOperator> {

    public ComparisonExpressionResolver() {
        super(PrimitiveType.INT, PrimitiveType.INT, PrimitiveType.BOOLEAN);
    }

    @Override
    public DeciderOperator getOperation(LanguageParser.BoolExprContext ruleContext) {
        return DeciderOperator.getOperator(ruleContext.op.getText());
    }

    @Override
    public Optional<Constant> computeConstExpr(Constant[] constants, DeciderOperator operation) {
        boolean result = operation.test(constants[0].getVal()[0], constants[1].getVal()[0]);
        return Optional.of(new Constant(PrimitiveType.BOOLEAN, result ? 1 : 0));
    }

    @Override
    public int generateCombinators(Symbol[] symbols, DeciderOperator operation, FactorioSignal[] outSymbol, CombinatorGroup group) {
        if(symbols[0] instanceof Constant) {
            var tmp = symbols[1];
            symbols[1] = symbols[0];
            symbols[0] = tmp;
            operation = operation.getInverted();
        }
        var connected = DeciderCombinator.withLeftRight(symbols[0].toAccessor()[0],  symbols[1].toAccessor()[0], Writer.one(outSymbol[0]), operation);
        connected.setGreenIn(group.getInput());
        connected.setGreenOut(group.getOutput());
        group.getCombinators().add(connected);
        return 1;
    }
}
