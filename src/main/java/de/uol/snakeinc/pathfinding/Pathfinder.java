package de.uol.snakeinc.pathfinding;

import java.util.List;

public abstract class Pathfinder {

    private PathCell[][] cells;

    public Pathfinder(PathCell[][] cells) {
        this.cells = cells;
    }

    public abstract List<PathCell> findPath(PathCell start, PathCell end);

}
