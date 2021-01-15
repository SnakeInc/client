package de.uol.snakeinc.pathfinder.astar;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.pathfinding.PathCell;
import de.uol.snakeinc.pathfinding.astar.AStarSearch;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class AStarSearchTest {

    @Test
    public void testPathfinderSuccessOneWay() {
        Cell[][] cells = new Cell[5][5];
        //# # # # # #
        //# 0 0 0 0 0
        //# 1 1 0 1 1
        //# 0 0 0 1 0
        //# 0 1 1 1 0
        //# 0 0 0 0 0
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                Cell cell = new Cell(x, y);
                if ((y == 1 && x == 0) ||
                    (y == 1 && x == 1) ||
                    (y == 1 && x == 3) ||
                    (y == 1 && x == 4) ||
                    (y == 2 && x == 3) ||
                    (y == 3 && x == 1) ||
                    (y == 3 && x == 2) ||
                    (y == 3 && x == 3)) {
                    cell.setId(1);
                }
                cells[x][y] = cell;
            }
        }
        AStarSearch search = new AStarSearch(cells);
        List<PathCell> pathCells = search.findPath(cells[0][0], cells[4][2]);
        assertTrue(pathCells != null);
        List<Cell> correctPath = new ArrayList<Cell>();
        correctPath.add(cells[0][0]);
        correctPath.add(cells[1][0]);
        correctPath.add(cells[2][0]);
        correctPath.add(cells[2][1]);
        correctPath.add(cells[2][2]);
        correctPath.add(cells[1][2]);
        correctPath.add(cells[0][2]);
        correctPath.add(cells[0][3]);
        correctPath.add(cells[0][4]);
        correctPath.add(cells[1][4]);
        correctPath.add(cells[2][4]);
        correctPath.add(cells[3][4]);
        correctPath.add(cells[4][4]);
        correctPath.add(cells[4][3]);
        correctPath.add(cells[4][2]);
        assertTrue(testCellsInUse(pathCells, correctPath));
    }

    @Test
    public void testPathfinderSuccessTwoWay() {
        Cell[][] cells = new Cell[5][5];
        //# # # # # #
        //# 0 0 0 0 0
        //# 1 1 0 1 1
        //# 0 0 0 0 0
        //# 0 1 1 1 0
        //# 0 0 0 0 0
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                Cell cell = new Cell(x, y);
                if ((y == 1 && x == 0) ||
                    (y == 1 && x == 1) ||
                    (y == 1 && x == 3) ||
                    (y == 1 && x == 4) ||
                    (y == 3 && x == 1) ||
                    (y == 3 && x == 2) ||
                    (y == 3 && x == 3)) {
                    cell.setId(1);
                }
                cells[x][y] = cell;
            }
        }
        AStarSearch search = new AStarSearch(cells);
        List<PathCell> pathCells = search.findPath(cells[0][0], cells[4][2]);
        assertTrue(pathCells != null);
        List<Cell> correctPath = new ArrayList<Cell>();
        correctPath.add(cells[0][0]);
        correctPath.add(cells[1][0]);
        correctPath.add(cells[2][0]);
        correctPath.add(cells[2][1]);
        correctPath.add(cells[2][2]);
        correctPath.add(cells[3][2]);
        correctPath.add(cells[4][2]);
        assertTrue(testCellsInUse(pathCells, correctPath));
    }

    @Test
    public void testPathfinderFailed() {
        Cell[][] cells = new Cell[5][5];
        //# # # # # #
        //# 0 0 0 0 0
        //# 1 1 1 1 1
        //# 0 0 0 0 0
        //# 0 1 1 1 0
        //# 0 0 0 0 0
        for (int x = 0; x < 5; x++) {
            for (int y = 0; y < 5; y++) {
                Cell cell = new Cell(x, y);
                if ((y == 1 && x == 0) ||
                    (y == 1 && x == 1) ||
                    (y == 1 && x == 2) ||
                    (y == 1 && x == 3) ||
                    (y == 1 && x == 4) ||
                    (y == 3 && x == 1) ||
                    (y == 3 && x == 2) ||
                    (y == 3 && x == 3)) {
                    cell.setId(1);
                }
                cells[x][y] = cell;
            }
        }
        AStarSearch search = new AStarSearch(cells);
        List<PathCell> pathCells = search.findPath(cells[0][0], cells[4][2]);
        assertTrue(pathCells == null);
    }

    private boolean testCellsInUse(List<PathCell> pathCells, List<Cell> rightPathCells) {
        if (rightPathCells.size() != pathCells.size()) {
            return false;
        }
        for (Cell cell : rightPathCells) {
            if (!pathCells.contains(cell)) {
                return false;
            }
        }
        return true;
    }

}
