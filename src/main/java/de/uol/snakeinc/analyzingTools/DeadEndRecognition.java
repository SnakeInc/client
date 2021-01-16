package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.Common;
import static de.uol.snakeinc.Common.generateAllXYUpToFromOne;
import static de.uol.snakeinc.Common.generateXY;
import static de.uol.snakeinc.Common.turnLeft;
import static de.uol.snakeinc.Common.turnRight;
import de.uol.snakeinc.Config;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;


@Log4j2
public class DeadEndRecognition {

    Cell[][] cells;
    HistoryMap historyMap;
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
        this.historyMap = new HistoryMap(getDeepCopyOfMap());
    }

    /**
     * method to start.
     */
    public void findDeadEnds() {
        Direction usDir = us.getDirection();
        int usX = us.getX();
        int usY = us.getY();
        int usSpeed = us.getSpeed();
        testPossibleMoves(usDir, usX, usY, usSpeed);
        if (Config.RANDOM_TESTING) {
            testRandom(usDir, usX, usY);
        }
    }

    /**
     * tests for a dead end in every direction.
     * @param direction Direction
     * @param x x coordinate
     * @param y y coordinate
     */
    private void testRandom(Direction direction, int x, int y) {
        Common.Tuple t1 = generateXY(direction, x, y, 6);
        if (!offBoardOrDeadly(t1.getX(), t1.getY())) {
            findNeighbours(t1.getX(), t1.getY(), historyMap.getMap());
        }
        Common.Tuple t2 = generateXY(turnLeft(direction), x, y, 6);
        if (!offBoardOrDeadly(t2.getX(), t2.getY())) {
            findNeighbours(t2.getX(), t2.getY(), historyMap.getMap());
        }
        Common.Tuple t3 = generateXY(turnLeft(turnLeft(direction)), x, y, 6);
        if (!offBoardOrDeadly(t3.getX(), t3.getY())) {
            findNeighbours(t3.getX(), t3.getY(), historyMap.getMap());
        }
        Common.Tuple t4 = generateXY(turnRight(direction), x, y, 6);
        if (!offBoardOrDeadly(t4.getX(), t4.getY())) {
            findNeighbours(t4.getX(), t4.getY(), historyMap.getMap());
        }
    }

    /**
     * method to test all possible moves.
     * @param direction Direction
     * @param x x coordinate
     * @param y y coordinate
     * @param speed our speed
     */
    private void testPossibleMoves(Direction direction, int x, int y, int speed) {
        testMove(direction, x, y, speed);
        testMove(turnLeft(direction), x, y, speed);
        testMove(turnRight(direction), x, y, speed);
        if (speed > 1) {
            testMove(direction, x, y, speed - 1);
        }
        if (speed < 9) {
            testMove(direction, x, y, speed + 1);
        }
    }

    /**
     * tests a single move.
     * calls testRoundOfLine().
     * @param direction Direction
     * @param x x coordinate
     * @param y y coordinate
     * @param speed our speed
     */
    private void testMove(Direction direction, int x, int y, int speed) {
        Common.Tuple[] tuple = generateAllXYUpToFromOne(direction, x, y, speed + 1);
        for (Common.Tuple t: tuple) {
            if (isNotOffBoard(t.getX(), t.getY())) {
                historyMap.setDeadly(t.getX(), t.getY());
            }
        }
        testRoundOfLine(tuple, direction);
        historyMap.resetMap();
    }

    /**
     * always starting with the highest cell and going clockwise.
     * calls testRoundOfCell() only once per area.
     * @param tuplesInLine tuples that would be blocked by that action
     * @param direction Direction
     */
    private void testRoundOfLine(Common.Tuple[] tuplesInLine, Direction direction) {
        ArrayList<Common.Tuple> toList = new ArrayList<>(Arrays.asList(tuplesInLine));
        ArrayList<Common.Tuple> tuples = getLineOffPosition(toList, direction);
        AtomicBoolean alreadyChecked = new AtomicBoolean(false);
        tuples.forEach((neighbour) -> {
            if (isNotOffBoard(neighbour.getX(), neighbour.getY())) {
                if (isNotDeadly(neighbour.getX(), neighbour.getY())) {
                    if (!alreadyChecked.get()) {
                        testRoundOfCell(neighbour.getX(), neighbour.getY());
                        alreadyChecked.set(true);
                    }
                } else {
                    alreadyChecked.set(false);
                }
            }
        });
    }

    /**
     * calculates all tuples around tuplesInLine.
     * starting with y as lowest possible and x same as first tuple of line.
     * @param tuples tuples
     * @param direction Direction
     * @return returns a clockwise ArrayList around the input tuple
     */
    private ArrayList<Common.Tuple> getLineOffPosition(ArrayList<Common.Tuple> tuples, Direction direction) {
        ArrayList<Common.Tuple> tuplesToReturn = new ArrayList<>();
        int speed = tuples.size();
        int firstXOnLine = tuples.get(0).getX();
        int firstYOnLine = tuples.get(0).getY();
        switch (direction) {
            case UP:
                tuplesToReturn.add(new Common.Tuple(firstXOnLine, firstYOnLine - speed));
                for (int i = -1; i <= speed; i++) {
                    tuplesToReturn.add(new Common.Tuple(firstXOnLine + 1, firstYOnLine + i));
                }
                tuplesToReturn.add(new Common.Tuple(firstXOnLine, firstYOnLine + 1));
                for (int i = -1; i <= speed; i++) {
                    tuplesToReturn.add(new Common.Tuple(firstXOnLine - 1, firstYOnLine - i));
                }
                break;
            case DOWN:
                tuplesToReturn.add(new Common.Tuple(firstXOnLine, firstYOnLine - 1));
                for (int i = -1; i <= speed; i++) {
                    tuplesToReturn.add(new Common.Tuple(firstXOnLine + 1, firstYOnLine + i));
                }
                tuplesToReturn.add(new Common.Tuple(firstXOnLine, firstYOnLine + speed));
                for (int i = speed; i >= -1; i--) {
                    tuplesToReturn.add(new Common.Tuple(firstXOnLine - 1, firstYOnLine + i));
                }
                break;
            case LEFT:
                tuplesToReturn.add(new Common.Tuple(firstXOnLine, firstYOnLine - 1));
                tuplesToReturn.add(new Common.Tuple(firstXOnLine + 1, firstYOnLine - 1));
                tuplesToReturn.add(new Common.Tuple(firstXOnLine + 1, firstYOnLine));
                for (int i = -1; i <= speed; i++) {
                    tuplesToReturn.add(new Common.Tuple(firstXOnLine - i, firstYOnLine + 1));
                }
                tuplesToReturn.add(new Common.Tuple(firstXOnLine - speed, firstYOnLine));
                for (int i = speed; i >= 1; i--) {
                    tuplesToReturn.add(new Common.Tuple(firstXOnLine - i, firstYOnLine - 1));
                }
                break;
            case RIGHT:
                tuplesToReturn.add(new Common.Tuple(firstXOnLine, firstYOnLine - 1));
                for (int i = 1; i <= speed; i++) {
                    tuplesToReturn.add(new Common.Tuple(firstXOnLine + i, firstYOnLine - 1));
                }
                tuplesToReturn.add(new Common.Tuple(firstXOnLine + speed, firstYOnLine));
                for (int i = speed; i >= -1; i--) {
                    tuplesToReturn.add(new Common.Tuple(firstXOnLine + i, firstYOnLine + 1));
                }
                tuplesToReturn.add(new Common.Tuple(firstXOnLine - 1, firstYOnLine));
                tuplesToReturn.add(new Common.Tuple(firstXOnLine - 1, firstYOnLine - 1));
                break;
            default:
                throw new IllegalStateException();
        }
        return tuplesToReturn;
    }

    /**
     * calculates all tuples around a tuple.
     * always starting with the highest cell and going clockwise.
     * calls findNeighbours() only once per area.
     * @param x x
     * @param y y
     */
    private void testRoundOfCell(int x, int y) {
        if (isNotOffBoard(x, y)) {
            boolean areaAlreadyTested = false;
            for (int i = 0; i <= 7; i++) {
                Common.Tuple tuple = getRoundOfPosition(x, y, i);
                if (isNotOffBoard(tuple.getX(), tuple.getY())) {
                    if (isNotDeadly(tuple.getX(), tuple.getY())) {
                        if (!areaAlreadyTested) {
                            findNeighbours(tuple.getX(), tuple.getY(), historyMap.getMap());
                            areaAlreadyTested = true;
                        }
                    } else {
                        areaAlreadyTested = false;
                    }
                }
            }
        }
    }

    /**
     * helper method for testRoundOfCell().
     * return a tuple clockwise around a tuple based on depth.
     * @param x x of the tuple
     * @param y y of the tuple
     * @param depth 0-7 position of the cycle around a tuple
     * @return a tuple
     */
    private Common.Tuple getRoundOfPosition(int x, int y, int depth) {
        int newX = x;
        int newY = y;
        switch (depth) {
            case 0:
                newY--;
                break;
            case 1:
                newY--;
                newX++;
                break;
            case 2:
                newX++;
                break;
            case 3:
                newX++;
                newY++;
                break;
            case 4:
                newY++;
                break;
            case 5:
                newY++;
                newX--;
                break;
            case 6:
                newX--;
                break;
            case 7:
                newY--;
                newX--;
                break;
            default:
                throw new IllegalStateException();
        }
        return new Common.Tuple(newX, newY);
    }

    /**
     * deepcopy method for cells.
     * @return a copy of cells
     */
    private Cell[][] getDeepCopyOfMap() {
        Cell[][] newCells = new Cell[cells.length][cells[0].length];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[0].length; j++) {
                newCells[i][j] = new Cell(cells[i][j]);
            }
        }
        return newCells;
    }

    /**
     * helper class.
     * double cell array with revertible changes
     */
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
            changedCells.add(map[x][y]);
        }

        public void resetMap() {
            changedCells.forEach(Cell::setNotDeadly);
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
            return historyMap.getMap()[x][y].isDeadly();
        }
    }

    /**
     * tests if coordinates are deadly.
     * @param x x coordinate
     * @param y y coordinate
     * @return returns true if deadly
     */
    private boolean isNotDeadly(int x, int y) {
        return !historyMap.getMap()[x][y].isDeadly();
    }

    /**
     * tests if coordinates are on the board.
     * @param x x coordinate
     * @param y y coordinate
     * @return returns true if not on the board
     */
    private boolean isNotOffBoard(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * calculates the risk for each gate.
     * goes from 2 to 1 based on the size of the dead end.
     * @param x x
     * @param y y
     * @param map cells array
     */
    private void findNeighbours(int x, int y, Cell[][] map) {
        Set<Cell> cellsTested = new HashSet<>();
        Stack<Cell> cellsToTest = new Stack<>();
        log.debug("Calculating: " + x + " : " + y);
        int deadEndCellCount = 1;
        cellsToTest.add(map[x][y]);
        int toTestCount = 1;
        while (toTestCount > 0 && deadEndCellCount < Config.MAX_RECOGNITION_CELL_COUNT) {
            Cell cell = cellsToTest.pop();
            toTestCount--;
            int xCell = cell.getX();
            int yCell = cell.getY();
            if (!(cellsTested.contains(cell) || cellsToTest.contains(cell) || offBoardOrDeadly(xCell, yCell))) {
                cellsTested.add(cell);
                deadEndCellCount++;
                //test up
                if (!offBoardOrDeadly(xCell, (yCell - 1))) {
                    Cell nextCell = map[xCell][(yCell - 1)];
                    cellsToTest.add(nextCell);
                    toTestCount++;
                }
                //test right
                if (!offBoardOrDeadly((xCell + 1), yCell)) {
                    Cell nextCell = map[(xCell + 1)][yCell];
                    cellsToTest.add(nextCell);
                    toTestCount++;
                }
                //test down
                if (!offBoardOrDeadly(xCell, (yCell + 1))) {
                    Cell nextCell = map[xCell][(yCell + 1)];
                    cellsToTest.add(nextCell);
                    toTestCount++;
                }
                //test left
                if (!offBoardOrDeadly((xCell - 1), yCell)) {
                    Cell nextCell = map[(xCell - 1)][yCell];
                    cellsToTest.add(nextCell);
                    toTestCount++;
                }
            }
        }
        double deadEndRisk;
        if ((deadEndCellCount < Config.MAX_RECOGNITION_CELL_COUNT)) {
            deadEndRisk = Config.LN_MULTIPLIER * -Math.log(deadEndCellCount) + Config.FUNCTION_Y_OFFSET;
            cellsTested.forEach((testedCell) -> {
                cells[testedCell.getX()][testedCell.getY()].setDeadEndRisk(deadEndRisk);
            });
            log.debug("Size: " + deadEndCellCount + " - Risk: " + deadEndRisk);
        }
    }
}
