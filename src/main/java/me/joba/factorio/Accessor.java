package me.joba.factorio;

import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONObject;

public interface Accessor {

    int getValue(Signal red, Signal green);
    boolean isConstant();
    Object toJson();

    static Accessor constant(int val) {
        return new Accessor() {
            @Override
            public int getValue(Signal red, Signal green) {
                return val;
            }

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
        return signal(sid.ordinal());
    }

    static Accessor signal(int sid) {
        return new Accessor() {
            @Override
            public int getValue(Signal red, Signal green) {
                return red.get(sid) + green.get(sid);
            }

            @Override
            public boolean isConstant() {
                return false;
            }

            @Override
            public Object toJson() {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", "virtual");
                jsonObject.put("name", FactorioSignal.values()[sid].getFactorioName());
                return jsonObject;
            }
        };
    }
}
