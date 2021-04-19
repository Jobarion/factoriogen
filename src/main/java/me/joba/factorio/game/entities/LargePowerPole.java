package me.joba.factorio.game.entities;

public class LargePowerPole extends CircuitNetworkEntity{

    public LargePowerPole() {
        this(0, 0);
    }

    public LargePowerPole(int x, int y) {
        super("big-electric-pole", 1, 200);
        setX(x);
        setY(y);
    }
}
