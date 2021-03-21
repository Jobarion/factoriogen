package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;

import java.util.HashMap;
import java.util.Map;

public class WhileVariableScope extends VariableScope {

    private Map<String, NamedVariable> accessedOutside = new HashMap<>();
    private CombinatorGroup variableProviderGroup;
    private CombinatorGroup conditionProviderGroup;
    private boolean isInLoopBody = false;

    public WhileVariableScope(VariableScope parent) {
        super(parent);
        this.variableProviderGroup = new CombinatorGroup(new NetworkGroup(), new NetworkGroup());
        this.conditionProviderGroup = new CombinatorGroup(variableProviderGroup.getInput(), new NetworkGroup());
    }

    public void enterLoopBody() {
        isInLoopBody = true;
    }

    @Override
    public NamedVariable getNamedVariable(String name) {
        var parentVar = getParentScope().getNamedVariable(name);
        if(parentVar == null) {
            return super.getNamedVariable(name);
        }
        else {
            var selfDefined = getDefinedVariables().get(name);
            //Shadow the outside variable with the while loop accessor
            var potentialProducer = isInLoopBody ? variableProviderGroup : conditionProviderGroup;
            if(selfDefined == null || selfDefined.getProducer() != potentialProducer) {
                selfDefined = createNamedVariable(name, parentVar.getType(), parentVar.getSignal(), potentialProducer);
                selfDefined.setDelay(0);
                accessedOutside.put(name, selfDefined);
            }
            return selfDefined;
        }
    }

    @Override
    public NamedVariable createNamedVariable(String name, VarType type, FactorioSignal signal, CombinatorGroup producer) {
        var created = super.createNamedVariable(name, type, signal, producer);
        if(accessedOutside.containsKey(name)) {
            accessedOutside.put(name, created);
        }
        return created;
    }

    public Map<String, NamedVariable> getAccessedOutside() {
        return accessedOutside;
    }

    public CombinatorGroup getVariableProviderGroup() {
        return variableProviderGroup;
    }

    public CombinatorGroup getConditionVariableProviderGroup() {
        return conditionProviderGroup;
    }
}
