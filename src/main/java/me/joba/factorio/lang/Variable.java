
package me.joba.factorio.lang;

import me.joba.factorio.Accessor;
import me.joba.factorio.CombinatorGroup;

public class Variable extends Symbol {

    private final CombinatorGroup producer;
    private final VariableAccessor accessor;
    private final int id;
    private VarType type;
    private int delay = -1;

    public Variable(VarType type, int id, CombinatorGroup producer) {
        this.type = type;
        this.id = id;
        this.producer = producer;
        this.accessor = new VariableAccessor(this);
    }

    public Variable(VarType type, int id, FactorioSignal signal, CombinatorGroup producer) {
        super(signal);
        this.type = type;
        this.id = id;
        this.producer = producer;
        this.accessor = new VariableAccessor(this);
    }

    @Override
    public VarType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return !isBound() ? "Var(" + id + ")" : "Var(" + getSignal().name() + ", " + id + ")";
    }

    @Override
    public Accessor toAccessor(FunctionContext context) {
        if(getSignal() == null) throw new UnsupportedOperationException("Variable not bound");
        return Accessor.signal(getSignal());
    }

    @Override
    public int getTickDelay() {
        if(delay == -1)
            throw new IllegalArgumentException("Delay not set");
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }


    public CombinatorGroup getProducer() {
        return producer;
    }

    public VariableAccessor createVariableAccessor() {
        return accessor;
    }
}
