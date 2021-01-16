package de.uol.snakeinc.deadendflooding.wayout;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import lombok.Getter;

public class WayOutNode implements Comparable<WayOutNode> {

    @Getter
    private WayOutNode parent;
    @Getter
    private int speed;
    @Getter
    private Direction direction;
    @Getter
    private Cell cell;

    public WayOutNode(WayOutNode parent, int speed, Direction direction, Cell cell) {
        this.parent = parent;
        this.speed = speed;
        this.direction = direction;
        this.cell = cell;
    }

    @Override
    public int compareTo(WayOutNode node) {
        return node.getCell() == this.cell &&
            node.getSpeed() == this.speed &&
            node.getParent() == this.parent &&
            node.getDirection() == this.direction ? 0 : 1;
    }
}
