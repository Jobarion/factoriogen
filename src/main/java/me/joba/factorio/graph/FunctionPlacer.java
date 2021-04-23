package me.joba.factorio.graph;

import me.joba.factorio.BlueprintWriter;
import me.joba.factorio.NetworkGroup;
import me.joba.factorio.game.Entity;
import me.joba.factorio.game.EntityBlock;
import me.joba.factorio.game.WireColor;
import me.joba.factorio.game.entities.CircuitNetworkEntity;
import me.joba.factorio.game.entities.ConstantCombinator;
import me.joba.factorio.game.entities.LargePowerPole;
import me.joba.factorio.game.entities.Substation;
import me.joba.factorio.lang.Constant;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.ToIntFunction;
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
            var node = new Node(entityIdNodeIdMap.get(cc.getEntityId()), connectedTo);
            if(cc.isFixedLocation()) {
                node.setFixedLocation(true);
                node.setX(cc.getX());
                node.setY(cc.getY());
                node.setOrientation(cc.getOrientation());
            }
            nodes.add(node);
        }

        placeInitial(nodes);
        SimulatedAnnealingSolver.simulatedAnnealing(nodes, 10_000_000);

        Map<Integer, Node> nodeIdMap = nodes.stream()
                .collect(Collectors.toMap(e -> e.getId(), e -> e));

        for(CircuitNetworkEntity entity : combinators) {
            var node = nodeIdMap.get(entityIdNodeIdMap.get(entity.getEntityId()));
            entity.setX(node.getX());
            entity.setY(node.getY());
            entity.setOrientation(node.getOrientation());
        }
    }

    public static EntityBlock placeFunction(List<CircuitNetworkEntity> combinators, List<NetworkGroup> networks, NetworkGroup functionCallOutGroup, NetworkGroup functionCallReturnGroup) {
        placeCombinators(combinators, networks);
        combinators.addAll(generateSubstations(combinators, functionCallOutGroup, functionCallReturnGroup));
        var connections = buildConnectionObjects(MSTSolver.solveMst(combinators));

        for(var entity : combinators) {
            entity.setConnectionData(connections.get(entity.getEntityId()));
        }

        return new EntityBlock(combinators);
    }

    public static EntityBlock generateFunctionConnectors(List<EntityBlock> functionBlocks) {
        List<CircuitNetworkEntity> closestSubstations = functionBlocks.stream()
                .map(block -> block.getEntities().stream()
                        .filter(x -> x instanceof Substation)
                        .map(x -> (Substation)x)
                        .sorted(Comparator.comparingInt((ToIntFunction<Substation>) Entity::getX).thenComparingInt(Entity::getY))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Function blocks doesn't contain a substation"))
                )
                .sorted(Comparator.comparingInt(Entity::getX))
                .collect(Collectors.toList());
        List<CircuitNetworkEntity> powerPoles = new ArrayList<>();
        CircuitNetworkEntity lastPole = null;
        for(var substation : closestSubstations) {
            var pole = new LargePowerPole();
            pole.setX(substation.getX());
            pole.setY(-1);//Hack :(

            JSONObject conn = new JSONObject();
            JSONObject one = new JSONObject();
            JSONArray red = new JSONArray();
            JSONArray green = new JSONArray();
            conn.put("1", one);
            one.put("red", red);
            one.put("green", green);

            green.add(new JSONObject(Map.of("entity_id", substation.getEntityId())));
            red.add(new JSONObject(Map.of("entity_id", substation.getEntityId())));

            if(lastPole != null) {
                green.add(new JSONObject(Map.of("entity_id", lastPole.getEntityId())));
                red.add(new JSONObject(Map.of("entity_id", lastPole.getEntityId())));
            }
            pole.setConnectionData(conn);
            lastPole = pole;
            powerPoles.add(pole);
        }
        return new EntityBlock(powerPoles);

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

    private static void placeInitial(List<Node> nodes) {
        double sqrt = Math.ceil(Math.sqrt((double)nodes.size() / 2));
        int shortEdgeLength = (int)Math.ceil(sqrt); //We want a square of combinators.
        var existing = nodes.stream()
                .filter(Node::isFixedLocation)
                .map(x -> new Point(x.getX(), x.getY()))
                .collect(Collectors.toSet());
        int x = 0;
        int y = 0;
        for(int i = 0; i < nodes.size(); i++) {
            Node node = nodes.get(i);
            if(node.isFixedLocation()) continue;
            //Skip pre placed combinators
            if(existing.contains(new Point(x, y))) {
                x++;
                i--;
                continue;
            }
            else if((x * 2) % 18 == 8 && (y / 2) % 9 == 4) {//This magic condition makes space for substations
                x++;
                i--;
                continue;
            }
            node.setOrientation(2);
            node.setX(x * 2);
            node.setY(y);
            x++;
            if(x > shortEdgeLength) {
                x = 0;
                y++;
            }
        }
    }

    public static void main(String[] args) {
//        for(int x = 15; x < 40; x++) {
//            var c1 = new ConstantCombinator(Collections.emptyMap());
//            c1.setX(0);
//            c1.setY(0);
//            var c2 = new ConstantCombinator(Collections.emptyMap());
//            c2.setX(x);
//            c2.setY(17);
//
//            List<CircuitNetworkEntity> entities = new ArrayList<>();
//            entities.add(c1);
//            entities.add(c2);
//
//            entities.addAll(generateSubstations(entities, null, null));
//
//            var block = new EntityBlock(entities);
//            System.out.println(x + ": " + BlueprintWriter.writeBlueprint(Arrays.asList(block)));
//        }

        var c1 = new ConstantCombinator(Collections.emptyMap());
        c1.setX(0);
        c1.setY(0);
        var c2 = new ConstantCombinator(Collections.emptyMap());
        c2.setX(1);
        c2.setY(1);

        List<CircuitNetworkEntity> entities = new ArrayList<>();
        entities.add(c1);
        entities.add(c2);

        entities.addAll(generateSubstations(entities, null, null));

        var block = new EntityBlock(entities);
        System.out.println(25 + ": " + BlueprintWriter.writeBlueprint(Arrays.asList(block)));
    }

    private static List<CircuitNetworkEntity> generateSubstations(List<CircuitNetworkEntity> combinators, NetworkGroup functionCallOutGroup, NetworkGroup functionCallReturnGroup) {
        final int xOffset = 8;
        final int yOffset = 9;

        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for(var cne : combinators) {
            maxX = Math.max(maxX, cne.getX());
            maxY = Math.max(maxY, cne.getY());
        }

        List<CircuitNetworkEntity> substations = new ArrayList<>();
        for(int x = xOffset; x <= maxX + 1; x += 18) {
            for(int y = yOffset; y <= maxY + 1; y += 18) {
                var substation = new Substation(x, y);
                substations.add(substation);
            }
        }

        boolean placeXEdge = (maxX - xOffset + 1) % 18 > xOffset || maxX < xOffset;
        boolean placeYEdge = (maxY - yOffset + 1) % 18 > yOffset || maxY < yOffset;

        if(placeXEdge) {
            int x = maxX + 2;
            for(int y = yOffset; y <= maxY; y += 18) {
                var substation = new Substation(x, y);
                substations.add(substation);
            }
        }

        if(placeYEdge) {
            int y = maxY + 2;
            for(int x = xOffset; x <= maxX; x += 18) {
                var substation = new Substation(x, y);
                substations.add(substation);
            }
        }

        if(placeXEdge && placeYEdge) {
            substations.add(new Substation(maxX + 2, maxY + 2));
        }
        for(var s : substations) {
            var conn = s.getConnectionPoints()[0].getConnections();
            conn.put(WireColor.GREEN, functionCallOutGroup);
            conn.put(WireColor.RED, functionCallReturnGroup);
        }
        return substations;
    }

//    private static List<CircuitNetworkEntity> generateSubstations(List<CircuitNetworkEntity> combinators, NetworkGroup functionCallOutGroup, NetworkGroup functionCallReturnGroup) {
//        int maxX = Integer.MIN_VALUE;
//        int maxY = Integer.MIN_VALUE;
//        for(var cne : combinators) {
//            maxX = Math.max(maxX, cne.getX());
//            maxY = Math.max(maxY, cne.getY());
//        }
//        maxX += 17;
//        maxY += 17;
//
//        List<CircuitNetworkEntity> substations = new ArrayList<>();
//        for(int x = 8; x <= maxX; x += 18) {
//            for(int y = 9; y <= maxY; y += 18) {
//                var substation = new Substation(x, y);
//                substations.add(substation);
//            }
//        }
////        //The edge
////        if(maxX % 18 < 8) {
////            int x = maxX + 2;
////            for(int y = 9; y < maxY; y += 18) {
////                var substation = new Substation(x, y);
////                substations.add(substation);
////            }
////        }
////        if(maxY % 18 < 9) {
////            int y = maxY + 2;
////            for(int x = 8; x < maxX; x += 18) {
////                var substation = new Substation(x, y);
////                substations.add(substation);
////            }
////        }
////        if(maxX % 18 < 8 && maxY % 18 < 9) {
////            var substation = new Substation(maxX + 2, maxY + 2);
////            substations.add(substation);
////        }
//        for(var s : substations) {
//            var conn = s.getConnectionPoints()[0].getConnections();
//            conn.put(WireColor.GREEN, functionCallOutGroup);
//            conn.put(WireColor.RED, functionCallReturnGroup);
//        }
//        return substations;
//    }

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
