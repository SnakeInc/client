package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.Common;
import static de.uol.snakeinc.Common.generateAllXYUpTo;
import static de.uol.snakeinc.Common.turnLeft;
import static de.uol.snakeinc.Common.turnRight;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.CustomLog;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

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

    public void findDeadEnds() {
        Direction usDir = us.getDirection();
        int usX = us.getY();
        int usY = us.getX();
        int usSpeed = us.getSpeed();
        testPossibleMoves(usDir, usX, usY, usSpeed);
    }

    private void testPossibleMoves(Direction direction, int x, int y, int speed) {
        testMove(direction, x, y, speed);
        testMove(turnLeft(direction), x, y, speed);
        testMove(turnRight(direction), x, y, speed);
        if(speed > 1) {
            testMove(direction, x, y, speed - 1);
        }
        if(speed < 9) {
            testMove(direction, x, y, speed + 1);
        }
    }

    private void testMove(Direction direction, int x, int y, int speed) {
        Common.Tuple[] tuple = generateAllXYUpTo(direction, x, y, speed);
        for(Common.Tuple t: tuple) {
            if(!isOffBoard(t.getX(), t.getY())) {
                historyMap.setDeadly(t.getX(), t.getY());
            }
        }
        testRoundOfLine(tuple);
        historyMap.resetMap();
    }

    private void testRoundOfLine(Common.Tuple[] tuplesInLine) {
        Set<Common.Tuple> toSet = new HashSet<>(Arrays.asList(tuplesInLine));
        Set<Common.Tuple> tuples = getLineOffPosition(toSet);
        boolean alreadyChecked = false;
        for (Common.Tuple neighbour : tuples) {
            if (!isOffBoard(neighbour.getX(), neighbour.getY())) {
                if(!isDeadly(neighbour.getX(), neighbour.getY()) && !alreadyChecked) {
                    testRoundOfCell(neighbour.getX(), neighbour.getY());
                    alreadyChecked = true;
                } else {
                    alreadyChecked = false;
                }
            }
        }
    }

    private Set<Common.Tuple> getLineOffPosition(Set<Common.Tuple> tuples) {
        Set<Common.Tuple> tuplesToReturn = new HashSet<>();
        Set<Common.Tuple> tuplesAlreadyAdded = new HashSet<>();
        for(Common.Tuple t : tuples) {
            Set<Common.Tuple> tmpTuple = testRoundOfCellReturnSet(t.getX(), t.getY());
            for(Common.Tuple tTmp : tmpTuple) {
                if(!tuplesAlreadyAdded.contains(tTmp) && !tuples.contains(tTmp)) {
                    tuplesToReturn.add(tTmp);
                    tuplesAlreadyAdded.add(tTmp);
                }
            }
        }
        return tuplesToReturn;
    }

    private Set<Common.Tuple> testRoundOfCellReturnSet(int x, int y) {
        Set<Common.Tuple> setToReturn = new HashSet<>();
        for(int i = 0; i <= 7; i++) {
            if(!isOffBoard(x, y)) {
                Common.Tuple tuple = getRoundOfPosition(x, y, i);
                setToReturn.add(tuple);
            }
        }
        setToReturn.forEach((c) -> {
            System.out.println("X: " + c.getX() + " - Y: " + c.getY());
        });
        return setToReturn;
    }

    private void testRoundOfCell(int x, int y) {
        if(!isOffBoard(x, y)) {
            boolean areaAlreadyTested = false;
            for(int i = 0; i <= 7; i++) {
                Common.Tuple tuple = getRoundOfPosition(x, y, i);
                if(!isOffBoard(tuple.getX(), tuple.getY())) {
                    if(!isDeadly(tuple.getX(), tuple.getY()) && !areaAlreadyTested) {
                        findNeighbours(tuple.getX(), tuple.getY(), historyMap.getMap());
                        areaAlreadyTested = true;
                    } else {
                        areaAlreadyTested = false;
                    }
                }
            }
        }
    }

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
            changedCells.add(map[x][y]);
        }

        public void resetMap() {
            changedCells.forEach(Cell::setNoneDeadly);
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
    private boolean isDeadly(int x, int y) {
        return historyMap.getMap()[x][y].isDeadly();
    }

    /**
     * tests if coordinates are on the board.
     * @param x x coordinate
     * @param y y coordinate
     * @return returns true if not on the board
     */
    private boolean isOffBoard(int x, int y) {
        return x < 0 || x >= width || y < 0 || y >= height;
    }

    /**
     * calculates the risk for each gate.
     * goes from 2 to 1 based on the size of the dead end.
     */
    private void findNeighbours(int x, int y, Cell[][] map) {
        Stack<Cell> cellsToTest = new Stack<>();
        Set<Cell> cellsTested = new HashSet<>();
        log.debug("Calculating at: " + x + " - " + y);
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
                cells[testedCell.getY()][testedCell.getX()].setDeadEndRisk(deadEndRisk);
            });
            log.debug("Size: " + deadEndCellCount + " - Risk: " + deadEndRisk);
        }
    }
}
