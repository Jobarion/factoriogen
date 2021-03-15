
package me.joba.factorio.lang;

import me.joba.factorio.Accessor;
import me.joba.factorio.NetworkGroup;

public abstract class Variable extends Symbol {

    private final int id;
    private VarType type;

    public Variable(VarType type, int id) {
        this.type = type;
        this.id = id;
    }

    public Variable(VarType type, int id, FactorioSignal signal, NetworkGroup networkGroup) {
        super(signal, networkGroup);
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

    public abstract NetworkGroup createVariableAccessor();
}
