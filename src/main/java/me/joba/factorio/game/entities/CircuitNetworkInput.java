package me.joba.factorio.game.entities;

import me.joba.factorio.NetworkGroup;

public interface CircuitNetworkInput {
    void setRedIn(NetworkGroup red);
    void setGreenIn(NetworkGroup red);
    NetworkGroup getRedIn();
    NetworkGroup getGreenIn();
}
