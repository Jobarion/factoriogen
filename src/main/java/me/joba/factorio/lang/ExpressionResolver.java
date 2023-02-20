package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;
import me.joba.factorio.lang.types.Type;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Arrays;
import java.util.Optional;

public abstract class ExpressionResolver<RC extends ParserRuleContext, OP> {

    protected final Type[] inputTypes;
    protected final Type outputType;

    protected ExpressionResolver(Type outputType, Type... inputTypes) {
        this.inputTypes = inputTypes;
        this.outputType = outputType;
    }

    public void parse(FunctionContext context, RC ruleContext) {
        var op = getOperation(ruleContext);
        Symbol[] symbols = new Symbol[inputTypes.length];
        boolean isConstant = true;
        for(int i = 0; i < inputTypes.length; i++) {
            var tmp = context.popTempVariable();
            if(!tmp.getType().equals(inputTypes[i])) throw new RuntimeException(tmp + " is not of type " + inputTypes[i]);
            symbols[symbols.length - 1 - i] = tmp;
            isConstant = isConstant && tmp instanceof Constant;
        }
        if(isConstant) {
            Constant[] consts = new Constant[symbols.length];
            System.arraycopy(symbols, 0, consts, 0, consts.length);
            var computedConstantOpt = computeConstExpr(consts, op);
            if(computedConstantOpt.isPresent()) {
                var computedConstant = computedConstantOpt.get();
                log(context, "Collapsed constants " + Arrays.toString(symbols) + " (using " + op + ") = " + computedConstant);
                context.pushTempVariable(computedConstant);
                return;
            }
        }
        int maxInputDelay = -1;
        for(Symbol s : symbols) {
            maxInputDelay = Math.max(maxInputDelay, s.getTickDelay());
            if(!s.isBound()) {
                s.bind(context.getFreeSymbol());
            }
        }

        var outSymbol = context.getFreeSymbols(outputType.getSize());
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

        int propagationDelay = generateCombinators(symbols, op, outSymbol, outputContext, context);
        bound.setDelay(maxInputDelay + propagationDelay);

        log(context, Arrays.toString(symbols) + " (using " + op + ") = " + bound + ", with delay " + bound.getTickDelay());
    }

    protected void log(FunctionContext context, String msg) {
        System.out.println("\t".repeat(context.getDepth()) + msg);
    }

    public abstract OP getOperation(RC ruleContext);
    public abstract Optional<Constant> computeConstExpr(Constant[] constants, OP operation);
    public abstract int generateCombinators(Symbol[] symbols, OP operation, FactorioSignal[] outSymbol, CombinatorGroup group, FunctionContext context);
}
