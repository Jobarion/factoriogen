package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;

public class WhileVariableScope extends VariableScope {

    private final CombinatorGroup preConditionProvider;
    private final CombinatorGroup postConditionProvider;

    public WhileVariableScope(VariableScope parent, CombinatorGroup preConditionProvider, CombinatorGroup postConditionProvider) {
        super(parent);
        this.preConditionProvider = preConditionProvider;
        this.postConditionProvider = postConditionProvider;
        for(var e : parent.getAllVariables().entrySet()) {
            var rebound = this.createNamedVariable(e.getKey(), e.getValue().getType(), e.getValue().getSignal(), preConditionProvider);
            rebound.setDelay(0);
        }
    }

    public CombinatorGroup getPreConditionProvider() {
        return preConditionProvider;
    }

    public CombinatorGroup getPostConditionProvider() {
        return postConditionProvider;
    }

    public void enterLoopBody() {
        assert getDefinedVariables().size() == getAllVariables().size();
        for(var e : getDefinedVariables().entrySet()) {
            var rebound = this.createNamedVariable(e.getKey(), e.getValue().getType(), e.getValue().getSignal(), postConditionProvider);
            rebound.setDelay(0);
        }
    }
}
