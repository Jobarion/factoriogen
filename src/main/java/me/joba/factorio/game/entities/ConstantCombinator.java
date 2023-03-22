package me.joba.factorio.game.entities;

import me.joba.factorio.NetworkGroup;
import me.joba.factorio.game.WireColor;
import me.joba.factorio.lang.FactorioSignal;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Map;

public class ConstantCombinator extends CircuitNetworkEntity implements CircuitNetworkOutput {

    private final Map<FactorioSignal, Integer> signals;

    public ConstantCombinator(Map<FactorioSignal, Integer> signals) {
        super("constant-combinator", 1, 10.3);
        this.signals = signals;
    }

    @Override
    public void setRedOut(NetworkGroup red) {
        setNetwork(0, WireColor.RED, red);
    }

    @Override
    public void setGreenOut(NetworkGroup green) {
        setNetwork(0, WireColor.GREEN, green);
    }

    @Override
    public NetworkGroup getRedOut() {
        return getNetwork(0, WireColor.RED);
    }

    @Override
    public NetworkGroup getGreenOut() {
        return getNetwork(0, WireColor.GREEN);
    }

    @Override
    public void gatherSignals() {

    }

    @Override
    public void update() {

    }

    @Override
    public Map<FactorioSignal, Integer> getOutput() {
        return signals;
    }

    @Override
    protected void extendJson(JSONObject json) {
        super.extendJson(json);
        JSONObject controlBehavior = new JSONObject();
        int index = 1;
        JSONArray filters = new JSONArray();
        for(var e : signals.entrySet()) {
            JSONObject filter = new JSONObject();
            filter.put("index", index++);
            filter.put("count", e.getValue());
            JSONObject signal = new JSONObject();
            signal.put("type", e.getKey().getType().getFactorioName());
            signal.put("name", e.getKey().getFactorioName());
            filter.put("signal", signal);
            filters.add(filter);
        }
        controlBehavior.put("filters", filters);
        json.put("control_behavior", controlBehavior);
    }
}
