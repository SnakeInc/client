package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.Common;
import de.uol.snakeinc.Config;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.extern.log4j.Log4j2;

import java.util.HashSet;
import java.util.Set;

@Log4j2
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
            if (BoardAnalyzer.inDistance(us, players[i], Config.OPPONENT_MOVES_DEPTH + 1)) {
                log.info("Computing Opponent Moves.");
                x = players[i].getX();
                y = players[i].getY();
                speed = players[i].getSpeed();
                calculateRisk(x, y, 1, speed);
            }
        }
        return evaluatedCells;
    }

    private void calculateRisk(int x, int y, int depth, int speed) {
        if (boardAnalyzer.checkForJumping(depth)) {
            calculateRiskWithJumping(x, y, depth, speed);
        } else {
            if (depth <= Config.OPPONENT_MOVES_DEPTH) {
                //Recursive call
                recursiveRiskByDirection(x, y, depth, speed, Direction.UP);
                recursiveRiskByDirection(x, y, depth, speed, Direction.DOWN);
                recursiveRiskByDirection(x, y, depth, speed, Direction.LEFT);
                recursiveRiskByDirection(x, y, depth, speed, Direction.RIGHT);
            }
        }
    }

    private void calculateRiskWithJumping(int x, int y, int depth, int speed) {
        if (depth <= Config.OPPONENT_MOVES_DEPTH) {
            //Recursive call
            recursiveRiskByDirectionWithJumping(x, y, depth, speed, Direction.UP);
            recursiveRiskByDirectionWithJumping(x, y, depth, speed, Direction.DOWN);
            recursiveRiskByDirectionWithJumping(x, y, depth, speed, Direction.LEFT);
            recursiveRiskByDirectionWithJumping(x, y, depth, speed, Direction.RIGHT);
        }
    }

    private void recursiveRiskByDirection(int x, int y, int depth, int speed, Direction dir) {
        boolean abort = false;

        for (var xy : Common.generateAllXYUpToFromOne(dir, x, y, speed + 1)) {
            if (Common.offBoardOrDeadly(xy.getX(), xy.getY(), cells)) {
                abort = true;
                break;
            } else {
                evaluateCells(xy.getX(), xy.getY(), depth);
            }
        }
        if (!abort) {
            var xy = Common.generateXY(dir, x, y, speed);
            calculateRisk(xy.getX(), xy.getY(), depth + 1, speed);
        }
    }

    private void recursiveRiskByDirectionWithJumping(int x, int y, int depth, int speed, Direction dir) {
        switch (dir) {
            case UP:
                if (!Common.offBoardOrDeadly(x, y - 1, cells) && !Common.offBoardOrDeadly(x, y - speed, cells)) {
                    evaluateCells(x, y - 1, depth);
                    evaluateCells(x, y - speed, depth);
                    calculateRisk(x, y - speed, depth + 1, speed);
                }
                break;
            case DOWN:
                if (!Common.offBoardOrDeadly(x, y + 1, cells) && !Common.offBoardOrDeadly(x, y + speed, cells)) {
                    evaluateCells(x, y + 1, depth);
                    evaluateCells(x, y + speed, depth);
                    calculateRisk(x, y + speed, depth + 1, speed);
                }
                break;
            case RIGHT:
                if (!Common.offBoardOrDeadly(x + 1, y, cells) && !Common.offBoardOrDeadly(x + speed, y, cells)) {
                    evaluateCells(x + 1, y, depth);
                    evaluateCells(x + speed, y, depth);
                    calculateRisk(x + speed, y, depth + 1, speed);
                }
                break;
            case LEFT:
                if (!Common.offBoardOrDeadly(x - 1, y, cells) && !Common.offBoardOrDeadly(x - speed, y, cells)) {
                    evaluateCells(x - 1, y, depth);
                    evaluateCells(x - speed, y, depth);
                    calculateRisk(x - speed, y, depth + 1, speed);
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
}
