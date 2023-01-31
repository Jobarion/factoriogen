package me.joba.factorio;

import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONObject;

public interface CombinatorIn {

    boolean isConstant();
    Object toJson();

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
        };
    }
}
