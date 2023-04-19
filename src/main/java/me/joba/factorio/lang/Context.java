package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;

import java.util.*;

public class Context {

    private int variableIdCounter = 0;
    private Set<FactorioSignal> freeBindings;
    private Stack<Symbol> tempVariables;
    private Stack<CombinatorContext> combinatorContexts;
    private Stack<VariableScope> variables;
    private Stack<ConditionContext> conditionContexts;

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

    public Context() {
        tempVariables = new Stack<>();
        combinatorContexts = new Stack<>();
        combinatorContexts.push(new CombinatorContext());
        variables = new Stack<>();
        variables.push(new VariableScope(null));
        freeBindings = new HashSet<>();
        freeBindings.addAll(Arrays.asList(FactorioSignal.values()));
        conditionContexts = new Stack<>();
    }

    public void pushTempVariable(Symbol var) {
        tempVariables.push(var);
    }

    public Variable createBoundTempVariable(VarType type, FactorioSignal signal) {
        var var = new AnonymousVariable(type, variableIdCounter++, signal);
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

    public CombinatorGroup getExpressionContext() {
        return combinatorContexts.peek().expressionContext;
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

    public NetworkGroup getCurrentNetworkGroup() {
        return combinatorContexts.peek().internalExpressionGroup;
    }
}