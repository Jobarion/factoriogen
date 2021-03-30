package me.joba.factorio;

import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface Combinator {

    default boolean isOutputOnly() {
        return true;
    }
    default String getFactorioName() {
        return "constant-combinator";
    }
    default JSONObject createJson() {
        JSONObject obj = new JSONObject();
        obj.put("name", getFactorioName());
        obj.put("control_behavior", createControlBehaviorJson());
        return obj;
    }
    JSONObject createControlBehaviorJson();

    static Combinator constant(Map<FactorioSignal, Integer> signals) {
        return () -> {
            JSONObject cbehavior = new JSONObject();
            int index = 1;
            JSONArray filters = new JSONArray();
            for(var e : signals.entrySet()) {
                JSONObject filter = new JSONObject();
                filter.put("index", index++);
                filter.put("count", e.getValue());
                JSONObject signal = new JSONObject();
                signal.put("type", "virtual");
                signal.put("name", e.getKey().getFactorioName());
                filter.put("signal", signal);
                filters.add(filter);
            }

            cbehavior.put("filters", filters);
            return cbehavior;
        };
    }
}
