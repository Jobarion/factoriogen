package me.joba.factorio.game.entities;

public class Substation extends CircuitNetworkEntity{

    public Substation() {
        super("substation", 1);
    }

    public Substation(int x, int y) {
        super("substation", 1);
        setX(x);
        setY(y);
    }
}
