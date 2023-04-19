package me.joba.factorio;

public class ConnectedCombinator {

    private static int entityIdCounter = 0;

    private Combinator combinator;
    private NetworkGroup redIn, greenIn;
    private NetworkGroup redOut, greenOut;
    private int entityId;

    public ConnectedCombinator(Combinator combinator) {
        this.combinator = combinator;
        this.entityId = entityIdCounter++;
    }

    public ConnectedCombinator(int entityId, Combinator combinator) {
        this.combinator = combinator;
        this.entityId = entityId;
    }

    public Combinator getCombinator() {
        return combinator;
    }

    public int getEntityId() {
        return entityId;
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

    public Signal tick() {
        Signal red = this.redIn != null ? this.redIn.getState() : Signal.EMPTY;
        Signal green = this.greenIn != null ? this.greenIn.getState() : Signal.EMPTY;
        Signal out = combinator.process(red, green);
        if(this.redOut != null) this.redOut.addOutput(combinator, out);
        if(this.greenOut != null) this.greenOut.addOutput(combinator, out);
        return out;
    }
}
