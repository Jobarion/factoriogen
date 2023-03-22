package me.joba.factorio.game.entities;

import me.joba.factorio.NetworkGroup;
import me.joba.factorio.game.WireColor;
import me.joba.factorio.lang.FactorioSignal;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class IOCircuitNetworkEntity extends CircuitNetworkEntity implements CircuitNetworkInput, CircuitNetworkOutput {


    private final Map<FactorioSignal, Integer> output = new HashMap<>();
    private final Map<FactorioSignal, Integer> input = new HashMap<>();
    private final Function<Map<FactorioSignal, Integer>, Map<FactorioSignal, Integer>> function;

    public IOCircuitNetworkEntity(String name, Function<Map<FactorioSignal, Integer>, Map<FactorioSignal, Integer>> function) {
        super(name, 2, 11.2);
        this.function = function;
    }

    //Temporary to make transition easier
    @Override
    public void setRedIn(NetworkGroup red) {
        setNetwork(0, WireColor.RED, red);
    }

    @Override
    public void setGreenIn(NetworkGroup green) {
        setNetwork(0, WireColor.GREEN, green);
    }

    @Override
    public void setRedOut(NetworkGroup red) {
        setNetwork(1, WireColor.RED, red);
    }

    @Override
    public void setGreenOut(NetworkGroup green) {
        setNetwork(1, WireColor.GREEN, green);
    }

    @Override
    public NetworkGroup getRedIn() {
        return getNetwork(0, WireColor.RED);
    }

    @Override
    public NetworkGroup getGreenIn() {
        return getNetwork(0, WireColor.GREEN);
    }

    @Override
    public NetworkGroup getRedOut() {
        return getNetwork(1, WireColor.RED);
    }

    @Override
    public NetworkGroup getGreenOut() {
        return getNetwork(1, WireColor.GREEN);
    }

    @Override
    public Map<FactorioSignal, Integer> getOutput() {
        return output;
    }

    @Override
    public void gatherSignals() {
        Map<FactorioSignal, Integer> inGreen = getGreenIn() != null ? getGreenIn().getValues() : Map.of();
        Map<FactorioSignal, Integer>  inRed = getRedIn() != null ? getRedIn().getValues() : Map.of();
        input.clear();
        input.putAll(inGreen);
        for(var e : inRed.entrySet()) {
            input.compute(e.getKey(), (k, v) -> v == null ? e.getValue() : v + e.getValue());
        }
        input.values().removeIf(i -> i == 0);
    }

    @Override
    public void update() {
        output.clear();
        output.putAll(function.apply(input));
        output.values().removeIf(i -> i == 0);
    }
}
