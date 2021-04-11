package me.joba.factorio.game;

import org.json.simple.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Entity {

    private static AtomicInteger entityIdCounter = new AtomicInteger(0);

    private final String name;
    private final int entityId;
    private int x, y, orientation;
    private String description;

    public Entity(String name) {
        this.name = name;
        this.entityId = entityIdCounter.getAndIncrement();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEntityId() {
        return entityId;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONObject pos = new JSONObject();
        json.put("name", name);
        json.put("position", pos);
        json.put("direction", getOrientation());
        json.put("entity_number", getEntityId());
        pos.put("x", getX());
        pos.put("y", getY());
        extendJson(json);
        return json;
    }

    protected void extendJson(JSONObject json) {

    }
}
