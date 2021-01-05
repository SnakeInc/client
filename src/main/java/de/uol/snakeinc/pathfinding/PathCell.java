package de.uol.snakeinc.pathfinding;

public class PathCell {

    private boolean inUse;
    private int x;
    private int y;

    public PathCell(boolean inUse, int x, int y) {
        this.inUse = inUse;
        this.x = x;
        this.y = y;
    }

    public boolean isInUse() {
        return this.inUse;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

}
