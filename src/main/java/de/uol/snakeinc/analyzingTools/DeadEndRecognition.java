package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.Common;
import de.uol.snakeinc.Config;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import static de.uol.snakeinc.Common.generateXY;
import static de.uol.snakeinc.Common.generateXYAllDirections;
import static de.uol.snakeinc.Common.offBoardOrDeadly;

@Log4j2
public class DeadEndRecognition {

    Cell[][] cells;
    Player us;
    ArrayList<Gate> gates;
    int width;
    int height;
    double mapCellCount;

    /**
     * Todo this.
     * @param cells todo
     * @param us todo
     */
    public DeadEndRecognition(Cell[][] cells, Player us) {
        this.cells = cells;
        this.us = us;
        this.width = cells.length;
        this.height = cells[1].length;
        this.mapCellCount = width * height;
        this.gates = new ArrayList<>();
    }

    /**
     * todo this.
     */
    public void findDeadEnds() {
        Direction dir = us.getDirection();
        int x = us.getX();
        int y = us.getY();
        int speed = us.getSpeed();
        calculateRisk(x, y, 1, speed, dir);
        calcDeadEndSize();
    }

    private void calculateRisk(int x, int y, int depth, int speed, Direction dir) {
        if (depth <= Config.DER_SEARCHING_DEPTH) {
            //Recursive call
            recursiveRiskByDirection(x, y, depth, speed, dir);
            recursiveRiskByDirection(x, y, depth, speed, Common.turnLeft(dir));
            recursiveRiskByDirection(x, y, depth, speed, Common.turnRight(dir));
            if (speed + 1 < Config.SPEED_MAX) {
                recursiveRiskByDirection(x, y, depth, speed + 1, dir);
            }
            if (speed - 1 > Config.SPEED_MIN) {
                recursiveRiskByDirection(x, y, depth, speed - 1, dir);
            }
        }
    }

    private void recursiveRiskByDirection(int x, int y, int depth, int speed, Direction dir) {
        boolean abort = false;
        for (int j = 1; j < speed + 1; j++) {
            var tuple = generateXY(dir, x, y, j);
            int xval = tuple.getX();
            int yval = tuple.getY();

            if (offBoardOrDeadly(xval, yval, cells)) {
                abort = true;
                break;
            } else {
                if (testForGate(xval, yval, dir)) {
                    abort = true;
                }
            }
        }
        var tuple = generateXY(dir, x, y, speed);

        if (!abort) {
            calculateRisk(tuple.getX(), tuple.getY(), depth + 1, speed, dir);
        }
    }

    private boolean testForGate(int x, int y, Direction dir) {
        switch (dir) {
            case UP:
            case DOWN:
                if (offBoardOrDeadly(x - 1, y, cells) && offBoardOrDeadly(x + 1, y, cells)) {
                    gates.add(new Gate(dir, x, y));
                    gates.add(new Gate(Common.turnLeft(Common.turnLeft(dir)), x, y));
                    return true;
                } else {
                    return false;
                }
            case LEFT:
            case RIGHT:
                if (offBoardOrDeadly(x, y - 1, cells) && offBoardOrDeadly(x, y + 1, cells)) {
                    gates.add(new Gate(dir, x, y));
                    gates.add(new Gate(Common.turnLeft(Common.turnLeft(dir)), x, y));
                    return true;
                } else {
                    return false;
                }
            default:
                throw new IllegalStateException();
        }
    }


    @AllArgsConstructor
    @Getter
    private static class Gate {
        private Direction dir;
        private int x;
        private int y;
    }

    private void calcDeadEndSize() {
        for (Gate gate : gates) {
            Set<Cell> cellsTested = new HashSet<>();
            Stack<Cell> cellsToTest = new Stack<>();
            int startX = gate.getX();
            int startY = gate.getY();
            cellsTested.add(cells[startX][startY]);
            Direction dir = gate.getDir();
            switch (dir) {
                case UP:
                    startY--;
                    break;
                case DOWN:
                    startY++;
                    break;
                case LEFT:
                    startX--;
                    break;
                case RIGHT:
                    startX++;
                    break;
                default:
                    throw new IllegalStateException();
            }
            if (!offBoardOrDeadly(startX, startY, cells)) {
                findNeighbours(startX, startY, cellsTested, cellsToTest);
            }
        }
    }

    private void findNeighbours(int x, int y, Set<Cell> cellsTested, Stack<Cell> cellsToTest) {
        log.debug("Calculating Gate: " + x + " - " + y);
        int deadEndCellCount = 1;
        cellsToTest.add(cells[x][y]);
        int toTestCount = 1;
        while (toTestCount > 0 && deadEndCellCount < (mapCellCount / 4)) {
            Cell cell = cellsToTest.pop();
            toTestCount--;
            int xCell = cell.getY();
            int yCell = cell.getX();
            if (!(cellsTested.contains(cell) || cellsToTest.contains(cell) || offBoardOrDeadly(xCell, yCell, cells))) {
                cellsTested.add(cell);
                deadEndCellCount++;
                //test up

                for (var tuple : generateXYAllDirections(xCell,yCell,1)) {
                    if (!offBoardOrDeadly(tuple.getX(), tuple.getY(), cells)) {
                        var nextCell = cells[tuple.getX()][tuple.getY()];
                        if (!(cellsTested.contains(cell) || cellsToTest.contains(cell))) {
                            cellsToTest.add(nextCell);
                            toTestCount++;
                        }

                    }
                }
            }
        }
        double deadEndRisk;
        if ((deadEndCellCount < (mapCellCount / 4)) && deadEndCellCount > 1) {
            deadEndRisk = -deadEndCellCount * (1 / (mapCellCount / 4)) + 2;
            for (Cell testedCell : cellsTested) {
                testedCell.setDeadEndRisk(deadEndRisk);
            }
            log.debug("Gate size: " + deadEndCellCount + " - Risk: " + deadEndRisk);
        }
    }
}
