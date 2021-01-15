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
    ArrayList<Gate> gates;
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
        this.gates = new ArrayList<>();
    }

    public void findDeadEnds() {
        Direction dir = us.getDirection();
        int x = us.getX();
        int y = us.getY();
        int speed = us.getSpeed();
        calculateRisk(x, y, 1, speed, dir);
        calcDeadEndSize();
    }

    private void calculateRisk(int x, int y, int depth, int speed, Direction dir) {
        if (depth <= 3) {
            //Recursive call
            recursiveRiskByDirection(x, y, depth, speed, dir);
            recursiveRiskByDirection(x, y, depth, speed, turnLeft(dir));
            recursiveRiskByDirection(x, y, depth, speed, turnRight(dir));
            if(speed + 1 < 10) {
                recursiveRiskByDirection(x, y, depth, speed + 1, dir);
            }
            if(speed - 1 > 0) {
                recursiveRiskByDirection(x, y, depth, speed - 1, dir);
            }
        }
    }

    private void recursiveRiskByDirection(int x, int y, int depth, int speed, Direction dir) {
        boolean abort = false;
        switch (dir) {
            case UP:
                for (int j = 1; j < speed + 1; j++) {
                    if (offBoardOrDeadly(x, y - j)) {
                        abort = true;
                        break;
                    } else {
                        if(testForGate(x, y - j, dir, depth)) {
                            abort = true;
                        }
                    }
                }
                if (!abort) {
                    calculateRisk(x, y - speed, depth + 1, speed, dir);
                }
                break;
            case DOWN:
                for (int j = 1; j < speed + 1; j++) {
                    if (offBoardOrDeadly(x, y + j)) {
                        abort = true;
                        break;
                    } else {
                        if(testForGate(x, y + j, dir, depth)) {
                            abort = true;
                        }
                    }
                }
                if (!abort) {
                    calculateRisk(x, y + speed, depth + 1, speed, dir);
                }
                break;
            case RIGHT:
                for (int j = 1; j < speed + 1; j++) {
                    if (offBoardOrDeadly(x + j, y)) {
                        abort = true;
                        break;
                    } else {
                        if(testForGate(x + j, y, dir, depth)) {
                            abort = true;
                        }
                    }
                }
                if (!abort) {
                    calculateRisk(x + speed, y, depth + 1, speed, dir);
                }
                break;
            case LEFT:
                for (int j = 1; j  < speed + 1; j++) {
                    if (offBoardOrDeadly(x - j, y)) {
                        abort = true;
                        break;
                    } else {
                        if(testForGate(x - j, y, dir, depth)) {
                            abort = true;
                        }
                    }
                }
                if (!abort) {
                    calculateRisk(x - speed, y, depth + 1, speed, dir);
                }
                break;
            default:
                throw new IllegalStateException();
        }
    }

    private boolean testForGate(int x, int y, Direction dir, int depth) {
        switch (dir) {
            case UP:
            case DOWN:
                if(offBoardOrDeadly(x - 1, y) && offBoardOrDeadly(x + 1, y)) {
                    gates.add(new Gate(dir, x, y));
                    gates.add(new Gate(turnLeft(turnLeft(dir)), x, y));
                    return true;
                } else
                    return false;
                }
            case LEFT:
            case RIGHT:
                if(offBoardOrDeadly(x, y - 1) && offBoardOrDeadly(x, y + 1)) {
                    gates.add(new Gate(dir, x, y));
                    gates.add(new Gate(turnLeft(turnLeft(dir)), x, y));
                    return true;
                } else {
                    return false;
                }
            default:
                throw new IllegalStateException();
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

    @AllArgsConstructor
    @Getter
    private class Gate {
        private Direction dir;
        private int x;
        private int y;
    }

    private void calcDeadEndSize() {
        gates.forEach((gate) -> {
            Set<Cell> cellsTested = new HashSet<>();
            Stack<Cell> cellsToTest = new Stack<>();
            int startX = gate.getX();
            int startY = gate.getY();
            cellsTested.add(cells[startX][startY]);
            Direction dir = gate.getDir();
            switch (dir) {
                case UP:
                    startY--;
                case DOWN:
                    startY++;
                case LEFT:
                    startX--;
                case RIGHT:
                    startX++;
            }
            if(!offBoardOrDeadly(startX, startY)) {
                findNeighbours(startX, startY, cellsTested, cellsToTest);
            }
        });
    }

    private void findNeighbours(int x, int y, Set<Cell> cellsTested, Stack<Cell> cellsToTest) {
        log.debug("Calculating Gate: " + x + " - " + y);
        int deadEndCellCount = 1;
        cellsToTest.add(cells[x][y]);
        int toTestCount = 1;
        while(toTestCount > 0 && deadEndCellCount < (mapCellCount / 4)) {
            Cell cell = cellsToTest.pop();
            toTestCount--;
            int xCell = cell.getY();
            int yCell = cell.getX();
            if(!(cellsTested.contains(cell) || cellsToTest.contains(cell) || offBoardOrDeadly(xCell, yCell))) {
                cellsTested.add(cell);
                deadEndCellCount++;
                //test up
                if(!offBoardOrDeadly(xCell, (yCell - 1))) {
                    var nextCell = cells[xCell][(yCell - 1)];
                    if (!(cellsTested.contains(cell) || cellsToTest.contains(cell))) {
                        cellsToTest.add(nextCell);
                        toTestCount++;
                    }

                }
                //test right
                if(!offBoardOrDeadly((xCell + 1), yCell)) {
                    var nextCell = cells[(xCell + 1)][yCell];
                    if (!(cellsTested.contains(cell) || cellsToTest.contains(cell))) {
                        cellsToTest.add(nextCell);
                        toTestCount++;
                    }

                }
                //test down
                if(!offBoardOrDeadly(xCell, (yCell + 1))) {
                    var nextCell = cells[xCell][(yCell + 1)];
                    if (!(cellsTested.contains(cell) || cellsToTest.contains(cell))) {
                        cellsToTest.add(nextCell);
                        toTestCount++;
                    }

                }
                //test left
                if(!offBoardOrDeadly((xCell - 1), yCell)) {
                    var nextCell = cells[(xCell - 1)][yCell];
                    if (!(cellsTested.contains(cell) || cellsToTest.contains(cell))) {
                        cellsToTest.add(nextCell);
                        toTestCount++;
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
