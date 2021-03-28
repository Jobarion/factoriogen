package me.joba.factorio;

import me.joba.factorio.graph.Node;
import me.joba.factorio.graph.SimulatedAnnealingSolver;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.Deflater;

public class BlueprintWriter {

    public static List<MSTSolver.Point> placeCombinators(List<ConnectedCombinator> combinators, Collection<NetworkGroup> networks) {
        List<Node> nodes = new ArrayList<>();

        Map<Integer, Integer> entityIdNodeIdMap = new HashMap<>();
        int currentId = 0;
        for(var cc : combinators) {
            entityIdNodeIdMap.put(cc.getEntityId(), currentId++);
        }
        Map<NetworkGroup, Set<ConnectedCombinator>> networkMap = new HashMap<>();
        for(NetworkGroup ng : networks) {
            Set<ConnectedCombinator> connected = new HashSet<>();
            for(ConnectedCombinator cc : combinators) {
                if(NetworkGroup.isEqual(cc.getGreenIn(), ng) || NetworkGroup.isEqual(cc.getGreenOut(), ng) || NetworkGroup.isEqual(cc.getRedIn(), ng) || NetworkGroup.isEqual(cc.getRedOut(), ng)) {
                    connected.add(cc);
                }
            }
            networkMap.put(ng, connected);
        }

        for(var cc : combinators) {
            Set<Integer> connectedTo = new HashSet<>();
            List<NetworkGroup> combinatorNetworks = Arrays.asList(cc.getGreenIn(), cc.getGreenOut(), cc.getRedIn(), cc.getRedOut());
            for(var network : combinatorNetworks) {
                if(network == null) continue;
                for(var neighbor : networkMap.get(network)) {
                    connectedTo.add(entityIdNodeIdMap.get(neighbor.getEntityId()));
                }
            }
            nodes.add(new Node(entityIdNodeIdMap.get(cc.getEntityId()), connectedTo));
        }

        SimulatedAnnealingSolver.placeInitial(nodes);
        SimulatedAnnealingSolver.simulatedAnnealing(nodes, 100_000_000);
        List<MSTSolver.Point> points = new ArrayList<>();
        for(Node node : nodes) {
            points.add(new MSTSolver.Point(node.getX(), node.getY()));
        }
        return points;
    }

