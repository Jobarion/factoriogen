package me.joba.factorio.lang;

import me.joba.factorio.CombinatorGroup;
import me.joba.factorio.NetworkGroup;
import me.joba.factorio.game.combinators.ArithmeticCombinator;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class VariableAccessor {

    private final Map<NetworkGroup, AccessibleVariable> accessedList;
    private final Variable variable;
    private boolean generated = false;

    public VariableAccessor(Variable variable) {
        this.variable = variable;
        this.accessedList = new HashMap<>();
    }

    public void generateAccessors() {
        if(generated) return;
        generated = true;
        if(accessedList.isEmpty()) return;
        var connectionNetwork = new NetworkGroup();
        variable.getProducer().getNetworks().add(connectionNetwork);
        generateAccurateAccessors();
    }

    private void generateAccurateAccessors() {

        Set<AccessibleVariable> ignored = new HashSet<>();
        boolean allIgnored = true;
        for(var accessing : accessedList.values()) {
            if(accessing.delay == variable.getTickDelay()) {
                ignored.add(accessing);
                NetworkGroup.merge(accessing.networkGroup, variable.getProducer().getOutput());
            }
            else {
                allIgnored = false;
            }
        }

        System.out.println("Generated " + ignored.size() + " accessors via direct access.");

        if(allIgnored) return;

        int minDelay = Integer.MAX_VALUE;
        int maxDelay = Integer.MIN_VALUE;

        for(var a : accessedList.values()) {
            if(ignored.contains(a)) continue;
            minDelay = Math.min(minDelay, a.delay);
            maxDelay = Math.max(maxDelay, a.delay);
        }

        if(minDelay < variable.getTickDelay()) {
            throw new RuntimeException("Unsupported requested access time of " + variable + " with delay " + minDelay + " (has " + variable.getTickDelay() + ")");
        }

        int sharedAccessorChain = minDelay - variable.getTickDelay() - 1;
        System.out.println("Shared accessors: " + sharedAccessorChain);

        System.out.println("Delay timing difference " + minDelay + " " + maxDelay);

        NetworkGroup output = variable.getProducer().getOutput();

        boolean done = false;
        while(!done) {
            done = true;
            for(var entry : accessedList.values()) {
                if(ignored.contains(entry)) continue;
                if(entry.delay == variable.getTickDelay()) continue;
                if(entry.delay == variable.getTickDelay() + 1) {
                    var connected = ArithmeticCombinator.copying(variable.getSignal()[0]);
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
                var connected = ArithmeticCombinator.copying(variable.getSignal()[0]);
                connected.setGreenIn(output);
                output = new NetworkGroup();
                connected.setGreenOut(output);
                variable.getProducer().getCombinators().add(connected);
                variable.getProducer().getNetworks().add(output);
            }
        }
    }

    public AccessibleVariable access(int delay) {
        if(delay < variable.getTickDelay()) {
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
            var previous = accessedList.put(networkGroup, this);
            if(previous != null) {
                if(previous.delay != delay) throw new RuntimeException("Accessing variable from same group with different delays");
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AccessibleVariable that = (AccessibleVariable) o;
            return networkGroup.equals(that.networkGroup);
        }

        @Override
        public int hashCode() {
            return Objects.hash(networkGroup);
        }
    }
}
