package me.joba.factorio;

import java.util.*;

public class NetworkGroup {

    private Map<Combinator, Signal> inputs = new HashMap<>();

    private Signal state;

    public Signal getState() {
        return state;
    }

    public void aggregateInputs() {
        if(inputs.isEmpty()) {
            state = Signal.EMPTY;
            return;
        }
        state = Signal.merge(inputs.values());
        inputs.clear();
    }

    public void addOutput(Combinator c, Signal s) {
        inputs.put(c, s);
    }

    public static final Map<Long, Node> NODE_MAP = new HashMap<>();
    public static final Set<NetworkGroup> NETWORK_GROUPS = new HashSet<>();

    private static Node getNode(int entityId, int circuitId, int color) {
        long key = (((long)entityId) << 32) | ((circuitId & 0x0000ffffL) << 16)  | ((color & 0x0000ffffL));
        var node = NODE_MAP.get(key);
        if(node == null) {
            node = new Node(key);
            NODE_MAP.put(key, node);
        }
        return node;
    }

    public static void addConnection(int entity1, int circuit1, int entity2, int circuit2, int color) {
        var node1 = getNode(entity1, circuit1, color);
        var node2 = getNode(entity2, circuit2, color);
        addConnection(node1, node2);
    }

    public static void calculateNetworkGroups() {
        Queue<Node> toCheck = new LinkedList<>();
        for(Node n : NODE_MAP.values()) {
            NetworkGroup group = new NetworkGroup();
            toCheck.add(n);
            while(!toCheck.isEmpty()) {
                var node = toCheck.poll();
                if(node.group == null) {
                    toCheck.addAll(node.connections);
                    node.group = group;
                    NETWORK_GROUPS.add(group);
                }
            }
        }
    }

    public static NetworkGroup getNetworkGroup(int entityId, int circuitId, int color) {
        return getNode(entityId, circuitId, color).group;
    }

    private static void addConnection(Node a, Node b) {
        a.connections.add(b);
        b.connections.add(a);
    }

    private static class Node {
        private final long id;
        private final Set<Node> connections;
        private NetworkGroup group;

        private Node(long id) {
            this.id = id;
            this.connections = new HashSet<>();
        }
    }
}
