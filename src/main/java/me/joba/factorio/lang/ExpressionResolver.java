package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;
import me.joba.factorio.game.entities.ArithmeticCombinator;
import me.joba.factorio.lang.types.Type;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

        var outSymbol = context.getFreeSignals(outputType.getSize());
        var outputContext = new CombinatorGroup(new NetworkGroup(), new NetworkGroup());
        context.getFunctionGroup().getSubGroups().add(outputContext);
        var bound = context.createBoundTempVariable(outputType, outSymbol, outputContext);

        //TODO deal with merging symbols that have the same signal (a * a, or, even worse, a() * a())

        Map<FactorioSignal, Integer> signalUseCount = new HashMap<>();
        for(Symbol s : symbols) {
            if(!s.isBound()) {
                s.bind(context.getFreeSignals(s.getType().getSize()));
            }
            for(var signal : s.getSignal()) {
                signalUseCount.compute(signal, (k, v) -> v == null ? 1 : v + 1);
            }
        }
        Map<Symbol, Boolean> requiresRemapping = new HashMap<>();
        int maxInputDelay = -1;
        for(Symbol s : symbols) {
            boolean clashes = false;
            for(FactorioSignal signal : s.getSignal()) {
                if(signalUseCount.get(signal) > 1) {
                    clashes = true;
                    break;
                }
            }
            requiresRemapping.put(s, clashes);
            if(!clashes) {
                maxInputDelay = Math.max(maxInputDelay, s.getTickDelay());
            }
            else {
                maxInputDelay = Math.max(maxInputDelay, s.getTickDelay() + 1);
            }
        }
        for(int i = 0; i < symbols.length; i++) {
            if(symbols[i] instanceof Variable v) {
                if(requiresRemapping.get(v)) {
                    CombinatorGroup remappedProducer = new CombinatorGroup(new NetworkGroup(), outputContext.getInput());
                    outputContext.getSubGroups().add(remappedProducer);
                    var accessor = v.createVariableAccessor();
                    accessor.access(maxInputDelay - 1).accept(remappedProducer);
                    outputContext.getAccessors().add(accessor);
                    var newSignals = context.getFreeSignals(v.getType().getSize());
                    for(int j = 0; j < v.getType().getSize(); j++) {
                        var remapping = ArithmeticCombinator.remapping(v.getSignal()[j], newSignals[j]);
                        remapping.setGreenIn(remappedProducer.getInput());
                        remapping.setGreenOut(remappedProducer.getOutput());
                        remappedProducer.getCombinators().add(remapping);
                    }
                    symbols[i] = context.createBoundTempVariable(v.getType(), newSignals, remappedProducer);
                    context.popTempVariable();
                }
                else {
                    var accessor = v.createVariableAccessor();
                    accessor.access(maxInputDelay).accept(outputContext);
                    outputContext.getAccessors().add(accessor);
                }
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
