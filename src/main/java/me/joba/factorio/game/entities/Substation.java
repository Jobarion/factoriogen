package me.joba.factorio.game.entities;

import me.joba.factorio.lang.FactorioSignal;

import java.util.Map;

public class Substation extends CircuitNetworkEntity{

    public Substation() {
        this(0, 0);
    }

    public Substation(int x, int y) {
        super("substation", 1, 19.0);
        setX(x);
        setY(y);
    }

    @Override
    public void gatherSignals() {

    }

    @Override
    public void update() {

    }

    @Override
    public Map<FactorioSignal, Integer> getOutput() {
        return Map.of();
    }
}
