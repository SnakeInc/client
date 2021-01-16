package de.uol.snakeinc.pathfinding.astar;

import de.uol.snakeinc.pathfinding.PathCell;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Sebastian Diers
 * A*-Node based on A*-Node by rosettacode.org - https://rosettacode.org/wiki/A*_search_algorithm#Java
 * Optimized to beeing used on cell-grid and for usage with objects.
 */
public class AStarNode implements Comparable<AStarNode> {

    @Getter
    private AStarNode parent;
    @Getter
    private PathCell cell;
    @Getter @Setter
    private double cost;
    private double distance;

    public AStarNode(AStarNode parent, PathCell cell, double cost, double distance) {
        this.parent = parent;
        this.cell = cell;
        this.cost = cost;
        this.distance = distance;
    }

    // Compare by f value (g + h)
    @Override
    public int compareTo(AStarNode node) {
        return (int)((this.cost + this.distance) - (node.cost + node.distance));
    }
}
