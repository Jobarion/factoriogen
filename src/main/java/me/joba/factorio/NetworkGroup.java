package me.joba.factorio;

import java.util.*;

public class NetworkGroup {

    private static Map<NetworkGroup, Set<NetworkGroup>> mergedMap = new HashMap<>();

    public static void merge(NetworkGroup... groups) {
        Set<NetworkGroup> totalGroup = new HashSet<>();
        for(var group : groups) {
            totalGroup.add(group);
            totalGroup.addAll(mergedMap.getOrDefault(group, Collections.emptySet()));
        }
        for(var group : totalGroup) {
            mergedMap.put(group, totalGroup);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkGroup that = (NetworkGroup) o;
        return mergedMap.getOrDefault(this, Collections.emptySet()).contains(that);
    }
}
