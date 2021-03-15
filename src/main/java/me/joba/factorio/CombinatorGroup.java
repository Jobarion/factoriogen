package me.joba.factorio;

import java.util.ArrayList;
import java.util.List;

public class CombinatorGroup {

    private final List<ConnectedCombinator> combinators = new ArrayList<>();
    private final List<NetworkGroup> networks = new ArrayList<>();
    private final NetworkGroup input, output;
    private String correspondingCode;

    public CombinatorGroup(NetworkGroup input, NetworkGroup output) {
        this.input = input;
        this.output = output;
        if(input != null) this.networks.add(input);
        if(input != output) this.networks.add(output);
    }

    public String getCorrespondingCode() {
        return correspondingCode;
    }

    public void setCorrespondingCode(String correspondingCode) {
        this.correspondingCode = correspondingCode;
    }

    public List<ConnectedCombinator> getCombinators() {
        return combinators;
    }

    public List<NetworkGroup> getNetworks() {
        return networks;
    }

    public NetworkGroup getInput() {
        return input;
    }

    public NetworkGroup getOutput() {
        return output;
    }
}
