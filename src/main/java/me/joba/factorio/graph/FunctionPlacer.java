package me.joba.factorio.graph;

import me.joba.factorio.NetworkGroup;
import me.joba.factorio.game.Entity;
import me.joba.factorio.game.EntityBlock;
import me.joba.factorio.game.WireColor;
import me.joba.factorio.game.entities.CircuitNetworkEntity;
import me.joba.factorio.game.entities.LargePowerPole;
import me.joba.factorio.game.entities.Substation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class FunctionPlacer {

    private static final int SUBSTATION_OFFSET_X = 6;
    private static final int SUBSTATION_OFFSET_Y = 9;
    private static final int SUBSTATION_SPACING_X = 14;
    private static final int SUBSTATION_SPACING_Y = 18;

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
            node.setOrientation(cc.getOrientation());
            if(cc.isFixedLocation()) {
                node.setFixedLocation(true);
                node.setX(cc.getX());
                node.setY(cc.getY());
            }
            nodes.add(node);
        }

        placeInitial(nodes);
        SimulatedAnnealingSolver.simulatedAnnealing(nodes, 10_000_000);

        Map<Integer, Node> nodeIdMap = nodes.stream()
                .collect(Collectors.toMap(Node::getId, e -> e));

        for(CircuitNetworkEntity entity : combinators) {
            var node = nodeIdMap.get(entityIdNodeIdMap.get(entity.getEntityId()));
            entity.setX(node.getX());
            entity.setY(node.getY());
            entity.setOrientation(node.getOrientation());
        }
    }

    public static EntityBlock placeFunction(List<CircuitNetworkEntity> combinators, List<NetworkGroup> networks, NetworkGroup functionCallOutGroup, NetworkGroup functionCallReturnGroup) {
        return placeFunction(combinators, networks, functionCallOutGroup, functionCallReturnGroup, false);
    }

    public static EntityBlock placeFunction(List<CircuitNetworkEntity> combinators, List<NetworkGroup> networks, NetworkGroup functionCallOutGroup, NetworkGroup functionCallReturnGroup, boolean prePlaced) {
        if(!prePlaced) {
            placeCombinators(combinators, networks);
            combinators.addAll(generateSubstations(combinators, functionCallOutGroup, functionCallReturnGroup));
        }
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
                        .map(x -> (Substation) x)
                        .min(Comparator.comparingInt((ToIntFunction<Substation>) Entity::getY).thenComparingInt(Entity::getX))
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
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
            else if((x * 2) % SUBSTATION_SPACING_X == SUBSTATION_OFFSET_X && (y % SUBSTATION_SPACING_Y) / 2 == SUBSTATION_OFFSET_Y / 2) {//This magic condition makes space for substations
                x++;
                i--;
                continue;
            }
            node.setX(x * 2);
            node.setY(y);
            x++;
            if(x > shortEdgeLength) {
                x = 0;
                y++;
            }
        }
    }

    private static List<CircuitNetworkEntity> generateSubstations(List<CircuitNetworkEntity> combinators, NetworkGroup functionCallOutGroup, NetworkGroup functionCallReturnGroup) {
        if(combinators.isEmpty()) return Collections.emptyList();
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for(var cne : combinators) {
            maxX = Math.max(maxX, cne.getX());
            maxY = Math.max(maxY, cne.getY());
        }

        List<CircuitNetworkEntity> substations = new ArrayList<>();
        for(int x = SUBSTATION_OFFSET_X; x <= maxX + 1; x += SUBSTATION_SPACING_X) {
            for(int y = SUBSTATION_OFFSET_Y; y <= maxY + 1; y += SUBSTATION_SPACING_Y) {
                var substation = new Substation(x, y);
                substations.add(substation);
            }
        }

        boolean placeXEdge = (maxX - SUBSTATION_OFFSET_X + 2) % SUBSTATION_SPACING_X > SUBSTATION_OFFSET_X || maxX < SUBSTATION_OFFSET_X;
        boolean placeYEdge = (maxY - SUBSTATION_OFFSET_Y + 2) % SUBSTATION_SPACING_Y > SUBSTATION_OFFSET_Y || maxY < SUBSTATION_OFFSET_Y;

        if(placeXEdge) {
            int x = maxX + 1;
            for(int y = SUBSTATION_OFFSET_Y; y <= maxY; y += SUBSTATION_SPACING_Y) {
                var substation = new Substation(x + 1, y);
                substations.add(substation);
            }
        }

        if(placeYEdge) {
            int y = maxY + 1;
            for(int x = SUBSTATION_OFFSET_X; x <= maxX; x += SUBSTATION_SPACING_X) {
                var substation = new Substation(x, y + 1);
                substations.add(substation);
            }
        }

        if(placeXEdge && placeYEdge) {
            substations.add(new Substation(maxX + 1, maxY + 1));
        }
        for(var s : substations) {
            var conn = s.getConnectionPoints()[0].getConnections();
            conn.put(WireColor.GREEN, functionCallOutGroup);
            conn.put(WireColor.RED, functionCallReturnGroup);
        }
        return substations;
    }

    private static <T> T getOrDefault(JSONObject obj, String key, T orElse) {
        var x = obj.getOrDefault(key, orElse);
        if(x instanceof Long) {
            return (T)(Object)((Long) x).intValue();
        }
        return (T)x;
    }
}
