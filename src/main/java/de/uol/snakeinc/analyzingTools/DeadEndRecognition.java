package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

@CustomLog
public class DeadEndRecognition {

    Cell[][] cells;
    Player us;
    BoardAnalyzer boardAnalyzer;
    int width;
    int height;
    double mapCellCount;

    public DeadEndRecognition(Cell[][] cells, Player us, BoardAnalyzer boardAnalyzer) {
        this.cells = cells;
        this.us = us;
        this.boardAnalyzer = boardAnalyzer;
        this.width = cells.length;
        this.height = cells[1].length;
        this.mapCellCount = width * height;
    }

    public void findDeadEnds() {
        Direction usDir = us.getDirection();
        int usX = us.getX();
        int usY = us.getY();
        int usSpeed = us.getSpeed();
        testPossibleMoves(usDir, usX, usY, usSpeed);
    }

    private void testPossibleMoves(Direction direction, int x, int y, int speed) {

    }

    private void testMove(Direction direction, int x, int y, int speed) {
        switch (direction) {
            case UP:

                break;
        }
    }

    private void testRoundOfLine() {

    }

    private void testRoundOfCell() {

    }

    private Cell[][] getDeepCopyOfMap() {
        Cell[][] newCells = new Cell[cells.length][cells[0].length];
        for(int i = 0; i < cells.length; i++) {
            for(int j = 0; j < cells[0].length; j++) {
                newCells[i][j] = cells[i][j].clone();
            }
        }
        return newCells;
    }

    @Getter
    class HistoryMap {

        private Cell[][] map;
        private Set<Cell> changedCells;

        public HistoryMap(Cell[][] map) {
            this.map = map;
            changedCells = new HashSet<>();
        }

        public void setDeadly(int x, int y) {
            map[x][y].setId(-1);

        }

    }

    /**
     * tests if coordinates are on the board or the cell is deadly.
     * @param x x coordinate
     * @param y y coordinate
     * @return returns the test value
     */
    private boolean offBoardOrDeadly(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return true;
        } else {
            return cells[x][y].isDeadly();
        }
    }

    /**
     * finds the start point for each gate.
     */
    private void calcDeadEndSize(Cell[][] map, Stack<Cell> cellsToTest, int startX, int startY) {
        if(!offBoardOrDeadly(startX, startY)) {
            findNeighbours(startX, startY, cellsToTest, map);
        }
    }

    /**
     * calculates the risk for each gate.
     * goes from 2 to 1 based on the size of the dead end.
     */
    private void findNeighbours(int x, int y, Stack<Cell> cellsToTest, Cell[][] map) {
        Set<Cell> cellsTested = new HashSet<>();
        log.debug("Calculating Gate: " + x + " - " + y);
        int deadEndCellCount = 1;
        cellsToTest.add(map[x][y]);
        while(!cellsToTest.empty() && deadEndCellCount < (mapCellCount / 4)) {
            Cell cell = cellsToTest.pop();
            int xCell = cell.getY();
            int yCell = cell.getX();
            if(!(cellsTested.contains(cell) || cellsToTest.contains(cell) || offBoardOrDeadly(xCell, yCell))) {
                cellsTested.add(cell);
                deadEndCellCount++;
                //test up
                if(!offBoardOrDeadly(xCell, (yCell - 1))) {
                    var nextCell = map[xCell][(yCell - 1)];
                    if(!(cellsTested.contains(cell) || cellsToTest.contains(cell))) {
                        cellsToTest.add(nextCell);
                    }

                }
                //test right
                if(!offBoardOrDeadly((xCell + 1), yCell)) {
                    var nextCell = map[(xCell + 1)][yCell];
                    if(!(cellsTested.contains(cell) || cellsToTest.contains(cell))) {
                        cellsToTest.add(nextCell);
                    }

                }
                //test down
                if(!offBoardOrDeadly(xCell, (yCell + 1))) {
                    var nextCell = map[xCell][(yCell + 1)];
                    if(!(cellsTested.contains(cell) || cellsToTest.contains(cell))) {
                        cellsToTest.add(nextCell);
                    }

                }
                //test left
                if(!offBoardOrDeadly((xCell - 1), yCell)) {
                    var nextCell = map[(xCell - 1)][yCell];
                    if(!(cellsTested.contains(cell) || cellsToTest.contains(cell))) {
                        cellsToTest.add(nextCell);
                    }
                }
            }
        }
        double deadEndRisk;
        if((deadEndCellCount < (mapCellCount / 4)) && deadEndCellCount > 1) {
                deadEndRisk = -deadEndCellCount * (1 / (mapCellCount / 4)) + 2;
            cellsTested.forEach((testedCell) -> {
                testedCell.setDeadEndRisk(deadEndRisk);
            });
            log.debug("Gate size: " + deadEndCellCount + " - Risk: " + deadEndRisk);
        }
    }

    /**
     * turns the direction to the left.
     * @param dir Direction
     * @return Direction
     */
    private Direction turnLeft(Direction dir) {
        switch (dir) {
            case UP:
                return Direction.LEFT;
            case DOWN:
                return Direction.RIGHT;
            case LEFT:
                return Direction.DOWN;
            case RIGHT:
                return Direction.UP;
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * turns the direction to the right.
     * @param dir Direction
     * @return Direction
     */
    private Direction turnRight(Direction dir) {
        switch (dir) {
            case UP:
                return Direction.RIGHT;
            case DOWN:
                return Direction.LEFT;
            case LEFT:
                return Direction.UP;
            case RIGHT:
                return Direction.DOWN;
            default:
                throw new IllegalStateException();
        }
    }
}
