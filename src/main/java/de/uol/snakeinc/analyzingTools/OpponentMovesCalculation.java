package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import de.uol.snakeinc.entities.Tupel;
import lombok.CustomLog;

import java.util.HashSet;
import java.util.Set;

@CustomLog
public class OpponentMovesCalculation {

    private Cell[][] cells;
    private Player[] players;
    private Player us;
    private BoardAnalyzer boardAnalyzer;
    private int width;
    private int height;
    Set<Cell> evaluatedCells = new HashSet<>();


    public OpponentMovesCalculation(Cell[][] cells, Player[] players, Player us, BoardAnalyzer boardAnalyzer) {
        this.cells = cells;
        this.width = cells.length;
        this.height = cells[1].length;
        this.players = players;
        this.us = us;
        this.boardAnalyzer = boardAnalyzer;
    }

    public Set<Cell> evaluate() {
        int x;
        int y;
        int speed;
        for (Player player : players) {
            if (BoardAnalyzer.inDistance(us, player) && player.isActive()) {
                log.info("Computing Opponent MOves.");
                x = player.getX();
                y = player.getY();
                speed = player.getSpeed();
                nextDepth(x, y, 1, speed);
            }
        }
        return evaluatedCells;
    }



    private void nextDepth(int x, int y, int depth, int speed) {
        if (boardAnalyzer.checkForJumping(depth)) {
            nextDepthWithJumping(x, y, depth, speed);
        } else {
            if (depth <= 3) {
                //Recursive call
                recursiveRiskByDirection(x, y, depth, speed, Direction.UP);
                recursiveRiskByDirection(x, y, depth, speed, Direction.DOWN);
                recursiveRiskByDirection(x, y, depth, speed, Direction.LEFT);
                recursiveRiskByDirection(x, y, depth, speed, Direction.RIGHT);
            }
        }
    }

    private void nextDepthWithJumping(int x, int y, int depth, int speed) {

        if(depth <= 3) {
            //Recursive call
            recursiveRiskByDirectionWithJumping(x, y, depth, speed, Direction.UP);
            recursiveRiskByDirectionWithJumping(x, y, depth, speed, Direction.DOWN);
            recursiveRiskByDirectionWithJumping(x, y, depth, speed, Direction.LEFT);
            recursiveRiskByDirectionWithJumping(x, y, depth, speed, Direction.RIGHT);
        }
    }

    private void recursiveRiskByDirection(int x, int y, int depth, int speed, Direction dir) {
        boolean abort = false;
        switch (dir) {
            case UP:
                for (int j = 1; j < speed + 1; j++) {
                    if (y - j < 0 || y - j >= height) {
                        abort = true;
                        break;
                    } else if (cells[x][y - j].isDeadly()) {
                        abort = true;
                        break;
                    } else {
                        evaluatedCells.add(cells[x][y - j]);
                        cells[x][y - j].raiseActionRisk(depth);
                    }
                }
                if (!abort) {
                    nextDepth(x, y - speed, depth + 1, speed);
                }
            case DOWN:
                for (int j = 1; j + 1 < speed + 1; j++) {
                    if (y + j < 0 || y + j >= height) {
                        abort = true;
                        break;
                    } else if (cells[x][y + j].isDeadly()) {
                        abort = true;
                        break;
                    } else {
                        evaluatedCells.add(cells[x][y + j]);
                        cells[x][y + j].raiseActionRisk(depth);
                    }
                }
                if (!abort) {
                    nextDepth(x, y + speed, depth + 1, speed);
                }
            case RIGHT:
                for (int j = 1; j + 1 < speed + 1; j++) {
                    if (x + j < 0 || x + j >= width) {
                        abort = true;
                        break;
                    } else if (cells[x + j][y].isDeadly()) {
                        abort = true;
                        break;
                    } else {
                        evaluatedCells.add(cells[x + j][y]);
                        cells[x + j][y].raiseActionRisk(depth);
                    }
                }
                if (!abort) {
                    nextDepth(x + speed, y, depth + 1, speed);
                }
            case LEFT:
                for (int j = 1; j < speed + 1; j++) {
                    if (x - j < 0 || x - j >= width) {
                        abort = true;
                        break;
                    } else if (cells[x - j][y].isDeadly()) {
                        abort = true;
                        break;
                    } else {
                        evaluatedCells.add(cells[x - j][y]);
                        cells[x - j][y].raiseActionRisk(depth);
                    }
                }
                if (!abort) {
                    nextDepth(x - speed, y, depth + 1, speed);
                }
        }
    }
    private void recursiveRiskByDirectionWithJumping(int x, int y, int depth, int speed, Direction dir) {
        boolean abort = false;
            switch (dir) {
                case UP:
                    for (int j = 1; j < speed + 1; j++) {
                        if (y - j < 0 || y - j >= height) {
                            abort = true;
                            break;
                        } else if (cells[x][y - j].isDeadly()) {
                            abort = true;
                            break;
                        } else if (j == 1 || j == speed) {
                            evaluatedCells.add(cells[x][y - j]);
                            cells[x][y - j].raiseActionRisk(depth);
                        }
                    }
                    if (!abort) {
                        nextDepth(x, y - speed, depth + 1, speed);
                    }
                case DOWN:
                    for (int j = 1; j + 1 < speed + 1; j++) {
                        if (y + j < 0 || y + j >= height) {
                            abort = true;
                            break;
                        } else if (cells[x][y + j].isDeadly()) {
                            abort = true;
                            break;
                        } else if (j == 1 || j == speed) {
                            evaluatedCells.add(cells[x][y + j]);
                            cells[x][y + j].raiseActionRisk(depth);
                        }
                    }
                    if (!abort) {
                        nextDepth(x, y + speed, depth + 1, speed);
                    }
                case RIGHT:
                    for (int j = 1; j + 1 < speed + 1; j++) {
                        if (x + j < 0 || x + j >= width) {
                            abort = true;
                            break;
                        } else if (cells[x + j][y].isDeadly()) {
                            abort = true;
                            break;
                        } else if (j == 1 || j == speed) {
                            evaluatedCells.add(cells[x + j][y]);
                            cells[x + j][y].raiseActionRisk(depth);
                        }
                    }
                    if (!abort) {
                        nextDepth(x + speed, y, depth + 1, speed);
                    }
                case LEFT:
                    for (int j = 1; j < speed + 1; j++) {
                        if (x - j < 0 || x - j >= width) {
                            abort = true;
                            break;
                        } else if (cells[x - j][y].isDeadly()) {
                            abort = true;
                            break;
                        } else if (j == 1 || j == speed) {
                            evaluatedCells.add(cells[x - j][y]);
                            cells[x - j][y].raiseActionRisk(depth);
                        }
                    }
                    if (!abort) {
                        nextDepth(x - speed, y, depth + 1, speed);
                    }
            }
        }
}
