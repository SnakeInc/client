package de.uol.snakeinc.pathfinding;

import lombok.Getter;

public abstract class PathCell {

    @Getter
    private int x;
    @Getter
    private int y;

    public PathCell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract boolean isInUse();
}
