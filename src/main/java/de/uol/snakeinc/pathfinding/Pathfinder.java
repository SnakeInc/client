package de.uol.snakeinc.pathfinding;

import de.uol.snakeinc.entities.Cell;

import java.util.List;

public interface Pathfinder {

    public List<Cell> findPath(Cell[][] cells);

}
