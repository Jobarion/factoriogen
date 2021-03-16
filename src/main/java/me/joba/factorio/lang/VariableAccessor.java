package me.joba.factorio.lang;

import me.joba.factorio.ArithmeticCombinator;
import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.ConnectedCombinator;
import me.joba.factorio.NetworkGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class VariableAccessor {

    private final Map<CombinatorGroup, NetworkGroup> accessedList;
    private final NamedVariable variable;

    public VariableAccessor(NamedVariable variable) {
        this.variable = variable;
        this.accessedList = new HashMap<>();
    }

    public void generateAccessors() {
        if(accessedList.isEmpty()) return;
        var connectionNetwork = new NetworkGroup();
        variable.getProducer().getNetworks().add(connectionNetwork);
        for(var entry : accessedList.entrySet()) {
            var cmb = ArithmeticCombinator.copying(variable.getSignal());
            var connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(variable.getProducer().getOutput());
            connected.setGreenOut(entry.getValue());
            entry.getKey().getCombinators().add(connected);
        }
    }

    public AccessibleVariable access() {
        return new AccessibleVariable();
    }

    public class AccessibleVariable implements Consumer<CombinatorGroup>, BiConsumer<NetworkGroup, CombinatorGroup> {

        private boolean accessed = false;

        @Override
        public void accept(CombinatorGroup group) {
            accept(group.getInput(), group);
        }

        @Override
        public void accept(NetworkGroup networkGroup, CombinatorGroup combinatorGroup) {
            if(accessed) {
                throw new RuntimeException("AccessibleVariable accessed from multiple networks");
            }
            accessed = true;
            accessedList.put(combinatorGroup, networkGroup);
        }
    }
}
