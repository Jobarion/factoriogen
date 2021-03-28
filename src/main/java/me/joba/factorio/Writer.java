package me.joba.factorio;

import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONObject;

public interface Writer {

    boolean isConstant();
    JSONObject toJson();

    static Writer one(FactorioSignal outSignal) {
        return new Writer() {
            @Override
            public boolean isConstant() {
                return true;
            }

            @Override
            public JSONObject toJson() {
                JSONObject obj = new JSONObject();
                obj.put("type", "virtual");
                obj.put("name", outSignal.getFactorioName());
                return obj;
            }
        };
    }

    static Writer fromInput(FactorioSignal outSignal) {
        return new Writer() {

            @Override
            public boolean isConstant() {
                return false;
            }

            @Override
            public JSONObject toJson() {
                JSONObject obj = new JSONObject();
                obj.put("type", "virtual");
                obj.put("name", outSignal.getFactorioName());
                return obj;
            }
        };
    }

    static Writer everything(boolean one) {
        return new Writer() {

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
        };
    }
}
