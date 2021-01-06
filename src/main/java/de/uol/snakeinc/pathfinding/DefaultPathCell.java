package de.uol.snakeinc.pathfinding;

public class DefaultPathCell extends PathCell {

    private boolean inUse;

    public DefaultPathCell(boolean inUse, int x, int y) {
        super(x, y);
        this.inUse = inUse;
    }

    @Override
    public boolean isInUse() {
        return this.inUse;
    }
}
