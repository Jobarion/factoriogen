package me.joba.factorio.game.entities;

public class Substation extends CircuitNetworkEntity{

    public Substation() {
        this(0, 0);
    }

    public Substation(int x, int y) {
        super("substation", 1, 19.0);
        setX(x);
        setY(y);
    }
}
