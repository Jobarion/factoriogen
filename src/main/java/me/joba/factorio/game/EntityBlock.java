package me.joba.factorio.game;

import java.util.List;

public class EntityBlock {

    private final List<? extends Entity> entities;
    private int minX, maxX, minY, maxY;

    public EntityBlock(List<? extends Entity> entities) {
        this.entities = entities;
        minX = Integer.MAX_VALUE;
        minY = Integer.MAX_VALUE;
        maxX = Integer.MIN_VALUE;
        maxY = Integer.MIN_VALUE;
        for(Entity e : entities) {
            minX = Math.min(e.getX(), minX);
            minY = Math.min(e.getY(), minY);
            maxX = Math.max(e.getX(), maxX);
            maxY = Math.max(e.getY(), maxY);
        }
    }

    public void applyOffset(int x, int y) {
        for(Entity e : entities) {
            e.setX(e.getX() + x);
            e.setY(e.getY() + y);
        }
        minX += x;
        maxX += x;
        minY += y;
        maxY += y;
    }

    public List<? extends Entity> getEntities() {
        return entities;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }
}
