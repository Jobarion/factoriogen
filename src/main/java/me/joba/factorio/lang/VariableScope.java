package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.lang.types.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class VariableScope {

    private static int variableIdCounter = 0;
    private Map<String, Variable> variables;
    private VariableScope parentScope;
    private boolean sync = true;
//    private final Stack<Symbol> tempVariables = new Stack<>();

    public VariableScope(VariableScope parent) {
        this.parentScope = parent;
        this.variables = new HashMap<>();
    }

    public Map<String, Variable> getDefinedVariables() {
        return variables;
    }

    public void markAsAsync() {
        sync = false;
    }

    public boolean isSync() {
        return sync;
    }

    public Variable getNamedVariable(String name) {
        var v = variables.get(name);
        if(v != null) return v;
        if(parentScope != null) return parentScope.getNamedVariable(name);
        return null;
    }

    public Variable createNamedVariable(String name, Type type, FactorioSignal[] signal, CombinatorGroup producer) {
        var var = new Variable(type, variableIdCounter++, signal, producer);
        variables.put(name, var);
        return var;
    }

//    public void pushTempVariable(Symbol var) {
//        tempVariables.push(var);
//    }
//
//    public Symbol popTempVariable() {
//        return tempVariables.pop();
//    }


    protected VariableScope getParentScope() {
        return parentScope;
    }

    public Map<String, Variable> getAllVariables() {
        Map<String, Variable> result = new HashMap<>();
        var currentScope = this;
        while(currentScope != null) {
            for(var e : currentScope.variables.entrySet()) {
                result.putIfAbsent(e.getKey(), e.getValue());
            }
            currentScope = currentScope.getParentScope();
        }
        return result;
    }
}
