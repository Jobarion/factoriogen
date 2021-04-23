package me.joba.factorio.lang;

import me.joba.factorio.Accessor;
import me.joba.factorio.lang.types.TupleType;
import me.joba.factorio.lang.types.Type;

public abstract class Symbol {

    private FactorioSignal[] signal;
    private final Type type;

    public Symbol(Type type) {
        this.type = type;
    }

    public Symbol(Type type, FactorioSignal... signal) {
        this.type = type;
        bind(signal);
    }

    public void bind(FactorioSignal... factorioSignal) {
        if(signal != null) throw new RuntimeException("Variable already bound");
        if(type.getSize() != factorioSignal.length) throw new IllegalArgumentException("Cannot bind type " + type + " with size " + type.getSize() + " to " + factorioSignal.length + " signals.");
        this.signal = factorioSignal;
    }

    public FactorioSignal[] getSignal() {
        return signal;
    }

    public boolean isBound() {
        return signal != null;
    }

    public Type getType() {
        return type;
    }

    public abstract Accessor[] toAccessor();
    public abstract int getTickDelay();
}
