package me.joba.factorio.game;

import me.joba.factorio.NetworkGroup;
import me.joba.factorio.game.WireColor;

import java.util.HashMap;
import java.util.Map;

public class ConnectionPoint {

    private final int id;
    private final Map<WireColor, NetworkGroup> connections = new HashMap<>();

    public ConnectionPoint(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Map<WireColor, NetworkGroup> getConnections() {
        return connections;
    }
}
