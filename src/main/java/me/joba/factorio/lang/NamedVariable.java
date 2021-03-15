package me.joba.factorio.lang;

import me.joba.factorio.*;

public class NamedVariable extends Variable {

    private final CombinatorGroup producer;

    public NamedVariable(VarType type, int id, FactorioSignal signal, NetworkGroup networkGroup, CombinatorGroup producer) {
        super(type, id, signal, networkGroup);
        this.producer = producer;
    }

    @Override
    public ConnectedCombinator createVariableAccessor() {
        var cmb = ArithmeticCombinator.withLeftRight(Accessor.signal(getSignal().ordinal()), Accessor.constant(0), getSignal().ordinal(), ArithmeticCombinator.ADD);
        var connected = new ConnectedCombinator(cmb);
        connected.setGreenIn(producer.getOutput());
        return connected;
    }
}
