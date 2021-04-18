package me.joba.factorio.game.entities;

import me.joba.factorio.NetworkGroup;
import me.joba.factorio.game.ConnectionPoint;
import me.joba.factorio.game.Entity;
import me.joba.factorio.game.WireColor;
import org.json.simple.JSONObject;

public class CircuitNetworkEntity extends Entity {

    private final ConnectionPoint[] connectionPoints;
    private JSONObject connectionData;

    public CircuitNetworkEntity(String name, int connectionPoints) {
        super(name);
        this.connectionPoints = new ConnectionPoint[connectionPoints];
        for(int i = 0; i < connectionPoints; i++) {
            this.connectionPoints[i] = new ConnectionPoint(i + 1);
        }
    }

    public ConnectionPoint[] getConnectionPoints() {
        return connectionPoints;
    }

    public NetworkGroup getNetwork(int connectionPoint, WireColor color) {
        return connectionPoints[connectionPoint].getConnections().get(color);
    }

    public void setNetwork(int connectionPoint, WireColor color, NetworkGroup group) {
        connectionPoints[connectionPoint].getConnections().put(color, group);
    }

    public void setConnectionData(JSONObject connectionData) {
        this.connectionData = connectionData;
    }

    @Override
    protected void extendJson(JSONObject json) {
        super.extendJson(json);
        if(connectionData != null) {
            json.put("connections", connectionData);
        }
    }
}
