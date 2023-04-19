package me.joba.factorio;

import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONObject;

public interface Writer {

    Signal writeValue(Signal red, Signal green);
    boolean isConstant();
    JSONObject toJson();

    static Writer constant(int channel, int val) {
        return new Writer() {
            @Override
            public Signal writeValue(Signal red, Signal green) {
                return Signal.singleValue(channel, val);
            }

            @Override
            public boolean isConstant() {
                return true;
            }

            @Override
            public JSONObject toJson() {
                JSONObject obj = new JSONObject();
                obj.put("type", "virtual");
                obj.put("name", FactorioSignal.values()[channel].getFactorioName());
                return obj;
            }
        };
    }

    static Writer fromInput(int channel) {
        return new Writer() {
            @Override
            public Signal writeValue(Signal red, Signal green) {
                return Signal.singleValue(channel, red.get(channel) + green.get(channel));
            }

            @Override
            public boolean isConstant() {
                return false;
            }

            @Override
            public JSONObject toJson() {
                JSONObject obj = new JSONObject();
                obj.put("type", "virtual");
                obj.put("name", FactorioSignal.values()[channel].getFactorioName());
                return obj;
            }
        };
    }

    static Writer everything(boolean one) {
        return new Writer() {
            @Override
            public Signal writeValue(Signal red, Signal green) {
                if(!one) {
                    return Signal.merge(red, green);
                }
                int[] count = new int[Signal.SIGNAL_TYPES.get()];
                for(int i = 0; i < count.length; i++) {
                    int val = red.get(i) + green.get(i);
                    if(val != 0) count[i] = 1;
                }
                return Signal.multiValue(count);
            }

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
