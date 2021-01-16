package de.uol.snakeinc.pathfinding;

import java.util.List;

public abstract class Pathfinder {


    public Pathfinder(PathCell[][] cells) { }

    public abstract List<PathCell> findPath(PathCell start, PathCell end);

}
