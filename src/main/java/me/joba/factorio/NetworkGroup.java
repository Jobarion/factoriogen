package me.joba.factorio;

import java.util.*;

public class NetworkGroup {

    private static Map<NetworkGroup, NetworkGroup> canonicalMap = new HashMap<>();


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
        Set<NetworkGroup> previousCanonicals = new HashSet<>();
        for(int i = 0; i < groups.length; i++) {
            var canonical = getCanonical(groups[i]);
            if(canonical != groups[i]) {
                previousCanonicals.add(canonical);
            }
        }

        NetworkGroup promotedCanonical = groups[0];

        for(var e : new HashSet<>(canonicalMap.entrySet())) {
            if(previousCanonicals.contains(e.getValue())) {
                canonicalMap.put(e.getKey(), promotedCanonical);
            }
        }

        for(NetworkGroup group : previousCanonicals) {
            canonicalMap.put(group, promotedCanonical);
        }

        for(NetworkGroup group : groups) {
            canonicalMap.put(group, promotedCanonical);
        }
    }

    public static NetworkGroup getCanonical(NetworkGroup ng) {
        return canonicalMap.getOrDefault(ng, ng);
    }

    public static boolean isEqual(NetworkGroup a, NetworkGroup b) {
        if(a == b) return true;
        if(a == null || b == null) return false;
        return getCanonical(a) == getCanonical(b);
    }
}
