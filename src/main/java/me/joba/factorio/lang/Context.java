package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;

import java.util.*;

public class Context {

    private int variableIdCounter = 0;
    private Set<FactorioSignal> freeBindings;
    private Map<String, Variable> vars;
    private Stack<Symbol> tempVariables;
    private Stack<ScopedContext> scopeStack;
    private ScopedContext currentScope;
    private Stack<ConditionContext> conditionContexts;
    private ConditionContext conditionContext;

    private class ScopedContext {
        private NetworkGroup internalExpressionGroup = new NetworkGroup();
        private CombinatorGroup expressionContext;
        private Set<String> createdVariables = new HashSet<>();
        private Set<FactorioSignal> usedBindings = new HashSet<>();

        public ScopedContext() {
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

        private final Map<String, Variable> originalBindings = new HashMap<>();
        private final Map<String, Variable> assignedIf = new HashMap<>();
        private final Map<String, Variable> assignedElse = new HashMap<>();
        private boolean isInElse = false;

        private Map<String, Variable> active = assignedIf;

        public Map<String, Variable> getAssignedIf() {
            return assignedIf;
        }

        public Map<String, Variable> getAssignedElse() {
            return assignedElse;
        }

        public Map<String, Variable> getOriginalBindings() {
            return originalBindings;
        }

        public void enterElse() {
            active = assignedElse;
            isInElse = true;
        }

        public boolean isInElse() {
            return isInElse;
        }

        public void registerAssignment(String name, Variable var) {
            active.put(name, var);
        }
    }

    public Context() {
        tempVariables = new Stack<>();
        currentScope = new ScopedContext();
        vars = new HashMap<>();
        scopeStack = new Stack<>();
        freeBindings = new HashSet<>();
        freeBindings.addAll(Arrays.asList(FactorioSignal.values()));
        conditionContexts = new Stack<>();
        conditionContext = new ConditionContext();
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
        var var = new AnonymousVariable(type, variableIdCounter++, signal);
        tempVariables.push(var);
        return var;
    }

    public Symbol popTempVariable() {
        return tempVariables.pop();
    }

    public Variable createNamedVariable(String name, VarType type, FactorioSignal signal, CombinatorGroup producer) {
        var var = new NamedVariable(type, variableIdCounter++, signal, producer);
        var previous = vars.put(name, var);
        if(previous == null) {
            currentScope.createdVariables.add(name);
        }
        else {
            conditionContext.originalBindings.putIfAbsent(name, previous);
        }
        return var;
    }

    public Variable getNamedVariable(String name) {
        if(!conditionContext.isInElse) return vars.get(name);

        var elseOverride = conditionContext.getAssignedElse().get(name);
        //If we're in an else block and the variable was overwritten in that else block, use it
        if(elseOverride != null) return elseOverride;

        //If it was overwritten in the if block, use the original one
        if(conditionContext.getAssignedIf().get(name) != null) {
            return conditionContext.getOriginalBindings().get(name);
        }

        //It wasn't overwritten, use the original one
        return vars.get(name);
    }

    public void startExpressionContext() {
        currentScope.startExpressionContext();
    }

    public CombinatorGroup getExpressionContext() {
        return currentScope.expressionContext;
    }

    public void enterIf() {
        conditionContexts.push(conditionContext);
        conditionContext = new ConditionContext();
    }

    public void leaveIf() {
        conditionContext = conditionContexts.pop();
    }

    public ConditionContext getConditionContext() {
        return conditionContext;
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

    public NetworkGroup getCurrentNetworkGroup() {
        return currentScope.internalExpressionGroup;
    }
}
