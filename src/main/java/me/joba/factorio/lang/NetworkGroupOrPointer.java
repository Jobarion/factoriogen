package me.joba.factorio.lang;

import me.joba.factorio.NetworkGroup;

public class NetworkGroupOrPointer {

    private final NetworkGroup networkGroup;
    private NetworkGroupOrPointer pointer;

    private NetworkGroupOrPointer(NetworkGroup networkGroup) {
        this.networkGroup = networkGroup;
    }

    private NetworkGroupOrPointer(NetworkGroupOrPointer pointer) {
        this.pointer = pointer;
        this.networkGroup = null;
    }

    public NetworkGroup resolve() {
        if(networkGroup != null) return networkGroup;
        return pointer.resolve();
    }

    public NetworkGroup getNetworkGroup() {
        return networkGroup;
    }

    public NetworkGroupOrPointer getPointer() {
        return pointer;
    }

    public void setPointer(NetworkGroupOrPointer pointer) {
        this.pointer = pointer;
    }

    public static NetworkGroupOrPointer group(NetworkGroup networkGroup) {
        return new NetworkGroupOrPointer(networkGroup);
    }

    public static NetworkGroupOrPointer pointer() {
        return new NetworkGroupOrPointer((NetworkGroup) null);
    }

    public static NetworkGroupOrPointer pointer(NetworkGroupOrPointer pointer) {
        return new NetworkGroupOrPointer(pointer);
    }
}
