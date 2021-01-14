package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;
import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import static de.uol.snakeinc.Common.*;

@Log4j2
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
                if(offBoardOrDeadly(x - 1, y, cells) && offBoardOrDeadly(x + 1, y, cells)) {
                    gates.add(new Gate(dir, x, y));
                    gates.add(new Gate(turnLeft(turnLeft(dir)), x, y));
                    return true;
                } else
                    return false;
            case LEFT:
            case RIGHT:
                if(offBoardOrDeadly(x, y - 1, cells) && offBoardOrDeadly(x, y + 1, cells)) {
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
                case DOWN:
                    startY++;
                case LEFT:
                    startX--;
                case RIGHT:
                    startX++;
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
        while(toTestCount > 0 && deadEndCellCount < (mapCellCount / 4)) {
            Cell cell = cellsToTest.pop();
            toTestCount--;
            int xCell = cell.getY();
            int yCell = cell.getX();
            if(!(cellsTested.contains(cell) || cellsToTest.contains(cell) || offBoardOrDeadly(xCell, yCell, cells))) {
                cellsTested.add(cell);
                deadEndCellCount++;
                //test up

                for(var tuple : generateXYAllDirections(xCell,yCell,1)) {
                    if(!offBoardOrDeadly(tuple.getX(), tuple.getY(), cells)) {
                        var nextCell = cells[tuple.getX()][tuple.getY()];
                        if(!(cellsTested.contains(cell) || cellsToTest.contains(cell))) {
                            cellsToTest.add(nextCell);
                            toTestCount++;
                        }

                    }
                }
            }
        }
        double deadEndRisk;
        if((deadEndCellCount < (mapCellCount / 4)) && deadEndCellCount > 1) {
                deadEndRisk = -deadEndCellCount * (1 / (mapCellCount / 4)) + 2;
            for (Cell testedCell : cellsTested) {
                testedCell.setDeadEndRisk(deadEndRisk);
            }
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
