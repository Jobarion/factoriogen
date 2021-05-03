package me.joba.factorio;

import me.joba.factorio.game.EntityBlock;
import me.joba.factorio.graph.MSTSolver;
import me.joba.factorio.graph.Tuple;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;

public class BlueprintWriter {

    public static String writeBlueprint(List<EntityBlock> blocks) {
        JSONArray entities = new JSONArray();

        for(var block : blocks) {
            for(var entity : block.getEntities()) {
                entities.add(entity.toJson());
            }
        }

        JSONObject blueprint = new JSONObject();
        blueprint.put("icons", new JSONArray());
        blueprint.put("entities", entities);
        blueprint.put("item", "blueprint");
        blueprint.put("version", 281474976710656L);
        JSONObject root = new JSONObject();
        root.put("blueprint", blueprint);
        String outString = root.toJSONString();
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
