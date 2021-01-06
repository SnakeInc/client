package de.uol.snakeinc.pathfinding;

public abstract class PathCell {

    private int x;
    private int y;

    public PathCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract boolean isInUse();

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

}
