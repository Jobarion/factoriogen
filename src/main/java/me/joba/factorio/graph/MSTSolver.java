package me.joba.factorio.graph;

import me.joba.factorio.NetworkGroup;
import me.joba.factorio.game.WireColor;
import me.joba.factorio.game.entities.CircuitNetworkEntity;

import java.util.*;

public class MSTSolver {

    public static List<Tuple<Node, Node>> solveMst(Collection<CircuitNetworkEntity> entities) {
        Map<NetworkGroup, List<Node>> networkMap = new HashMap<>();

        for(var entity : entities) {
            for(var cp : entity.getConnectionPoints()) {
                for(var e : cp.getConnections().entrySet()) {
                    if(e.getValue() == null) continue;
                    var ng = NetworkGroup.getCanonical(e.getValue());
                    networkMap.compute(ng, (k, v) -> {
                        if(v == null) {
                            v = new ArrayList<>();
                        }
                        v.add(new Node(entity, cp.getId(), e.getKey()));
                        return v;
                    });
                }
            }
        }

        List<Tuple<Node, Node>> connections = new ArrayList<>();

        for(var nodes : networkMap.values()) {
            connections.addAll(solveMstForCompleteGraph(nodes));
        }

        return connections;
    }

    private static List<Tuple<Node, Node>> solveMstForCompleteGraph(List<Node> entities) {
        if(entities.size() <= 1) return Collections.emptyList();
        List<Tuple<Node, Node>> connections = new ArrayList<>();
        Set<Node> connected = new HashSet<>();
        connected.add(entities.iterator().next());
        while(connected.size() != entities.size()) {
            Node closestConnected = null;
            Node closestUnconnected = null;

            for(Node connectedNode : connected) {
                for(Node node : entities) {
                    if(connected.contains(node)) continue;
                    if(closestConnected == null || closestConnected.distanceSquared(closestUnconnected) > connectedNode.distanceSquared(node)) {
                        closestConnected = connectedNode;
                        closestUnconnected = node;
                    }
                }
            }
            connected.add(closestUnconnected);
            if(closestConnected.wireColor != closestUnconnected.wireColor) throw new IllegalArgumentException("Network group with multiple wire colors: " + closestConnected.wireColor + ", " + closestUnconnected.wireColor);
            connections.add(new Tuple<>(closestConnected, closestUnconnected));
        }
        return connections;
    }

    public static class Node {
        private final CircuitNetworkEntity entity;
        private final int circuitId;
        private final WireColor wireColor;

        public Node(CircuitNetworkEntity entity, int circuitId, WireColor wireColor) {
            this.entity = entity;
            this.circuitId = circuitId;
            this.wireColor = wireColor;
        }

        public CircuitNetworkEntity getEntity() {
            return entity;
        }

        public int getCircuitId() {
            return circuitId;
        }

        public WireColor getWireColor() {
            return wireColor;
        }

        public int distanceSquared(Node other) {
            int dx = entity.getX() - other.getEntity().getX();
            int dy = entity.getY() - other.getEntity().getY();
            return dx * dx + dy * dy;
        }
    }
}
