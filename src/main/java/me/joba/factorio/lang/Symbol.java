package me.joba.factorio.lang;

import me.joba.factorio.Accessor;
import me.joba.factorio.NetworkGroup;

public abstract class Symbol {

    private FactorioSignal signal;

    public Symbol() {

    }

    public Symbol(FactorioSignal signal) {
        this.signal = signal;
    }

    public void bind(FactorioSignal factorioSignal) {
        this.signal = factorioSignal;
    }

    public FactorioSignal getSignal() {
        return signal;
    }

    public boolean isBound() {
        return signal != null;
    }

    public abstract Accessor toAccessor(Context context);
    public abstract VarType getType();
}
