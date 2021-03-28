package me.joba.factorio;

import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONObject;

public interface Accessor {

    boolean isConstant();
    Object toJson();

    static Accessor constant(int val) {
        return new Accessor() {
            @Override
            public boolean isConstant() {
                return true;
            }

            @Override
            public Object toJson() {
                return val;
            }
        };
    }

    static Accessor signal(FactorioSignal sid) {
        return new Accessor() {
            @Override
            public boolean isConstant() {
                return false;
            }

            @Override
            public Object toJson() {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "virtual");
                jsonObject.put("name", sid.getFactorioName());
                return jsonObject;
            }
        };
    }
}
