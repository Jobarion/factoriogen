package me.joba.factorio.game.entities;

import me.joba.factorio.lang.FactorioSignal;

import java.util.Map;

public class LargePowerPole extends CircuitNetworkEntity{

    public LargePowerPole() {
        this(0, 0);
    }

    public LargePowerPole(int x, int y) {
        super("big-electric-pole", 1, 200);
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
