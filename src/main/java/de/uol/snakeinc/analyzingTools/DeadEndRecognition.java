package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;

public class DeadEndRecognition {

    Cell[][] cells;
    Player us;
    BoardAnalyzer boardAnalyzer;
    ArrayList<Gate> gates;
    int width;
    int height;
    int mapCellCount;

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
                    return true;
                } else
                    return false;
            case LEFT:
            case RIGHT:
                if(offBoardOrDeadly(x, y - 1) && offBoardOrDeadly(x, y + 1)) {
                    gates.add(new Gate(dir, x, y));
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
            HashMap<Cell, Boolean> cellsTested = new HashMap<>();
            HashMap<Cell, Boolean> cellsToTest = new HashMap<>();
            int startX = gate.getX();
            int startY = gate.getY();
            cellsTested.put(cells[startX][startY], true);
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

    private void findNeighbours(int x, int y, HashMap<Cell, Boolean> cellsTested, HashMap<Cell, Boolean> cellsToTest) {
        int deadEndCellCount = 0;

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
