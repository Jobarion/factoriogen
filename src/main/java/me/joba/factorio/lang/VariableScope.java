package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;

import javax.naming.Name;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VariableScope {

    private static int variableIdCounter = 0;
    private Map<String, Variable> variables;
    private VariableScope parentScope;

    public VariableScope(VariableScope parent) {
        this.parentScope = parent;
        this.variables = new HashMap<>();
    }

    public Map<String, Variable> getDefinedVariables() {
        return variables;
    }

    public Variable getNamedVariable(String name) {
        var v = variables.get(name);
        if(v != null) return v;
        if(parentScope != null) return parentScope.getNamedVariable(name);
        return null;
    }

    public Variable createNamedVariable(String name, VarType type, FactorioSignal signal, CombinatorGroup producer) {
        var var = new Variable(type, variableIdCounter++, signal, producer);
        variables.put(name, var);
        return var;
    }

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
