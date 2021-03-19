package me.joba.factorio.lang;

import me.joba.factorio.ArithmeticCombinator;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Arrays;

public abstract class Combiner<RC extends ParserRuleContext, OP> {

    private final Context context;
    private final int requiredValues;
    private final VarType inputType;
    private final VarType outputType;

    protected Combiner(Context context, int requiredValues, VarType inputType, VarType outputType) {
        this.context = context;
        this.requiredValues = requiredValues;
        this.inputType = inputType;
        this.outputType = outputType;
    }

    public void parse(RC ruleContext) {
        var op = getOperation(ruleContext);
        Symbol[] symbols = new Symbol[requiredValues];
        boolean isConstant = true;
        for(int i = 0; i < requiredValues; i++) {
            var tmp = context.popTempVariable();
            if(tmp.getType() != inputType) throw new RuntimeException(tmp + " is not of type " + inputType);
            symbols[symbols.length - 1 - i] = tmp;
            isConstant = isConstant && tmp instanceof Constant;
        }
        if(isConstant) {
            Constant[] consts = new Constant[symbols.length];
            System.arraycopy(symbols, 0, consts, 0, consts.length);
            var computedConstant = computeConstExpr(consts, op);
            System.out.println("Collapsed constants " + Arrays.toString(symbols) + " (using " + op + ") = " + computedConstant);
            context.pushTempVariable(computedConstant);
            return;
        }
        int maxInputDelay = -1;
        for(Symbol s : symbols) {
            maxInputDelay = Math.max(maxInputDelay, s.getTickDelay());
            if(!s.isBound()) {
                s.bind(context.getFreeSymbol());
            }
        }

        var outSymbol = context.getFreeSymbol();
        var bound = context.createBoundVariable(outputType, outSymbol);
        bound.setDelay(maxInputDelay + 1);

        System.out.println(Arrays.toString(symbols) + " (using " + op + ") = " + bound + ", with delay " + bound.getTickDelay());

        for(var s : symbols) {
            if(s instanceof NamedVariable) {
                var accessor = ((Variable)s).createVariableAccessor();
                accessor.access(bound.getTickDelay()).accept(context.getExpressionContext());
                context.getExpressionContext().getAccessors().add(accessor);
            }
        }

        generateCombinators(symbols, op, outSymbol);
    }

    public abstract OP getOperation(RC ruleContext);
    public abstract Constant computeConstExpr(Constant[] constants, OP operation);
    public abstract void generateCombinators(Symbol[] symbols, OP operation, FactorioSignal outSymbol);
}
