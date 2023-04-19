package me.joba.factorio.lang;

import me.joba.factorio.*;

public class NamedVariable extends Variable {

    private final CombinatorGroup producer;
    private final VariableAccessor accessor;

    public NamedVariable(VarType type, int id, FactorioSignal signal, CombinatorGroup producer) {
        super(type, id, signal);
        this.producer = producer;
        this.accessor = new VariableAccessor(this);
    }

    public CombinatorGroup getProducer() {
        return producer;
    }

    @Override
    public VariableAccessor createVariableAccessor() {
        return accessor;
    }
}
