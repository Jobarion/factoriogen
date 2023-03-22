package me.joba.factorio;

import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONObject;

import java.util.Map;

public interface CombinatorIn {

    boolean isConstant();
    Object toJson();
    int sample(Map<FactorioSignal, Integer> inputs);

    static CombinatorIn constant(int val) {
        return new CombinatorIn() {
            @Override
            public boolean isConstant() {
                return true;
            }

            @Override
            public Object toJson() {
                return val;
            }

            @Override
            public int sample(Map<FactorioSignal, Integer> inputs) {
                return val;
            }
        };
    }

    static CombinatorIn signal(FactorioSignal sid) {
        return new CombinatorIn() {
            @Override
            public boolean isConstant() {
                return false;
            }

            @Override
            public Object toJson() {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", sid.getType().getFactorioName());
                jsonObject.put("name", sid.getFactorioName());
                return jsonObject;
            }

            @Override
            public int sample(Map<FactorioSignal, Integer> inputs) {
                return inputs.getOrDefault(sid, 0);
            }
        };
    }
}
