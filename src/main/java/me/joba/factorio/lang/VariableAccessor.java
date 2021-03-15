package me.joba.factorio.lang;

import me.joba.factorio.ArithmeticCombinator;
import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.ConnectedCombinator;
import me.joba.factorio.NetworkGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VariableAccessor {

    private final List<CombinatorGroup> accessedList;
    private final NamedVariable variable;

    public VariableAccessor(NamedVariable variable) {
        this.variable = variable;
        this.accessedList = new ArrayList<>();
    }

    public void generateAccessors() {
        if(accessedList.isEmpty()) return;
        var connectionNetwork = new NetworkGroup();
        variable.getProducer().getNetworks().add(connectionNetwork);
        for(CombinatorGroup group : accessedList) {
            var cmb = ArithmeticCombinator.copying(variable.getSignal());
            var connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(variable.getProducer().getOutput());
            connected.setGreenOut(group.getInput());
            group.getCombinators().add(connected);
        }
    }

    public Consumer<CombinatorGroup> access() {
        return new AccessibleVariable();
    }

    private class AccessibleVariable implements Consumer<CombinatorGroup> {

        private boolean accessed = false;

        @Override
        public void accept(CombinatorGroup group) {
            if(accessed) {
                throw new RuntimeException("AccessibleVariable accessed from multiple networks");
            }
            accessed = true;
            accessedList.add(group);
        }
    }
}
