package me.joba.factorio;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class MSTSolver {

    //Assumes compete graph
    public static void solveMst(Collection<Node> nodes) {
        if(nodes.size() <= 1) return;
        Set<Node> connected = new HashSet<>();
        connected.add(nodes.iterator().next());
        while(connected.size() != nodes.size()) {
            Node closestConnected = null;
            Node closestUnconnected = null;

            for(Node connectedNode : connected) {
                for(Node node : nodes) {
                    if(connected.contains(node)) continue;
                    if(closestConnected == null || closestConnected.distance(closestUnconnected) > connectedNode.distance(node)) {
                        closestConnected = connectedNode;
                        closestUnconnected = node;
                    }
                }
            }
            connected.add(closestUnconnected);
            closestConnected.accept(closestUnconnected);
            closestUnconnected.accept(closestConnected);
        }
    }

    public static abstract class Node implements Consumer<Node> {
        private final Point point;
        private int entityId, circuitId;

        public Node(Point point, int entityId, int circuitId) {
            this.point = point;
            this.entityId = entityId;
            this.circuitId = circuitId;
        }

        public int getEntityId() {
            return entityId;
        }

        public int getCircuitId() {
            return circuitId;
        }

        public int distance(Node other) {
            int dx = point.x - other.point.x;
            int dy = point.y - other.point.y;
            return dx * dx + dy * dy;
        }
    }

    public static class Point {
        private final int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}
