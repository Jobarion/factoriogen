package me.joba.factorio.lang.expr;

import me.joba.factorio.Accessor;
import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.Writer;
import me.joba.factorio.game.entities.ArithmeticCombinator;
import me.joba.factorio.game.entities.ArithmeticOperator;
import me.joba.factorio.game.entities.DeciderCombinator;
import me.joba.factorio.game.entities.DeciderOperator;
import me.joba.factorio.lang.*;
import me.joba.factorio.lang.types.PrimitiveType;

import java.util.Optional;

public class BooleanNotExpressionResolver extends ExpressionResolver<LanguageParser.BoolExprContext, Void> {

    public BooleanNotExpressionResolver() {
        super(PrimitiveType.BOOLEAN, PrimitiveType.BOOLEAN);
    }

    @Override
    public Void getOperation(LanguageParser.BoolExprContext ruleContext) {
        return null;
    }

    @Override
    public Optional<Constant> computeConstExpr(Constant[] constants, Void operation) {
        return Optional.of(new Constant(PrimitiveType.BOOLEAN, constants[0].getVal()[0] == 0 ? 1 : 0));
    }

    @Override
    public int generateCombinators(Symbol[] symbols, Void operation, FactorioSignal[] outSymbol, CombinatorGroup group) {
        var connected = DeciderCombinator.withLeftRight(symbols[0].toAccessor()[0], Accessor.constant(0), Writer.one(outSymbol[0]), DeciderOperator.EQ);
        connected.setGreenIn(group.getInput());
        connected.setGreenOut(group.getOutput());
        group.getCombinators().add(connected);
        return 1;
    }
}
