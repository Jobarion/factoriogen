package me.joba.factorio.game.entities;

import me.joba.factorio.NetworkGroup;

public interface CircuitNetworkOutput {
    void setRedOut(NetworkGroup red);
    void setGreenOut(NetworkGroup red);
    NetworkGroup getRedOut();
    NetworkGroup getGreenOut();
}
