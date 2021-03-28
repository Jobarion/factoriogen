package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;

import java.util.*;

public class FunctionContext {

    public static final String CONTROL_FLOW_VAR_NAME = "$CONTROL_FLOW";

    private int variableIdCounter = 0;
    private Set<FactorioSignal> freeBindings;
    private Stack<Symbol> tempVariables;
    private Stack<CombinatorContext> combinatorContexts;
    private Stack<VariableScope> variables;
    private Stack<ConditionContext> conditionContexts;
    private final CombinatorGroup functionHeader;
    private final CombinatorGroup functionReturn;

    private class CombinatorContext {
        private NetworkGroup internalExpressionGroup = new NetworkGroup();
        private CombinatorGroup expressionContext;

        public CombinatorContext() {
            this.expressionContext = new CombinatorGroup(internalExpressionGroup, new NetworkGroup());
            expressionContext.getNetworks().add(internalExpressionGroup);
        }

        public CombinatorGroup startExpressionContext() {
            this.internalExpressionGroup = new NetworkGroup();
            this.expressionContext = new CombinatorGroup(internalExpressionGroup, new NetworkGroup());
            return expressionContext;
        }
    }

    public class ConditionContext {

        private VariableScope ifScope;
        private VariableScope elseScope;

        public VariableScope getIfScope() {
            return ifScope;
        }

        public void setIfScope(VariableScope ifScope) {
            this.ifScope = ifScope;
        }

        public Optional<VariableScope> getElseScope() {
            return Optional.ofNullable(elseScope);
        }

        public void setElseScope(VariableScope elseScope) {
            this.elseScope = elseScope;
        }
    }

    public FunctionContext(CombinatorGroup functionHeader) {
        this.functionHeader = functionHeader;
        tempVariables = new Stack<>();
        combinatorContexts = new Stack<>();
        combinatorContexts.push(new CombinatorContext());
        variables = new Stack<>();
        variables.push(new VariableScope(null));
        freeBindings = new HashSet<>();
        freeBindings.addAll(Arrays.asList(FactorioSignal.values()));
        freeBindings.removeIf(FactorioSignal::isReserved);
        conditionContexts = new Stack<>();
        functionReturn = new CombinatorGroup(null, new NetworkGroup());
        functionHeader.getSubGroups().add(functionReturn);
    }

    public Variable getControlFlowVariable() {
        return getNamedVariable(CONTROL_FLOW_VAR_NAME);
    }

    public Variable overwriteControlFlowVariable(CombinatorGroup producer) {
        return createNamedVariable(CONTROL_FLOW_VAR_NAME, VarType.INT, CombinatorUtil.CONTROL_FLOW_SIGNAL, producer);
    }

    public void pushTempVariable(Symbol var) {
        tempVariables.push(var);
    }

    public Variable createBoundTempVariable(VarType type, FactorioSignal signal, CombinatorGroup producer) {
        var var = new Variable(type, variableIdCounter++, signal, producer);
        tempVariables.push(var);
        return var;
    }

    public Symbol popTempVariable() {
        return tempVariables.pop();
    }

    public Variable createNamedVariable(String name, VarType type, FactorioSignal signal, CombinatorGroup producer) {
        return variables.peek().createNamedVariable(name, type, signal, producer);
    }

    public Variable getNamedVariable(String name) {
        return variables.peek().getNamedVariable(name);
    }

    public void startExpressionContext() {
        combinatorContexts.peek().startExpressionContext();
    }

//    public CombinatorGroup getExpressionContext() {
//        return combinatorContexts.peek().expressionContext;
//    }

    public void enterLoop() {
        variables.push(new WhileVariableScope(variables.peek()));
    }

    public VariableScope getVariableScope() {
        return variables.peek();
    }

    public void leaveLoop() {
        variables.pop();
    }

    public void enterIfStatement() {
        var vscope = new VariableScope(variables.peek());
        variables.push(vscope);
        conditionContexts.peek().setIfScope(vscope);
    }

    public void enterElseStatement() {
        variables.pop(); //The if scope
        var vscope = new VariableScope(variables.peek());
        variables.push(vscope);
        conditionContexts.peek().setElseScope(vscope);
    }

    public void enterConditional() {
        conditionContexts.push(new ConditionContext());
    }

    public void leaveConditional() {
        variables.pop(); //the last if/else/elseif block
        conditionContexts.pop();
    }

    public ConditionContext getConditionContext() {
        return conditionContexts.peek();
    }

    public void enterScope() {
        combinatorContexts.push(new CombinatorContext());
    }

    public void leaveScope() {
        combinatorContexts.pop();
    }

    public FactorioSignal getFreeSymbol() {
        var signal = freeBindings.iterator().next();
        freeBindings.remove(signal);
        return signal;
    }

    public void claimSymbol(FactorioSignal signal) {
        if(!freeBindings.remove(signal)) {
            throw new IllegalArgumentException("Attempted to claim unavailable symbol " + signal);
        }
    }

//    public NetworkGroup getCurrentNetworkGroup() {
//        return combinatorContexts.peek().internalExpressionGroup;
//    }

    public CombinatorGroup getFunctionGroup() {
        return functionHeader;
    }

    public CombinatorGroup getFunctionReturnGroup() {
        return functionReturn;
    }
}
