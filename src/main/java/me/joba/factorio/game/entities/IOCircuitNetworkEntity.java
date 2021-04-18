package me.joba.factorio.game.entities;

import me.joba.factorio.NetworkGroup;
import me.joba.factorio.game.WireColor;

public class IOCircuitNetworkEntity extends CircuitNetworkEntity {

    public IOCircuitNetworkEntity(String name) {
        super(name, 2);
    }

    //Temporary to make transition easier
    public void setRedIn(NetworkGroup red) {
        setNetwork(0, WireColor.RED, red);
    }

    public void setGreenIn(NetworkGroup green) {
        setNetwork(0, WireColor.GREEN, green);
    }

    public void setRedOut(NetworkGroup red) {
        setNetwork(1, WireColor.RED, red);
    }

    public void setGreenOut(NetworkGroup green) {
        setNetwork(1, WireColor.GREEN, green);
    }

    public NetworkGroup getRedIn() {
        return getNetwork(0, WireColor.RED);
    }

    public NetworkGroup getGreenIn() {
        return getNetwork(0, WireColor.GREEN);
    }

    public NetworkGroup getRedOut() {
        return getNetwork(1, WireColor.RED);
    }

    public NetworkGroup getGreenOut() {
        return getNetwork(1, WireColor.GREEN);
    }
}
