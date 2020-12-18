package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.CustomLog;

@CustomLog
public class OpponentMovesCalculation {

    private BoardAnalyzer boardAnalyzer;
    private int width;
    private int height;
    private Cell [][] cells;

    public OpponentMovesCalculation(BoardAnalyzer boardAnalyzer) {
        this.boardAnalyzer = boardAnalyzer;
    }

    /**
     * todo this.
     * @param cells   todo
     * @param players todo
     * @param us      todo
     */
    public void evaluate(Cell[][] cells, Player[] players, Player us) {
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
        SWITCH:
        switch (dir) {
            case UP:
                for (int j = 1; j < speed + 1; j++) {
                    if (y - j < 0 || y - j >= height || cells[x][y - j].isDeadly()) {
                        break SWITCH;
                    } else {
                        cells[x][y - j].raiseActionRisk(depth);
                    }
                }
                nextDepth(x, y - speed, depth + 1, speed);
                //TODO here was fallthrough
                break;
            case DOWN:
                for (int j = 1; j < speed + 1; j++) {
                    if (y + j < 0 || y + j >= height || cells[x][y + j].isDeadly()) {
                        break SWITCH;
                    } else {
                        cells[x][y + j].raiseActionRisk(depth);
                    }
                }
                nextDepth(x, y + speed, depth + 1, speed);
                //TODO here was fallthrough
                break;
            case RIGHT:
                for (int j = 1; j < speed + 1; j++) {
                    if (x + j < 0 || x + j >= width || cells[x + j][y].isDeadly()) {
                        break SWITCH;
                    } else {
                        cells[x + j][y].raiseActionRisk(depth);
                    }
                }
                nextDepth(x + speed, y, depth + 1, speed);
                //TODO here was fallthrough
                break;
            case LEFT:
                for (int j = 1; j  < speed + 1; j++) {
                    if (x - j < 0 || x - j >= width || cells[x - j][y].isDeadly()) {
                        break SWITCH;
                    } else {
                        cells[x - j][y].raiseActionRisk(depth);
                    }
                }
                nextDepth(x - speed, y, depth + 1, speed);
                //TODO here was fallthrough
                break;
            default:
                throw new IllegalStateException();
        }
    }
    private void recursiveRiskByDirectionWithJumping(int x, int y, int depth, int speed, Direction dir) {
        SWITCH:
        switch (dir) {
            case UP:
                for (int j = 1; j < speed + 1; j++) {
                    if (y - j < 0 || y - j >= height || cells[x][y - j].isDeadly()) {
                        break SWITCH;
                    } else if (j == 1 || j == speed) {
                        cells[x][y - j].raiseActionRisk(depth);
                    }
                }
                nextDepth(x, y - speed, depth + 1, speed);
                //TODO here was fallthrough
                break;
            case DOWN:
                for (int j = 1; j < speed + 1; j++) {
                    if (y + j < 0 || y + j >= height || cells[x][y + j].isDeadly()) {
                        break SWITCH;
                    } else if (j == 1 || j == speed) {
                        cells[x][y + j].raiseActionRisk(depth);
                    }
                }
                nextDepth(x, y + speed, depth + 1, speed);
                //TODO here was fallthrough
                break;
            case RIGHT:
                for (int j = 1; j < speed + 1; j++) {
                    if (x + j < 0 || x + j >= width || cells[x + j][y].isDeadly()) {
                        break SWITCH;
                    } else if (j == 1 || j == speed) {
                        cells[x + j][y].raiseActionRisk(depth);
                    }
                }
                nextDepth(x + speed, y, depth + 1, speed);
                //TODO here was fallthrough
                break;
            case LEFT:
                for (int j = 1; j < speed + 1; j++) {
                    if (x - j < 0 || x - j >= width || cells[x - j][y].isDeadly()) {
                        break SWITCH;
                    } else if (j == 1 || j == speed) {
                        cells[x - j][y].raiseActionRisk(depth);
                    }
                }
                nextDepth(x - speed, y, depth + 1, speed);
                //TODO here was fallthrough
                break;
            default:
                throw new IllegalStateException();
        }
    }
}
