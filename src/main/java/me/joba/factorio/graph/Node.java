package me.joba.factorio.graph;

import java.util.List;
import java.util.Set;

public class Node {

    private int x, y;
    private final int id;
    private final Set<Integer> neighbors;

    public Node(int id, Set<Integer> neighbors) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.neighbors = neighbors;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getId() {
        return id;
    }

    public Set<Integer> getNeighbors() {
        return neighbors;
    }

    public boolean isNeighbor(Node b) {
        return getNeighbors().contains(b.getId());
    }

    @Override
    public String toString() {
        return "Node{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
