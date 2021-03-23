
package me.joba.factorio.lang;

import me.joba.factorio.Accessor;

public abstract class Variable extends Symbol {

    private final int id;
    private VarType type;
    private int delay = -1;

    public Variable(VarType type, int id) {
        this.type = type;
        this.id = id;
    }

    public Variable(VarType type, int id, FactorioSignal signal) {
        super(signal);
        this.type = type;
        this.id = id;
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
    public Accessor toAccessor(Context context) {
        if(getSignal() == null) throw new UnsupportedOperationException("Variable not bound");
        return Accessor.signal(getSignal().ordinal());
    }

    @Override
    public int getTickDelay() {
//        if(delay == -1)
//            throw new IllegalArgumentException("Delay not set");
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
    public abstract VariableAccessor createVariableAccessor();
}
