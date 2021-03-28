package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Arrays;

public abstract class Combiner<RC extends ParserRuleContext, OP> {

    private final int requiredValues;
    private final VarType inputType;
    private final VarType outputType;

    protected Combiner(int requiredValues, VarType inputType, VarType outputType) {
        this.requiredValues = requiredValues;
        this.inputType = inputType;
        this.outputType = outputType;
    }

    public void parse(FunctionContext context, RC ruleContext) {
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
//        maxInputDelay++;

        var outSymbol = context.getFreeSymbol();
        var outputContext = new CombinatorGroup(new NetworkGroup(), new NetworkGroup());
        context.getFunctionGroup().getSubGroups().add(outputContext);
        var bound = context.createBoundTempVariable(outputType, outSymbol, outputContext);

        for(var s : symbols) {
            if(s instanceof Variable) {
                var accessor = ((Variable)s).createVariableAccessor();
                accessor.access(maxInputDelay).accept(outputContext);
                outputContext.getAccessors().add(accessor);
            }
        }

        int propagationDelay = generateCombinators(symbols, op, outSymbol, outputContext);
        bound.setDelay(maxInputDelay + propagationDelay);

        System.out.println(Arrays.toString(symbols) + " (using " + op + ") = " + bound + ", with delay " + bound.getTickDelay());
    }

    public abstract OP getOperation(RC ruleContext);
    public abstract Constant computeConstExpr(Constant[] constants, OP operation);
    public abstract int generateCombinators(Symbol[] symbols, OP operation, FactorioSignal outSymbol, CombinatorGroup group);
}
