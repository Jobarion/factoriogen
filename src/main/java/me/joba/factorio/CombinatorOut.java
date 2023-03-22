package me.joba.factorio;

import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.stream.Collectors;

public interface CombinatorOut {

    boolean isConstant();
    JSONObject toJson();
    Map<FactorioSignal, Integer> sample(Map<FactorioSignal, Integer> inputs);

    static CombinatorOut one(FactorioSignal outSignal) {
        return new CombinatorOut() {
            @Override
            public boolean isConstant() {
                return true;
            }

            @Override
            public JSONObject toJson() {
                JSONObject obj = new JSONObject();
                obj.put("type", outSignal.getType().getFactorioName());
                obj.put("name", outSignal.getFactorioName());
                return obj;
            }

            @Override
            public Map<FactorioSignal, Integer> sample(Map<FactorioSignal, Integer> inputs) {
                return Map.of(outSignal, 1);
            }
        };
    }

    static CombinatorOut fromInput(FactorioSignal outSignal) {
        return new CombinatorOut() {

            @Override
            public boolean isConstant() {
                return false;
            }

            @Override
            public JSONObject toJson() {
                JSONObject obj = new JSONObject();
                obj.put("type", outSignal.getType().getFactorioName());
                obj.put("name", outSignal.getFactorioName());
                return obj;
            }

            @Override
            public Map<FactorioSignal, Integer> sample(Map<FactorioSignal, Integer> inputs) {
                return Map.of(outSignal, inputs.getOrDefault(outSignal, 0));
            }
        };
    }

    static CombinatorOut everything(boolean one) {
        return new CombinatorOut() {

            @Override
            public boolean isConstant() {
                return one;
            }

            @Override
            public JSONObject toJson() {
                JSONObject obj = new JSONObject();
                obj.put("type", "virtual");
                obj.put("name", "signal-everything");
                return obj;
            }

            @Override
            public Map<FactorioSignal, Integer> sample(Map<FactorioSignal, Integer> inputs) {
                return inputs.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> one ? 1 : e.getValue()));
            }
        };
    }
}
