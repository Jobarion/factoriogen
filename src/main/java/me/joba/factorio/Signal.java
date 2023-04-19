package me.joba.factorio;

import me.joba.factorio.lang.FactorioSignal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public interface Signal {

    Signal EMPTY = x -> 0;

    int get(int sid);

    static Signal singleValue(int sid, int val) {
        return x -> x == sid ? val : 0;
    }

    static Signal multiValue(int[] values) {
        return x -> values.length > x ? values[x] : 0;
    }

    static Signal merge(Signal... a) {
        if(a.length == 1) return a[0];
        return merge(Arrays.asList(a));
    }

    static Signal merge(Iterable<Signal> iter) {
        int[] values = new int[SIGNAL_TYPES.get()];
        int count = 0;
        int nonNullId = 0;
        for(int i = 0; i < SIGNAL_TYPES.get(); i++) {
            int val = 0;
            for(Signal s : iter) {
                val += s.get(i);
            }
            values[i] = val;
            if(val != 0) {
                count++;
                nonNullId = i;
            }
        }
        if(count > 1) {
            return multiValue(values);
        }
        else {
            return singleValue(nonNullId, values[nonNullId]);
        }
    }

    Map<String, Integer> signalTypes = new HashMap<>();
    Map<Integer, String> factorioTypes = new HashMap<>();
    AtomicInteger SIGNAL_TYPES = new AtomicInteger(0);

    static int getSignalId(String signal) {
        Integer id = signalTypes.get(signal);
        if(id == null) {
            id = SIGNAL_TYPES.getAndIncrement();
            signalTypes.put(signal, id);
            factorioTypes.put(id, signal);
        }
        return id;
    }

    static String getFactorioId(int signalId) {
        return factorioTypes.get(signalId);
    }
}
