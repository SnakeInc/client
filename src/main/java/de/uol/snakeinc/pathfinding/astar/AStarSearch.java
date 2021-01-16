package de.uol.snakeinc.pathfinding.astar;

import de.uol.snakeinc.pathfinding.PathCell;
import de.uol.snakeinc.pathfinding.Pathfinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Sebastian Diers
 * A*-Algorithm based on A*-Algorithm by rosettacode.org - https://rosettacode.org/wiki/A*_search_algorithm#Java.
 * Optimized to beeing used on cell-grid and for usage with objects.
 */
public class AStarSearch extends Pathfinder {

    private List<AStarNode> open;
    private List<AStarNode> closed;
    private List<AStarNode> path;
    private PathCell[][] maze;
    private AStarNode now;
    private PathCell start;
    private PathCell end;

    public AStarSearch(PathCell[][] cells) {
        super(cells);
        this.open = new ArrayList<>();
        this.closed = new ArrayList<>();
        this.path = new ArrayList<>();
        this.maze = cells;
    }

    @Override
    public List<PathCell> findPath(PathCell start, PathCell end, boolean checkEnd) {
        this.start = start;
        this.now = new AStarNode(null, start, 0, 0);
        this.end = end;
        this.closed.add(this.now);
        addNeighborsToOpenList(checkEnd);
        while (this.now.getCell().getX() != this.end.getX() || this.now.getCell().getY() != this.end.getY()) {
            if (this.open.isEmpty()) { // Nothing to examine
                return null;
            }
            this.now = this.open.get(0); // get first node (lowest f score)
            this.open.remove(0); // remove it
            //todo remove
            //--------------------
            //search visualisation
            //--------------------
            //var str = new StringBuilder("");
            //for (var row : maze) {
            //    for (var cell : row) {
            //        if(cell.isInUse()) {
            //            str.append("X");
            //        } else if (open.stream().map(AStarNode::getCell).anyMatch(cell::equals)) {
            //            str.append("o");
            //        } else if(closed.stream().map(AStarNode::getCell).anyMatch(cell::equals)) {
            //            str.append("c");
            //        } else if(now.getCell().equals(cell)) {
            //            str.append("n");
            //        } else {
            //            str.append(".");
            //        }
            //    }
            //    str.append("\n");
            //}
            //System.out.println(str.toString());
            this.closed.add(this.now); // and add to the closed
            addNeighborsToOpenList(checkEnd);
        }
        this.path.add(0, this.now);
        while (this.now.getCell().getX() != this.start.getX() || this.now.getCell().getY() != this.start.getY()) {
            this.now = this.now.getParent();
            this.path.add(0, this.now);
        }
        List<PathCell> cells = new ArrayList<>();
        for (AStarNode node : this.path) {
            cells.add(node.getCell());
        }
        return cells;
    }
    /*
     ** Looks in a given List<> for a node
     **
     ** @return (bool) NeighborInListFound
     */
    private static boolean findNeighborInList(List<AStarNode> array, AStarNode node) {
        return array.stream().anyMatch(
            (n) -> (n.getCell().getX() == node.getCell().getX() && n.getCell().getY() == node.getCell().getY())
        );
    }

    /*
     ** Calculate distance between this.now and dx and dy
     **
     ** @return (int) distance
     */
    private double distance(int dx, int dy) {
        return Math.abs(this.now.getCell().getX() + dx - this.end.getX()) + Math.abs(this.now.getCell().getY()
            + dy - this.end.getY()); // else return "Manhattan distance"
    }

    private void addNeighborsToOpenList(boolean checkEnd) {
        // TODO: For Pathfinding with jumps etc. implement options like jump/left/right instead of directions
        // Maybe extra algorithm
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                if (x != 0 && y != 0) {
                    continue; // skip if diagonal movement is not allowed
                }
                int mazePositionY = this.now.getCell().getY() + y;
                int mazePositionX = this.now.getCell().getX() + x;
                if (mazePositionY >= this.maze[0].length || mazePositionY < 0 ||
                    mazePositionX >= this.maze.length || mazePositionX < 0) {
                    continue; // skip if diagonal movement is not allowed
                }
                AStarNode node = new AStarNode(
                    this.now, maze[mazePositionX][mazePositionY],
                    this.now.getCost(),
                    this.distance(x, y)
                );
                if ((x != 0 || y != 0) // not this.now
                    && this.now.getCell().getX() + x >= 0
                    && this.now.getCell().getX() + x < this.maze.length // check maze boundaries
                    && this.now.getCell().getY() + y >= 0
                    && this.now.getCell().getY() + y < this.maze[0].length
                    // check if square is walkable
                    && !this.checkInUse(this.maze[this.now.getCell().getX() + x][this.now.getCell().getY() + y]
                    , checkEnd)
                    // if not already done
                    && !findNeighborInList(this.open, node) && !findNeighborInList(this.closed, node)) {
                    node.setCost(node.getParent().getCost() + 1.); // Horizontal/vertical cost = 1.0
                    this.open.add(node);
                }
            }
        }
        Collections.sort(this.open);
    }

    private boolean checkInUse(PathCell cell, boolean checkEnd) {
        if (!cell.isInUse() || (!checkEnd && this.end == cell)) {
            return false;
        }
        return true;
    }
}
