
package me.joba.factorio.lang;

import me.joba.factorio.Accessor;
import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.lang.types.Type;

import java.util.Arrays;

public class Variable extends Symbol {

    private final CombinatorGroup producer;
    private final VariableAccessor accessor;
    private final int id;
    private int delay = -1;

    public Variable(Type type, int id, CombinatorGroup producer) {
        super(type);
        this.id = id;
        this.producer = producer;
        this.accessor = new VariableAccessor(this);
    }

    public Variable(Type type, int id, FactorioSignal[] signal, CombinatorGroup producer) {
        super(type, signal);
        this.id = id;
        this.producer = producer;
        this.accessor = new VariableAccessor(this);
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return !isBound() ? "Var(" + id + ")" : "Var(" + Arrays.toString(getSignal()) + ", " + id + ")";
    }

    @Override
    public Accessor[] toAccessor() {
        if(getSignal() == null) throw new UnsupportedOperationException("Variable not bound");
        var accessors = new Accessor[getType().getSize()];
        var signals = getSignal();
        for(int i = 0; i < accessors.length; i++) {
            accessors[i] = Accessor.signal(signals[i]);
        }
        return accessors;
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
