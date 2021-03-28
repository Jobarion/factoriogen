package me.joba.factorio;

import java.util.*;

public class NetworkGroup {

    private static Map<NetworkGroup, Set<NetworkGroup>> mergedMap = new HashMap<>();


    private final String name;

    public NetworkGroup() {
        this(null);
    }

    public NetworkGroup(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

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

    public static Set<NetworkGroup> getMerged(NetworkGroup g) {
        return mergedMap.getOrDefault(g, Collections.emptySet());
    }

     public static boolean isEqual(NetworkGroup a, NetworkGroup b) {
        if(a == b) return true;
        if(a == null || b == null) return false;
         return mergedMap.getOrDefault(a, Collections.emptySet()).contains(b);
     }
}
