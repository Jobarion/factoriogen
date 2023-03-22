package me.joba.factorio.game.entities;

import me.joba.factorio.Main;
import me.joba.factorio.NetworkGroup;
import me.joba.factorio.game.ConnectionPoint;
import me.joba.factorio.game.Entity;
import me.joba.factorio.game.WireColor;
import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONObject;

import java.util.Map;

public abstract class CircuitNetworkEntity extends Entity {

    private final ConnectionPoint[] connectionPoints;
    private JSONObject connectionData;
    private final double maxWireDistance;

    public CircuitNetworkEntity(String name, int connectionPoints, double maxWireDistance) {
        super(name);
        this.maxWireDistance = maxWireDistance;
        this.connectionPoints = new ConnectionPoint[connectionPoints];
        for(int i = 0; i < connectionPoints; i++) {
            this.connectionPoints[i] = new ConnectionPoint(i + 1);
        }
    }

    public double getMaxWireDistance() {
        return maxWireDistance;
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

    public abstract void gatherSignals();
    public abstract void update();
    public abstract Map<FactorioSignal, Integer> getOutput();

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
