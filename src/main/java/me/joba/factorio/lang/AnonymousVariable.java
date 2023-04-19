package me.joba.factorio.lang;

public class AnonymousVariable extends Variable {

    public AnonymousVariable(VarType type, int id, FactorioSignal signal) {
        super(type, id, signal);
    }

    public AnonymousVariable(VarType type, int id) {
        super(type, id);
    }

    @Override
    public VariableAccessor createVariableAccessor() {
        throw new UnsupportedOperationException("Shouldn't happen");
    }
}
