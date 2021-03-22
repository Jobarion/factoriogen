package me.joba.factorio.lang;

import me.joba.factorio.ArithmeticCombinator;
import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.ConnectedCombinator;
import me.joba.factorio.NetworkGroup;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class VariableAccessor {

    private static final boolean ACCURATE_TIMING = true;

    private final Map<CombinatorGroup, AccessibleVariable> accessedList;
    private final NamedVariable variable;
    private boolean generated = false;

    public VariableAccessor(NamedVariable variable) {
        this.variable = variable;
        this.accessedList = new HashMap<>();
    }

    public void generateAccessors() {
        if(generated) return;
        generated = true;
        if(accessedList.isEmpty()) return;
        var connectionNetwork = new NetworkGroup();
        variable.getProducer().getNetworks().add(connectionNetwork);

        if(ACCURATE_TIMING) {
            generateAccurateAccessors();
        }
        else {
            generateInaccurateAccessors();
        }
    }

    private void generateInaccurateAccessors() {
        for(var entry : accessedList.values()) {
            var cmb = ArithmeticCombinator.copying(variable.getSignal());
            var connected = new ConnectedCombinator(cmb);
            connected.setGreenIn(variable.getProducer().getOutput());
            connected.setGreenOut(entry.networkGroup);
            entry.combinatorGroup.getCombinators().add(connected);
        }
    }

    private void generateAccurateAccessors() {
        int minDelay = Integer.MAX_VALUE;
        int maxDelay = Integer.MIN_VALUE;

        for(var a : accessedList.values()) {
            minDelay = Math.min(minDelay, a.delay);
            maxDelay = Math.max(maxDelay, a.delay);
        }

        if(minDelay < variable.getTickDelay() + 1) {
            throw new RuntimeException("Unsupported requested access time of " + variable + " with delay " + minDelay + " (has " + (variable.getTickDelay() + 1) + ")");
        }

        int sharedAccessorChain = minDelay - variable.getTickDelay() - 1;
        System.out.println("Shared accessors: " + sharedAccessorChain);

        System.out.println("Delay timing difference " + minDelay + " " + maxDelay);

        //TODO: Transform an accessor chain like (1 and 2 are the signals that are passed on, * means every signal)
        // 1 -> 1 -> 1 -> 1 -> 1, and 2 -> 2 -> 2 to * -> * -> * -> * -> 1
        //                                                  -> 2

        NetworkGroup output = variable.getProducer().getOutput();

        boolean done = false;
        while(!done) {
            done = true;
            for(var entry : accessedList.values()) {
                if(entry.delay == variable.getTickDelay()) continue;
                if(entry.delay == variable.getTickDelay() + 1) {
                    var cmb = ArithmeticCombinator.copying(variable.getSignal());
                    var connected = new ConnectedCombinator(cmb);
                    connected.setGreenIn(output);
                    connected.setGreenOut(entry.networkGroup);
                    entry.combinatorGroup.getCombinators().add(connected);
                }
                else {
                    done = false;
                }
                entry.delay--;
            }
            if(!done) {
                var cmb = ArithmeticCombinator.copying(variable.getSignal());
                var connected = new ConnectedCombinator(cmb);
                connected.setGreenIn(output);
                output = new NetworkGroup();
                connected.setGreenOut(output);
                variable.getProducer().getCombinators().add(connected);
                variable.getProducer().getNetworks().add(output);
            }
        }
    }

    public AccessibleVariable access(int delay) {
        if(delay < variable.getTickDelay() + 1) {
            throw new RuntimeException("Illegal access");
        }
        return new AccessibleVariable(delay);
    }

    public class AccessibleVariable implements Consumer<CombinatorGroup>, BiConsumer<NetworkGroup, CombinatorGroup> {

        private boolean accessed = false;
        private NetworkGroup networkGroup;
        private CombinatorGroup combinatorGroup;
        private int delay;

        public AccessibleVariable(int delay) {
            this.delay = delay;
        }

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
            this.networkGroup = networkGroup;
            this.combinatorGroup = combinatorGroup;
            var previous = accessedList.put(this.combinatorGroup, this);
            if(previous != null) {
                if(previous.delay != delay) throw new RuntimeException("Accessing variable from same group with different delays");
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AccessibleVariable that = (AccessibleVariable) o;
            return combinatorGroup.equals(that.combinatorGroup);
        }

        @Override
        public int hashCode() {
            return Objects.hash(combinatorGroup);
        }
    }
}