    public static String writeBlueprint(List<ConnectedCombinator> combinators, Collection<NetworkGroup> networks) {
        List<MSTSolver.Point> calculatedPositions = placeCombinators(combinators, networks);
        JSONArray entities = new JSONArray();
        Map<Integer, JSONObject> connections = new HashMap<>();
        Map<Integer, MSTSolver.Point> positions = new HashMap<>();
        for(int i = 0; i < combinators.size(); i++) {
            var combinator = combinators.get(i);
            var position = calculatedPositions.get(i);
            if(combinator.getName() != null) {
                System.out.println("Placed '" + combinator.getName() + "' at x=" + position.getX() + " y=" + position.getY());
            }
            var json = combinator.getCombinator().createJson();
            JSONObject pos = new JSONObject();
            json.put("position", pos);
            json.put("direction", 2);
            json.put("entity_number", combinator.getEntityId());
            pos.put("x", position.getX());
            pos.put("y", position.getY());
            positions.put(combinator.getEntityId(), position);
            JSONObject connectionList = new JSONObject();
            json.put("connections", connectionList);
            connections.put(combinator.getEntityId(), connectionList);
            entities.add(json);
        }
        Map<NetworkGroup, Set<ConnectedCombinator>> networkMap = new HashMap<>();
        Set<NetworkGroup> handled = new HashSet<>();
        for(NetworkGroup ng : networks) {
            if(handled.contains(ng)) continue;
            Set<ConnectedCombinator> connected = new HashSet<>();
            List<MSTSolver.Node> nodes = new ArrayList<>();
            for(ConnectedCombinator cc : combinators) {

                var jsonConn = connections.get(cc.getEntityId());
                var jsonObjIn = getOrElse(jsonConn, "1", new JSONObject());
                var jsonObjOut = getOrElse(jsonConn, "2", new JSONObject());

                var greenIn = getOrElse(jsonObjIn, "green", new JSONArray());
                var greenOut = getOrElse(jsonObjOut, "green", new JSONArray());
                var redIn = getOrElse(jsonObjIn, "red", new JSONArray());
                var redOut = getOrElse(jsonObjOut, "red", new JSONArray());

                jsonObjIn.put("green", greenIn);
                jsonObjIn.put("red", redIn);
                jsonObjOut.put("green", greenOut);
                jsonObjOut.put("red", redOut);
                jsonConn.put("1", jsonObjIn);
                jsonConn.put("2", jsonObjOut);

                if(NetworkGroup.isEqual(cc.getGreenIn(), ng)) {
                    nodes.add(new MSTSolver.Node(positions.get(cc.getEntityId()), cc.getEntityId(), 1) {
                        @Override
                        public void accept(MSTSolver.Node node) {
                            JSONObject conObj = new JSONObject();
                            conObj.put("entity_id", node.getEntityId());
                            conObj.put("circuit_id", node.getCircuitId());
                            greenIn.add(conObj);
                        }
                    });
                }


                if(NetworkGroup.isEqual(cc.getGreenOut(), ng)) {
                    nodes.add(new MSTSolver.Node(positions.get(cc.getEntityId()), cc.getEntityId(), cc.getCombinator().isOutputOnly() ? 1 : 2) {
                        @Override
                        public void accept(MSTSolver.Node node) {
                            JSONObject conObj = new JSONObject();
                            conObj.put("entity_id", node.getEntityId());
                            conObj.put("circuit_id", node.getCircuitId());
                            greenOut.add(conObj);
                        }
                    });
                }


                if(NetworkGroup.isEqual(cc.getRedIn(), ng)) {
                    nodes.add(new MSTSolver.Node(positions.get(cc.getEntityId()), cc.getEntityId(), 1) {
                        @Override
                        public void accept(MSTSolver.Node node) {
                            JSONObject conObj = new JSONObject();
                            conObj.put("entity_id", node.getEntityId());
                            conObj.put("circuit_id", node.getCircuitId());
                            redIn.add(conObj);
                        }
                    });
                }


                if(NetworkGroup.isEqual(cc.getRedOut(), ng)) {
                    nodes.add(new MSTSolver.Node(positions.get(cc.getEntityId()), cc.getEntityId(), cc.getCombinator().isOutputOnly() ? 1 : 2) {
                        @Override
                        public void accept(MSTSolver.Node node) {
                            JSONObject conObj = new JSONObject();
                            conObj.put("entity_id", node.getEntityId());
                            conObj.put("circuit_id", node.getCircuitId());
                            redOut.add(conObj);
                        }
                    });
                }
            }
            MSTSolver.solveMst(nodes);
            networkMap.put(ng, connected);
            handled.addAll(NetworkGroup.getMerged(ng));
        }

        JSONObject blueprint = new JSONObject();
        blueprint.put("icons", new JSONArray());
        blueprint.put("entities", entities);
        blueprint.put("item", "blueprint");
        blueprint.put("version", 281474976710656L);
        JSONObject root = new JSONObject();
        root.put("blueprint", blueprint);
        String outString = root.toJSONString();
        System.out.println(outString);
        Deflater deflater = new Deflater(9);
        deflater.setInput(outString.getBytes(StandardCharsets.UTF_8));
        deflater.finish();
        byte[] total = new byte[0];
        byte[] buf = new byte[1024];
        while(!deflater.finished()) {
            int written = deflater.deflate(buf);
            byte[] tmp = new byte[total.length + written];
            System.arraycopy(total, 0, tmp, 0, total.length);
            System.arraycopy(buf, 0, tmp, total.length, written);
            total = tmp;
        }
        deflater.end();
        byte[] base64Bytes = Base64.getEncoder().encode(total);

        return "0" + new String(base64Bytes);
    }

    private static <T> T get(JSONObject obj, String key) {
        var x = obj.get(key);
        if(x == null) return (T)x;
        if(x instanceof Long) {
            return (T)(Object)((Long) x).intValue();
        }
        return (T)x;
    }

    private static <T> T getOrElse(JSONObject obj, String key, T orElse) {
        T val = get(obj, key);
        if(val == null) return orElse;
        return val;
    }
}
