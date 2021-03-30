package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;

import java.util.*;

public class FunctionContext {

    public static final String CONTROL_FLOW_VAR_NAME = "$CONTROL_FLOW";

    private int variableIdCounter = 0;
    private Set<FactorioSignal> freeBindings;
    private Stack<Symbol> tempVariables;
    private Stack<VariableScope> variables;
    private Stack<ConditionContext> conditionContexts;
    private final CombinatorGroup functionHeader;
    private final CombinatorGroup functionReturn;

    public class ConditionContext {

        private final VariableScope ifScope;
        private final VariableScope elseScope;
        private final CombinatorGroup ifProvider;
        private final CombinatorGroup elseProvider;


        public ConditionContext(VariableScope ifScope, VariableScope elseScope, CombinatorGroup ifProvider, CombinatorGroup elseProvider) {
            this.ifScope = ifScope;
            this.elseScope = elseScope;
            this.ifProvider = ifProvider;
            this.elseProvider = elseProvider;
        }

        public VariableScope getIfScope() {
            return ifScope;
        }
        public VariableScope getElseScope() {
            return elseScope;
        }

        public CombinatorGroup getIfProvider() {
            return ifProvider;
        }

        public CombinatorGroup getElseProvider() {
            return elseProvider;
        }
    }

    public FunctionContext(CombinatorGroup functionHeader) {
        this.functionHeader = functionHeader;
        tempVariables = new Stack<>();
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
        return createNamedVariable(CONTROL_FLOW_VAR_NAME, VarType.INT, Constants.CONTROL_FLOW_SIGNAL, producer);
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

    public void enterLoop() {
        var preConditionGroup = new CombinatorGroup(null, new NetworkGroup("while pre condition out"));
        var postConditionGroup = new CombinatorGroup(null, new NetworkGroup("while post condition out"));
        variables.push(new WhileVariableScope(variables.peek(), preConditionGroup, postConditionGroup));
    }

    public int getDepth() {
        return variables.size();
    }

    public VariableScope getVariableScope() {
        return variables.peek();
    }

    public void leaveLoop() {
        variables.pop();
    }

    public void enterIfStatement() {
        variables.push(conditionContexts.peek().getIfScope());
    }

    public void enterElseStatement() {
        variables.pop(); //The if scope
        variables.push(conditionContexts.peek().getElseScope());
    }

    public void enterConditional() {
        VariableScope ifScope = new VariableScope(getVariableScope());
        VariableScope elseScope = new VariableScope(getVariableScope());

        CombinatorGroup ifVarProvider = new CombinatorGroup(null, new NetworkGroup("if var supply"));
        CombinatorGroup elseVarProvider = new CombinatorGroup(null, new NetworkGroup("else var supply"));

        //Make all currently existing variables available to if and else scopes
        for(var e : getVariableScope().getAllVariables().entrySet()) {
            ifScope.createNamedVariable(e.getKey(), e.getValue().getType(), e.getValue().getSignal(), ifVarProvider).setDelay(0);
            elseScope.createNamedVariable(e.getKey(), e.getValue().getType(), e.getValue().getSignal(), elseVarProvider).setDelay(0);
        }

        conditionContexts.push(new ConditionContext(ifScope, elseScope, ifVarProvider, elseVarProvider));
    }

    public ConditionContext leaveConditional() {
        variables.pop(); //the last if/else/elseif block
        return conditionContexts.pop();
    }

    public ConditionContext getConditionContext() {
        return conditionContexts.peek();
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
