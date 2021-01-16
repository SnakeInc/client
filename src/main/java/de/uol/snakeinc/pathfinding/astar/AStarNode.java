package de.uol.snakeinc.pathfinding.astar;

import de.uol.snakeinc.pathfinding.PathCell;
import lombok.Getter;
import lombok.Setter;

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
    public int compareTo(AStarNode that) {
        return (int)((this.cost + this.distance) - (that.cost + that.distance));
    }
}
