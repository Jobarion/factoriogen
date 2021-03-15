package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;

import java.util.*;

public class Context {

    private int maxSymbol = 0;
    private int variableIdCounter = 0;

    private Set<FactorioSignal> freeBindings;
    private Map<String, Variable> vars;
    private Stack<Symbol> tempVariables;
    private Stack<ScopedContext> scopeStack;
    private ScopedContext currentScope;

    private class ScopedContext {
        private int symbolIndex = 0;
        private int expressionDepth = 0;
        private NetworkGroup internalExpressionGroup = new NetworkGroup();
        private CombinatorGroup expressionContext;
        private Set<String> createdVariables = new HashSet<>();
        private Set<FactorioSignal> usedBindings = new HashSet<>();

        public ScopedContext() {
            this.expressionContext = new CombinatorGroup(null, new NetworkGroup());
            expressionContext.getNetworks().add(internalExpressionGroup);
        }

        public CombinatorGroup startExpressionContext() {
            this.internalExpressionGroup = new NetworkGroup();
            this.expressionContext = new CombinatorGroup(null, new NetworkGroup());
            expressionContext.getNetworks().add(internalExpressionGroup);
            return expressionContext;
        }
    }

    public Context() {
        tempVariables = new Stack<>();
        currentScope = new ScopedContext();
        vars = new HashMap<>();
        scopeStack = new Stack<>();
        freeBindings = new HashSet<>();
        freeBindings.addAll(Arrays.asList(FactorioSignal.values()));
    }

    public void pushTempVariable(Symbol var) {
        tempVariables.push(var);
    }

    public Variable createTempVariable(VarType type) {
        var var = new AnonymousVariable(VarType.UNKNOWN, variableIdCounter++);
        tempVariables.push(var);
        return var;
    }

    public Variable createBoundVariable(VarType type, FactorioSignal signal) {
        var var = new AnonymousVariable(type, variableIdCounter++, signal, getCurrentNetworkGroup());
        tempVariables.push(var);
        return var;
    }

    public Symbol popTempVariable() {
        return tempVariables.pop();
    }

    public Variable createNamedVariable(String name, VarType type, FactorioSignal signal, CombinatorGroup producer) {
        var var = new NamedVariable(type, variableIdCounter++, signal, producer.getOutput(), producer);
        vars.put(name, var);
        currentScope.createdVariables.add(name);
        return var;
    }

    public Variable getNamedVariable(String name) {
        return vars.get(name);
    }

    public void startExpressionContext() {
        currentScope.startExpressionContext();
    }

    public CombinatorGroup getExpressionContext() {
        return currentScope.expressionContext;
    }

    public void enterScope() {
        scopeStack.push(currentScope);
        currentScope = new ScopedContext();
    }

    public void leaveScope() {
        freeBindings.addAll(currentScope.usedBindings);
        vars.keySet().removeAll(currentScope.createdVariables);
        currentScope = scopeStack.pop();
    }

    public FactorioSignal getFreeSymbol() {
        var signal = freeBindings.iterator().next();
        freeBindings.remove(signal);
        currentScope.usedBindings.add(signal);
        return signal;
    }

    public int getExpressionDepth() {
        return currentScope.expressionDepth;
    }

    public NetworkGroup getCurrentNetworkGroup() {
        return currentScope.internalExpressionGroup;
    }
}
