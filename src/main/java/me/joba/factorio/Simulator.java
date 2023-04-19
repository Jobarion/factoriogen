package me.joba.factorio;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Simulator {

    private static final String CLOCK = "0eNqtlMFugzAMht/F57QqtIUV7bb7XmCaEAS3tQQJCqYaqnj3JWHt2NSuY90FySH+bP+/4iPkZYu1IcWQHIGkVg0kL0doaKey0p1xVyMkQIwVCFBZ5aLMEO8rZJIzqaucVMbaQC+AVIFvkAS9uMkoUFKB5jIg7F8FoGJiwqEjH3Spaqscja1w5rieOVM8BgmodWNztXLlHS+KlvO1gM5mRpv52lYqyKAcroTCUdjoMs1xnx3IImzelkpGc0WQAxlu7cm5j+HG7MlNIXXrFA1uCHKN8TxmLMai+HOlhsYbhwrcZ2cQ1VgmKoapyMiW2IeBze5dG9+UDH9y5JKQU2X8wKb2X0HnvrdkGk6nydqgY6RTdazqzPhpEni0d3TLdTu1tNR1l3pL0q3RVUrKMiBh02J/hynhyJCTSQLC33v6NX35O8uXNx7yJdfjia5/kv/P+NNT9xPoGq2rvgWY/c3W+5xz2tot5bdaMlqkAg52awzaPASreLWJozhYROuo798BHDbaZw==";
    private static final String NETWORK = "0eNrtms2OmzAQx9/Fx5asMOEzak977wtUK0TA2VgKJjImarTi3YthSVgg4IGkSbe9RCKBwcxv/jPjIW9ovcvInlMm0OoN0TBhKVr9fEMpfWXBTn4njnuCVogKEiMNsSCWRwGnYhsTQcNFmMRrygKRcJRriLKI/EIrnGujNiIS0ojwfgNG/qIhwgQVlFQrKg+OPsviNeHFHU525JpFwETTkIb2SVpcmzB5e2nPtpdPloaOxZW292QVd4ooJ2F1iqFJK4InO39NtsGBFiaK6zZ0Jwi/4JAD5SIrvjmtozpj8SyfIkwy6VE84pBLNn40behNp5TfM1YtPJWmsPx45YSwpptoVD0V5WFGRXmIi6tzuYyWJ40hIn2OhLrx3axf/BbR07o3lKfCh7k1JdKGD/VjvA94+TQr9K04J8nEPoPeOkz2R79E4m94EvuUFTbQSvCM5DOgGA0gNSQNGepMP16+VEO+HBFyH3UHSP1s+Xrga6mXT5DsSUG1XAJaTMM6j9wwKXOYFFZEZULVaVacHPzw6qRsk3QE+n0CSXIg/Ci2lL1OVConURufLQtQL9jlsIKtEe5Wl/tQGFndqOsLEwucxOtq6Bgfw8T8tGESJWIgPjbBLoUEiNMBoykmAlDeaIeXq5Y2bHB7ZJ8CosocV2uIzvyy98x96ole1N09kk4dNbc4UwofUCnXKny13zq1D8+vfZVtWDqso7bnV0vN+y40Sbn3SFKnNFH7W3+EEtWbK1xQKRrrWNxhiXlqkL0JEvMeTWKLlsa+XF1jU3hiPNJbAEtN254xYu9S8yG3qFDmssrchfm7vG+CvLQNIt5FCkV2AQmegES/E5LnmwGB7vG8EYHpMFreiL50RZjgEc2ZpPnnCufybymbynltypwEA11/WxHJEGuK6OsECLLhg8loCWs8sK3WWmBzAo96HmK05yH2bXmQINz2IrkCkcr2vMw2L3MpzpaxNYOY9RCTxivgglahkb2ugW8kL3vqrNH8P2ucWZGsEb2NDB2xAwwJxc0cnjIwOY8WrcfYzS1vPTDpf1+gX0uX7qyplfVPb6mxB0umGKgcQyE590KdMiex7wT1wXqZtnA62dAFMnXVmBk6eKp+nmBad5+q9z+74uTcwFPmesD+4PMNFOaH4vjrw1y+eir/W7Nq/J1HQ0WnklbedrHpmJ5jO1i3LTvPfwMT8FVm";

    public static final int COLOR_RED = 0;
    public static final int COLOR_GREEN = 1;

    public static final int CIRCUIT_PRIMARY = 1;
    public static final int CIRCUIT_SECONDARY = 2;

    public static void main(String[] args) throws Exception {

        var combinators = readBlueprint(NETWORK);

        var networks = NetworkGroup.NETWORK_GROUPS;

        System.out.println("Loaded " + combinators.size() + " combinators");
        System.out.println("Partitioned in " + networks.size() + " networks");

        System.out.println(writeBlueprint(combinators, networks));

        System.exit(0);
        while(true) {
            Thread.sleep(100);
            System.out.println("==TICK==");
            for(NetworkGroup ng : networks) {
                System.out.println(toString(ng.getState()));
            }
            networks.forEach(NetworkGroup::aggregateInputs);
            for(var c : combinators) {
                var signal = c.tick();
                if(c.getEntityId() == 12) {
                    System.out.println("OUT!!! " + toString(signal));
                }
            }
        }
    }

    public static String toString(Signal signal) {
        if(signal == null) signal = Signal.EMPTY;
        Map<Integer, Integer> state = new HashMap<>();
        for(int i = 0; i < Signal.SIGNAL_TYPES.get(); i++) {
            int val = signal.get(i);
            if(val != 0) state.put(i, val);
        }
        return state.entrySet()
                .stream()
                .collect(() -> new StringJoiner(","),
                        (acc, val) -> acc.add(Signal.getFactorioId(val.getKey()) + ": " + val.getValue()),
                        StringJoiner::merge).toString();
    }

    public static String writeBlueprint(Collection<ConnectedCombinator> combinators, Collection<NetworkGroup> networks) {
        int x = 0;
        int y = 0;
        JSONArray entities = new JSONArray();
        Map<Integer, JSONObject> connections = new HashMap<>();
        Map<Integer, MSTSolver.Point> positions = new HashMap<>();
        for(ConnectedCombinator combinator : combinators) {
            var json = combinator.getCombinator().createJson();
            JSONObject pos = new JSONObject();
            json.put("position", pos);
            json.put("direction", 2);
            json.put("entity_number", combinator.getEntityId());
            pos.put("x", x);
            pos.put("y", y);
            positions.put(combinator.getEntityId(), new MSTSolver.Point(x, y));
            JSONObject connectionList = new JSONObject();
            json.put("connections", connectionList);
            connections.put(combinator.getEntityId(), connectionList);
            entities.add(json);
            x += 2;
            if(x > 8) {
                x = 0;
                y++;
            }
        }
        Map<NetworkGroup, Set<ConnectedCombinator>> networkMap = new HashMap<>();
        for(NetworkGroup ng : networks) {
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

                if(cc.getGreenIn() == ng) {
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


                if(cc.getGreenOut() == ng) {
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


                if(cc.getRedIn() == ng) {
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


                if(cc.getRedOut() == ng) {
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
        }
        //Minimum spanning tree instead of complete graph pls
//        for(var entry : networkMap.entrySet()) {
//            var ng = entry.getKey();
//            for(var combi1 : entry.getValue()) {
//                var jsonConn = connections.get(combi1.getEntityId());
//                var jsonObjIn = getOrElse(jsonConn, "1", new JSONObject());
//                var jsonObjOut = getOrElse(jsonConn, "2", new JSONObject());
//                var greenIn = getOrElse(jsonObjIn, "green", new JSONArray());
//                var redIn = getOrElse(jsonObjIn, "red", new JSONArray());
//                var greenOut = getOrElse(jsonObjOut, "green", new JSONArray());
//                var redOut = getOrElse(jsonObjOut, "red", new JSONArray());
//                jsonObjIn.put("green", greenIn);
//                jsonObjIn.put("red", redIn);
//                jsonObjOut.put("green", greenOut);
//                jsonObjOut.put("red", redOut);
//                jsonConn.put("1", jsonObjIn);
//                if(!combi1.getCombinator().isOutputOnly()) jsonConn.put("2", jsonObjOut);
//                for(var combi2 : entry.getValue()) {
//                    if(combi1 == combi2) {
//                        if(combi1.getGreenIn() != null && combi1.getGreenIn() == combi1.getGreenOut()) {
//                            JSONObject conObj = new JSONObject();
//                            conObj.put("entity_id", combi1.getEntityId());
//                            conObj.put("circuit_id", "2");
//                            greenIn.add(conObj);
//                            conObj = new JSONObject();
//                            conObj.put("entity_id", combi1.getEntityId());
//                            conObj.put("circuit_id", "1");
//                            greenOut.add(conObj);
//                        }
//                        if(combi1.getRedIn() != null && combi1.getRedOut() == combi1.getRedIn()) {
//                            JSONObject conObj = new JSONObject();
//                            conObj.put("entity_id", combi1.getEntityId());
//                            conObj.put("circuit_id", "2");
//                            redIn.add(conObj);
//                            conObj = new JSONObject();
//                            conObj.put("entity_id", combi1.getEntityId());
//                            conObj.put("circuit_id", "1");
//                            redOut.add(conObj);
//                        }
//                        continue;
//                    }
//                    JSONObject conObj = new JSONObject();
//                    conObj.put("entity_id", combi2.getEntityId());
//                    int circuitId = combi2.getGreenIn() == ng || combi2.getRedIn() == ng || combi2.getCombinator().isOutputOnly() ? 1 : 2;
//                    conObj.put("circuit_id", circuitId);
//                    if(combi1.getGreenIn() == ng || (combi1.getGreenOut() == ng && combi1.getCombinator().isOutputOnly())) greenIn.add(conObj);
//                    else if(combi1.getRedIn() == ng || (combi1.getRedOut() == ng && combi1.getCombinator().isOutputOnly())) redIn.add(conObj);
//                    else if(combi1.getGreenOut() == ng) greenOut.add(conObj);
//                    else if(combi1.getRedOut() == ng) redOut.add(conObj);
//                    else throw new RuntimeException("???");
//                }
//            }
//        }

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

    private static Collection<ConnectedCombinator> readBlueprint(String blueprintString) throws Exception {
        StringBuilder sb = new StringBuilder();
        Inflater inflater = new Inflater();
        byte[] blueprintBytes = blueprintString.getBytes(StandardCharsets.UTF_8);
        var base64Buf = ByteBuffer.wrap(blueprintBytes, 1, blueprintBytes.length - 1);
        inflater.setInput(Base64.getDecoder().decode(base64Buf));
        byte[] buf = new byte[1024];
        while(inflater.getRemaining() > 0) {
            int read = inflater.inflate(buf);
            sb.append(new String(buf, 0, read));
        }
        var blueprint = (JSONObject)new JSONParser().parse(sb.toString());
        var entities = (JSONArray)((JSONObject)blueprint.get("blueprint")).get("entities");

        Map<Integer, ConnectedCombinator> combinatorMap = new HashMap<>();
        for(int e = 0; e < entities.size(); e++) {
            var entity = (JSONObject)entities.get(e);
            parseCombinator((String)entity.get("name"), entity)
                    .ifPresent(combinator -> combinatorMap.put(combinator.getEntityId(), combinator));
        }
        NetworkGroup.calculateNetworkGroups();
        for(ConnectedCombinator combinator : combinatorMap.values()) {
            if(combinator.getCombinator().isOutputOnly()) {
                combinator.setRedOut(NetworkGroup.getNetworkGroup(combinator.getEntityId(), CIRCUIT_PRIMARY, COLOR_RED));
                combinator.setGreenOut(NetworkGroup.getNetworkGroup(combinator.getEntityId(), CIRCUIT_PRIMARY, COLOR_GREEN));
            }
            else {
                combinator.setRedIn(NetworkGroup.getNetworkGroup(combinator.getEntityId(), CIRCUIT_PRIMARY, COLOR_RED));
                combinator.setGreenIn(NetworkGroup.getNetworkGroup(combinator.getEntityId(), CIRCUIT_PRIMARY, COLOR_GREEN));
                combinator.setRedOut(NetworkGroup.getNetworkGroup(combinator.getEntityId(), CIRCUIT_SECONDARY, COLOR_RED));
                combinator.setGreenOut(NetworkGroup.getNetworkGroup(combinator.getEntityId(), CIRCUIT_SECONDARY, COLOR_GREEN));
            }
        }

        return combinatorMap.values();
    }

    private static Optional<ConnectedCombinator> parseCombinator(String type, JSONObject entity) {
        int ownId = get(entity, "entity_number");
        JSONObject controlBehavior = get(entity, "control_behavior");
        JSONObject connections = get(entity, "connections");
        JSONObject incoming = get(connections, "1");
        if(incoming != null) {
            JSONArray red = get(incoming, "red");
            if(red != null) {
                for(var o : red) {
                    JSONObject connection = (JSONObject) o;
                    int entityId = get(connection, "entity_id");
                    Integer circuitId = get(connection, "circuit_id");
                    circuitId = (circuitId == null) ? 1 : circuitId;
                    NetworkGroup.addConnection(ownId, CIRCUIT_PRIMARY, entityId, circuitId, COLOR_RED);
                }
            }
            JSONArray green = get(incoming, "green");
            if(green != null) {
                for(var o : green) {
                    JSONObject connection = (JSONObject) o;
                    int entityId = get(connection, "entity_id");
                    Integer circuitId = get(connection, "circuit_id");
                    circuitId = (circuitId == null) ? 1 : circuitId;
                    NetworkGroup.addConnection(ownId, CIRCUIT_PRIMARY, entityId, circuitId, COLOR_GREEN);
                }
            }
        }
        JSONObject outgoing = get(connections, "2");
        if(outgoing != null) {
            JSONArray red = get(outgoing, "red");
            if(red != null) {
                for(var o : red) {
                    JSONObject connection = (JSONObject) o;
                    int entityId = get(connection, "entity_id");
                    Integer circuitId = get(connection, "circuit_id");
                    circuitId = (circuitId == null) ? 1 : circuitId;
                    NetworkGroup.addConnection(ownId, CIRCUIT_SECONDARY, entityId, circuitId, COLOR_RED);
                }
            }
            JSONArray green = get(outgoing, "green");
            if(green != null) {
                for(var o : green) {
                    JSONObject connection = (JSONObject) o;
                    int entityId = get(connection, "entity_id");
                    Integer circuitId = get(connection, "circuit_id");
                    circuitId = (circuitId == null) ? 1 : circuitId;
                    NetworkGroup.addConnection(ownId, CIRCUIT_SECONDARY, entityId, circuitId, COLOR_GREEN);
                }
            }
        }

        switch (type) {
            case "arithmetic-combinator": {
                JSONObject conditions = get(controlBehavior, "arithmetic_conditions");
                Accessor secondIn;
                if(conditions.containsKey("second_signal")) {
                    secondIn = parseAccessor(get(conditions, "second_signal"));
                }
                else {
                    secondIn = Accessor.constant(get(conditions, "second_constant"));
                }
                var operation = ArithmeticCombinator.getOperation(get(conditions, "operation"));

                boolean isEach = "signal-each".equals(get(get(conditions, "first_signal"), "name"));
                if(!isEach) {
                    Accessor firstIn = parseAccessor(get(conditions, "first_signal"));
                    int output = Signal.getSignalId(get(get(conditions, "output_signal"), "name"));

                    return Optional.of(new ConnectedCombinator(ownId, ArithmeticCombinator.withLeftRight(firstIn, secondIn, output, operation)));
                }
                else {
                    boolean isOutEach = "signal-each".equals(get(get(conditions, "output_signal"), "name"));

                    if(isOutEach) {
                        return Optional.of(new ConnectedCombinator(ownId, ArithmeticCombinator.withEach(secondIn, operation)));
                    }
                    else {
                        int output = Signal.getSignalId(get(get(conditions, "output_signal"), "name"));
                        return Optional.of(new ConnectedCombinator(ownId, ArithmeticCombinator.withEachMerge(secondIn, output, operation)));
                    }
                }
            }
            case "decider-combinator": {
                JSONObject conditions = get(controlBehavior, "decider_conditions");
                String firstName = get(get(conditions, "first_signal"), "name");
                Accessor secondIn;
                if(conditions.containsKey("second_signal")) {
                    secondIn = parseAccessor(get(conditions, "second_signal"));
                }
                else {
                    secondIn = Accessor.constant(get(conditions, "constant"));
                }
                boolean outCopy = get(conditions, "copy_count_from_input");
                var operator = DeciderCombinator.getOperation(get(conditions, "comparator"));

                switch (firstName) {
                    case "signal-each": {
                        boolean isOutEach = "signal-each".equals(get(get(conditions, "output_signal"), "name"));
                        if(isOutEach) {
                            return Optional.of(new ConnectedCombinator(ownId, DeciderCombinator.withEach(secondIn, !outCopy, operator)));
                        }
                        else {
                            int output = Signal.getSignalId(get(get(conditions, "output_signal"), "name"));
                            return Optional.of(new ConnectedCombinator(ownId, DeciderCombinator.withEach(secondIn, output, !outCopy, operator)));
                        }
                    }
                    case "signal-everything": {
                        return Optional.of(new ConnectedCombinator(ownId, DeciderCombinator.withEvery(secondIn, parseWriter(get(conditions, "output_signal"), !outCopy), operator)));
                    }
                    case "signal-anything": {
                        return Optional.of(new ConnectedCombinator(ownId, DeciderCombinator.withAny(secondIn, parseWriter(get(conditions, "output_signal"), !outCopy), operator)));
                    }
                    default: {
                        Accessor firstIn = parseAccessor(get(conditions, "first_signal"));
                        return Optional.of(new ConnectedCombinator(ownId, DeciderCombinator.withLeftRight(firstIn, secondIn, parseWriter(get(conditions, "output_signal"), !outCopy), operator)));
                    }
                }
            }
            case "constant-combinator": {
                JSONArray arr = get(controlBehavior, "filters");
                Map<Integer, Integer> values = new HashMap<>();
                for(Object o : arr) {
                    int id = Signal.getSignalId(get(get((JSONObject) o, "signal"), "name"));
                    int count = get((JSONObject) o, "count");
                    values.put(id, count);
                }
                if(values.isEmpty()) return Optional.of(new ConnectedCombinator(ownId, Combinator.constant(Signal.EMPTY)));
                int[] valueArr = new int[Signal.SIGNAL_TYPES.get()];
                for(var e :  values.entrySet()) {
                    valueArr[e.getKey()] = e.getValue();
                }
                return Optional.of(new ConnectedCombinator(ownId, Combinator.constant(Signal.multiValue(valueArr))));
            }
        }
        return Optional.empty();
    }

    private static <T> T get(JSONObject obj, String key, Class<T> type) {
        return (T)obj.get(key);
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

    private static Accessor parseAccessor(JSONObject obj) {
        String type = get(obj, "type");
        if("virtual".equals(type)) {
            int signalId = Signal.getSignalId(get(obj, "name"));
            return Accessor.signal(signalId);
        }
        throw new UnsupportedOperationException("Unknown signal type " + type);
    }

    private static Writer parseWriter(JSONObject obj, boolean one) {
        String type = get(obj, "type");
        if("virtual".equals(type)) {
            String name = get(obj, "name");
            if("signal-everything".equals(name)) return Writer.everything(one);
            else if(one) return Writer.constant(Signal.getSignalId(name), 1);
            else return Writer.fromInput(Signal.getSignalId(name));
        }
        throw new UnsupportedOperationException("Unknown signal type " + type);
    }

    private static NetworkGroup getNetwork(int id, Map<Integer, NetworkGroup> map) {
        var group = map.get(id);
        if(group != null) return group;
        else {
            group = new NetworkGroup();
            map.put(id, group);
            return group;
        }
    }
}
