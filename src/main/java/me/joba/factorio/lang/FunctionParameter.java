package me.joba.factorio.lang;

import me.joba.factorio.lang.types.Type;

public class FunctionParameter {

    private final String name;
    private final Type type;
    private FactorioSignal[] signal;

    public FunctionParameter(String name, Type type, FactorioSignal[] signal) {
        this.name = name;
        this.type = type;
        this.signal = signal;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public FactorioSignal[] getSignal() {
        return signal;
    }

    public void setSignal(FactorioSignal[] signal) {
        this.signal = signal;
    }
}
