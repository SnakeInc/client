package de.uol.snakeinc.pathfinding.astar;

import de.uol.snakeinc.pathfinding.PathCell;
import org.antlr.v4.runtime.misc.NotNull;

public class AStarNode implements Comparable {

    private AStarNode parent;
    private PathCell cell;
    private double cost;
    private double distance;

    public AStarNode(AStarNode parent, PathCell cell, double cost, double distance) {
        this.parent = parent;
        this.cell = cell;
        this.cost = cost;
        this.distance = distance;
    }

    public PathCell getCell() {
        return this.cell;
    }

    public AStarNode getParent() {
        return this.parent;
    }

    public double getCost() {
        return this.cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getDistance() {
        return this.distance;
    }

    // Compare by f value (g + h)
    @Override
    public int compareTo(Object o) {
        AStarNode that = (AStarNode) o;
        return (int)((this.cost + this.distance) - (that.cost + that.distance));
    }
}
