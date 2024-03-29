package me.joba.factorio;

import me.joba.factorio.game.entities.CircuitNetworkEntity;
import me.joba.factorio.lang.VariableAccessor;

import java.util.ArrayList;
import java.util.List;

public class CombinatorGroup {

    private final List<CircuitNetworkEntity> combinators = new ArrayList<>();
    private final List<NetworkGroup> networks = new ArrayList<>();
    private final List<VariableAccessor> accessors = new ArrayList<>();
    private final NetworkGroup input, output;
    private String correspondingCode;
    private final List<CombinatorGroup> subGroups = new ArrayList<>();

    public CombinatorGroup(NetworkGroup input, NetworkGroup output) {
        this.input = input;
        this.output = output;
        if(input != null) this.networks.add(input);
        if(output != null && input != output) this.networks.add(output);
    }

    public List<CircuitNetworkEntity> getCombinators() {
        return combinators;
    }

    public List<NetworkGroup> getNetworks() {
        return networks;
    }

    public List<VariableAccessor> getAccessors() {
        return accessors;
    }

    public List<CombinatorGroup> getSubGroups() {
        return subGroups;
    }

    public NetworkGroup getInput() {
        return input;
    }

    public NetworkGroup getOutput() {
        return output;
    }
}
