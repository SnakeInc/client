package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.CustomLog;

import java.util.HashSet;
import java.util.Set;

@CustomLog
public class OpponentMovesCalculation {

    private BoardAnalyzer boardAnalyzer;
    private int width;
    private int height;
    private Cell [][] cells;
    Set<Cell> evaluatedCells = new HashSet<>();

    public OpponentMovesCalculation(BoardAnalyzer boardAnalyzer) {
        this.boardAnalyzer = boardAnalyzer;
    }

    /**
     * evaluates the risk of a cell if an other player could reach it.
     * @return evaluated Cells
     * @param cells cells
     * @param players players
     * @param us us
     */
    public Set<Cell> evaluate(Cell[][] cells, Player[] players, Player us) {
        this.width = cells.length;
        this.height = cells[1].length;
        this.cells = cells;
        int x;
        int y;
        int speed;
        for (int i = 0; i < players.length; i++) {
            if (BoardAnalyzer.inDistance(us, players[i])) {
                log.info("Computing Opponent Moves.");
                x = players[i].getX();
                y = players[i].getY();
                speed = players[i].getSpeed();
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
        if (depth <= 3) {
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
                    if (offBoardOrDeadly(x, y - j)) {
                        abort = true;
                        break;
                    } else {
                        evaluateCells(x, y - j, depth);
                    }
                }
                if (!abort) {
                    nextDepth(x, y - speed, depth + 1, speed);
                }
                break;
            case DOWN:
                for (int j = 1; j < speed + 1; j++) {
                    if(offBoardOrDeadly(x, y + j)) {
                        abort = true;
                        break;
                    } else {
                        evaluateCells(x, y + j, depth);
                    }
                }
                if (!abort) {
                    nextDepth(x, y + speed, depth + 1, speed);
                }
                break;
            case RIGHT:
                for (int j = 1; j < speed + 1; j++) {
                    if (offBoardOrDeadly(x + j, y)) {
                        abort = true;
                        break;
                    } else {
                        evaluateCells(x + j, y, depth);
                    }
                }
                if (!abort) {
                    nextDepth(x + speed, y, depth + 1, speed);
                }
                break;
            case LEFT:
                for (int j = 1; j  < speed + 1; j++) {
                    if (offBoardOrDeadly(x - j, y)) {
                        abort = true;
                        break;
                    } else {
                        evaluateCells(x - j, y, depth);
                    }
                }
                if (!abort) {
                    nextDepth(x - speed, y, depth + 1, speed);
                }
                break;
            default:
                throw new IllegalStateException();
        }
    }
    private void recursiveRiskByDirectionWithJumping(int x, int y, int depth, int speed, Direction dir) {
        boolean abort = false;
        switch (dir) {
            case UP:
                for (int j = 1; j < speed + 1; j++) {
                    if (offBoardOrDeadly(x, y - j)) {
                        abort = true;
                        break;
                    } else if (j == 1 || j == speed) {
                        evaluateCells(x, y - j, depth);
                    }
                }
                if (!abort) {
                    nextDepth(x, y - speed, depth + 1, speed);
                }
                break;
            case DOWN:
                for (int j = 1; j < speed + 1; j++) {
                    if (offBoardOrDeadly(x, y + j)) {
                        abort = true;
                        break;
                    } else if (j == 1 || j == speed) {
                        evaluateCells(x, y + j, depth);

                    }
                }
                if (!abort) {
                    nextDepth(x, y + speed, depth + 1, speed);
                }
                break;
            case RIGHT:
                for (int j = 1; j < speed + 1; j++) {
                    if (offBoardOrDeadly(x + j, y)) {
                        abort = true;
                        break;
                    } else if (j == 1 || j == speed) {
                        evaluateCells(x + j, y, depth);

                    }
                }
                if (!abort) {
                    nextDepth(x + speed, y, depth + 1, speed);
                }
                break;
            case LEFT:
                for (int j = 1; j < speed + 1; j++) {
                    if (offBoardOrDeadly(x - j, y)) {
                        abort = true;
                        break;
                    } else if (j == 1 || j == speed) {
                        evaluateCells(x - j, y, depth);
                    }
                }
                if (!abort) {
                    nextDepth(x - speed, y, depth + 1, speed);
                }
                break;
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * raises actionRisk of cells.
     * @param x coordinate
     * @param y coordinate
     * @param depth depth
     */
    public void evaluateCells (int x, int y, int depth) {
        evaluatedCells.add(cells[x][y]);
        cells[x][y].raiseActionRisk(depth);
    }

    /**
     * tests if coordinates are on the board or the cell is deadly.
     * @param x x coordinate
     * @param y y coordinate
     * @return returns the test value
     */
    public boolean offBoardOrDeadly(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return true;
        } else {
            return cells[x][y].isDeadly();
        }
    }
}
