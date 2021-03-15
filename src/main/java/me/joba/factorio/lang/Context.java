package me.joba.factorio.lang;

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
        private NetworkGroup expressionNetworkGroup = new NetworkGroup();
        private Set<String> createdVariables = new HashSet<>();
        private Set<FactorioSignal> usedBindings = new HashSet<>();
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
        var var = new Variable(VarType.UNKNOWN, variableIdCounter++);
        tempVariables.push(var);
        return var;
    }

    public Variable createBoundVariable(VarType type, FactorioSignal signal) {
        var var = new Variable(type, variableIdCounter++, signal, getCurrentNetworkGroup());
        tempVariables.push(var);
        return var;
    }

    public Symbol popTempVariable() {
        return tempVariables.pop();
    }

    public Variable createNamedVariable(String name, VarType type) {
        var var =  vars.get(name);
        if(var != null) return var;
        var = new Variable(type, variableIdCounter++);
        vars.put(name, var);
        currentScope.createdVariables.add(name);
        return var;
    }

    public Variable getNamedVariable(String name) {
        var var =  vars.get(name);
        if(var == null) throw new RuntimeException("Variable " + name + " is not defined");
        return var;
    }

    public void startNewContext() {
        currentScope.expressionNetworkGroup = new NetworkGroup();
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
        return currentScope.expressionNetworkGroup;
    }
}
