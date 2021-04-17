package me.joba.factorio.graph;

import me.joba.factorio.NetworkGroup;
import me.joba.factorio.game.EntityBlock;
import me.joba.factorio.game.combinators.CircuitNetworkEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class FunctionPlacer {

    private static void placeCombinators(List<CircuitNetworkEntity> combinators, Collection<NetworkGroup> networks) {
        List<Node> nodes = new ArrayList<>();

        Map<Integer, Integer> entityIdNodeIdMap = new HashMap<>();
        int currentId = 0;
        for(var cc : combinators) {
            entityIdNodeIdMap.put(cc.getEntityId(), currentId++);
        }
        Map<NetworkGroup, Set<CircuitNetworkEntity>> networkMap = new HashMap<>();
        for(NetworkGroup ng : networks) {
            Set<CircuitNetworkEntity> connected = new HashSet<>();
            for(CircuitNetworkEntity cc : combinators) {
                for(var cp : cc.getConnectionPoints()) {
                    for(var group : cp.getConnections().values()) {
                        if(group == null) continue;
                        if(NetworkGroup.isEqual(group, ng)) connected.add(cc);
                    }
                }
            }
            networkMap.put(ng, connected);
        }

        for(var cc : combinators) {
            Set<Integer> connectedTo = new HashSet<>();
            List<NetworkGroup> combinatorNetworks = Arrays.stream(cc.getConnectionPoints())
                    .flatMap(cp -> cp.getConnections().values().stream())
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            for(var network : combinatorNetworks) {
                if(network == null) continue;
                for(var neighbor : networkMap.get(network)) {
                    connectedTo.add(entityIdNodeIdMap.get(neighbor.getEntityId()));
                }
            }
            nodes.add(new Node(entityIdNodeIdMap.get(cc.getEntityId()), connectedTo));
        }

        SimulatedAnnealingSolver.placeInitial(nodes);
        SimulatedAnnealingSolver.simulatedAnnealing(nodes, 10_000_000);

        Map<Integer, Node> nodeIdMap = nodes.stream()
                .collect(Collectors.toMap(e -> e.getId(), e -> e));

        for(CircuitNetworkEntity entity : combinators) {
            var node = nodeIdMap.get(entityIdNodeIdMap.get(entity.getEntityId()));
            entity.setX(node.getX());
            entity.setY(node.getY());
            entity.setOrientation(2);
        }
    }

    public static EntityBlock placeFunction(List<CircuitNetworkEntity> combinators, List<NetworkGroup> networks) {
        placeCombinators(combinators, networks);
        var connections = buildConnectionObjects(MSTSolver.solveMst(combinators));

        for(var entity : combinators) {
            entity.setConnectionData(connections.get(entity.getEntityId()));
        }

        return new EntityBlock(combinators);
    }

    private static Map<Integer, JSONObject> buildConnectionObjects(List<Tuple<MSTSolver.Node, MSTSolver.Node>> connections) {
        Map<Integer, JSONObject> connectionObjects = new HashMap<>();
        for(var t : connections) {
            var n1 = t.getLeft();
            var n2 = t.getRight();
            var json1 = connectionObjects.getOrDefault(n1.getEntity().getEntityId(), new JSONObject());
            var json2 = connectionObjects.getOrDefault(n2.getEntity().getEntityId(), new JSONObject());
            connectionObjects.put(n1.getEntity().getEntityId(), json1);
            connectionObjects.put(n2.getEntity().getEntityId(), json2);

            var connectionPoint1 = getOrDefault(json1, String.valueOf(n1.getCircuitId()), new JSONObject());
            json1.put(String.valueOf(n1.getCircuitId()), connectionPoint1);

            var color1 = getOrDefault(connectionPoint1, n1.getWireColor().name().toLowerCase(), new JSONArray());
            connectionPoint1.put(n1.getWireColor().name().toLowerCase(), color1);

            var connectionPoint2 = getOrDefault(json2, String.valueOf(n2.getCircuitId()), new JSONObject());
            json2.put(String.valueOf(n2.getCircuitId()), connectionPoint2);
            var color2 = getOrDefault(connectionPoint2, n2.getWireColor().name().toLowerCase(), new JSONArray());
            connectionPoint2.put(n2.getWireColor().name().toLowerCase(), color2);

            JSONObject entry1 = new JSONObject();
            entry1.put("entity_id", n2.getEntity().getEntityId());
            if(n2.getEntity().getConnectionPoints().length > 1) {
                entry1.put("circuit_id", n2.getCircuitId());
            }

            JSONObject entry2 = new JSONObject();
            entry2.put("entity_id", n1.getEntity().getEntityId());
            if(n1.getEntity().getConnectionPoints().length > 1) {
                entry2.put("circuit_id", n1.getCircuitId());
            }

            color1.add(entry1);
            color2.add(entry2);
        }
        return connectionObjects;
    }

    private static <T> T get(JSONObject obj, String key) {
        var x = obj.get(key);
        if(x == null) return (T)x;
        if(x instanceof Long) {
            return (T)(Object)((Long) x).intValue();
        }
        return (T)x;
    }

    private static <T> T getOrDefault(JSONObject obj, String key, T orElse) {
        var x = obj.getOrDefault(key, orElse);
        if(x instanceof Long) {
            return (T)(Object)((Long) x).intValue();
        }
        return (T)x;
    }
}
