package me.joba.factorio.lang;

import me.joba.factorio.*;

public class AnonymousVariable extends Variable {

    public AnonymousVariable(VarType type, int id, FactorioSignal signal, NetworkGroup networkGroup) {
        super(type, id, signal, networkGroup);
    }

    public AnonymousVariable(VarType type, int id) {
        super(type, id);
    }

    @Override
    public ConnectedCombinator createVariableAccessor() {
        throw new UnsupportedOperationException("Shouldn't happen");
    }
}
