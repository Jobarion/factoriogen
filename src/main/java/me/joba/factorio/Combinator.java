package me.joba.factorio;

import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Combinator {

    Signal process(Signal red, Signal green);
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

    static Combinator constant(Signal out) {
        return new Combinator() {
            @Override
            public Signal process(Signal red, Signal green) {
                return out;
            }

            @Override
            public JSONObject createControlBehaviorJson() {
                JSONObject cbehavior = new JSONObject();
                int index = 1;
                JSONArray filters = new JSONArray();
                Set<Integer> alwaysIncludeList = new HashSet<>();
                for(FactorioSignal s : out.includeSignal()) {
                    alwaysIncludeList.add(s.ordinal());
                }
                for(int i = 0; i < Signal.SIGNAL_TYPES.get(); i++) {
                    int val = out.get(i);
                    if(val != 0 || alwaysIncludeList.contains(i)) {
                        JSONObject filter = new JSONObject();
                        filter.put("index", index++);
                        filter.put("count", val);
                        JSONObject signal = new JSONObject();
                        signal.put("type", "virtual");
                        signal.put("name", FactorioSignal.values()[i].getFactorioName());
                        filter.put("signal", signal);
                        filters.add(filter);
                    }
                }
                cbehavior.put("filters", filters);
                return cbehavior;
            }
        };
    }
}
