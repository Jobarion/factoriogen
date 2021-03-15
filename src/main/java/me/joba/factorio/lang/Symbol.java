package me.joba.factorio.lang;

import me.joba.factorio.Accessor;
import me.joba.factorio.NetworkGroup;

public abstract class Symbol {

    private FactorioSignal signal;
    private NetworkGroup outputGroup;

    public Symbol() {

    }

    public Symbol(FactorioSignal signal, NetworkGroup outputGroup) {
        this.signal = signal;
        this.outputGroup = outputGroup;
    }

    public void bind(FactorioSignal factorioSignal, NetworkGroup networkGroup) {
        this.signal = factorioSignal;
        this.outputGroup = networkGroup;
    }

    public NetworkGroup getOutputGroup() {
        return outputGroup;
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
