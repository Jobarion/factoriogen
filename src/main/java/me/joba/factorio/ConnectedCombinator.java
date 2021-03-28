package me.joba.factorio;

public class ConnectedCombinator {

    private static int entityIdCounter = 0;

    private Combinator combinator;
    private NetworkGroup redIn, greenIn;
    private NetworkGroup redOut, greenOut;
    private int entityId;
    private String name;

    public ConnectedCombinator(Combinator combinator) {
        this(entityIdCounter++, combinator);
    }

    public ConnectedCombinator(int entityId, Combinator combinator) {
        this.combinator = combinator;
        this.entityId = entityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Combinator getCombinator() {
        return combinator;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public void setRedIn(NetworkGroup red) {
        this.redIn = red;
    }

    public void setGreenIn(NetworkGroup green) {
        this.greenIn = green;
    }

    public void setRedOut(NetworkGroup red) {
        this.redOut = red;
    }

    public void setGreenOut(NetworkGroup green) {
        this.greenOut = green;
    }

    public NetworkGroup getRedIn() {
        return redIn;
    }

    public NetworkGroup getGreenIn() {
        return greenIn;
    }

    public NetworkGroup getRedOut() {
        return redOut;
    }

    public NetworkGroup getGreenOut() {
        return greenOut;
    }
}
