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

    public OpponentMovesCalculation(Cell[][] cells, Player[] players, Player us, BoardAnalyzer boardAnalyzer) {
        this.cells = cells;
        this.width = cells.length;
        this.height = cells[1].length;
        this.players = players;
        this.us = us;
        this.boardAnalyzer = boardAnalyzer;
    }

    public Set<Tupel> evaluate() {
        int x;
        int y;
        int speed;
        Set<Tupel> evaluatedCells = new HashSet<>();

        for (int i = 0; i < players.length; i++) {
            if (BoardAnalyzer.inDistance(us, players[i]) || players[i].isActive()) {
                x = players[i].getX();
                y = players[i].getY();
                speed = players[i].getSpeed();
                nextDepth(x, y, evaluatedCells, 1, speed, new JumpCounter(boardAnalyzer.getJumpCounter(), players[i]));
            }
        }
        return evaluatedCells;
    }



    private Set<Tupel> nextDepth(int x, int y, Set<Tupel> evaluated, int depth, int speed, JumpCounter jumpCounter) {

        if(depth == 3) {
            return evaluated;
        }
        //Recursive call
        recursiveRiskByDirection(x, y, evaluated, depth, speed, Direction.UP, jumpCounter);
        recursiveRiskByDirection(x, y, evaluated, depth, speed, Direction.DOWN, jumpCounter);
        recursiveRiskByDirection(x, y, evaluated, depth, speed, Direction.LEFT, jumpCounter);
        recursiveRiskByDirection(x, y, evaluated, depth, speed, Direction.RIGHT, jumpCounter);
        return evaluated;
    }

    private Set<Tupel> nextDepthWithJumping(int x, int y, Set<Tupel> evaluated, int depth, int speed) {

        if(depth == 3) {
            return evaluated;
        }
        //Recursive call
        recursiveRiskByDirectionWithJumping(x, y, evaluated, depth, speed, Direction.UP);
        recursiveRiskByDirectionWithJumping(x, y, evaluated, depth, speed, Direction.DOWN);
        recursiveRiskByDirectionWithJumping(x, y, evaluated, depth, speed, Direction.LEFT);
        recursiveRiskByDirectionWithJumping(x, y, evaluated, depth, speed, Direction.RIGHT);
        return evaluated;
    }

    private void recursiveRiskByDirection(int x, int y, Set<Tupel> evaluated, int depth, int speed, Direction dir, JumpCounter jumpCounter) {
        boolean abort = false;
        boolean jumping = jumpCounter.check();
        if (jumping == true) {
            //In jumping-scenario an other jump is not common.
            recursiveRiskByDirectionWithJumping(x, y, evaluated, depth, speed, dir);
        } else {
        switch (dir) {
            case UP:
                for (int j = 1; j < speed + 1; j++) {
                    if (y - j < 0 || y-j >= height|| cells[x][y - j].isDeadly()) {
                        abort = true;
                        break;
                    } else {
                        evaluated.add(new Tupel(x, y - j));
                        cells[x][y - j].raiseActionRisk(depth);
                    }
                }
                if (!abort) {
                    nextDepth(x, y - speed, evaluated, depth + 1, speed, new JumpCounter(jumpCounter, jumpCounter.getPseudoPlayer()));
                }
            case DOWN:
                for (int j = 1; j + 1 < speed; j++) {
                    if (y + j < 0 || y + j >= height || cells[x][y + j].isDeadly()) {
                        abort = true;
                        break;
                    } else {
                        evaluated.add(new Tupel(x, y + j));
                        cells[x][y + j].raiseActionRisk(depth);
                    }
                }
                if (!abort) {
                    nextDepth(x, y + speed, evaluated, depth + 1, speed, new JumpCounter(jumpCounter, jumpCounter.getPseudoPlayer()));
                }
            case RIGHT:
                for (int j = 1; j + 1 < speed; j++) {
                    if (x + j < 0 || x+j >= width|| cells[x + j][y].isDeadly()) {
                        abort = true;
                        break;
                    } else {
                        evaluated.add(new Tupel(x + j, y));
                        cells[x + j][y].raiseActionRisk(depth);
                    }
                }
                if (!abort) {
                    nextDepth(x + speed, y, evaluated, depth + 1, speed, new JumpCounter(jumpCounter, jumpCounter.getPseudoPlayer()));
                }
            case LEFT:
                for (int j = 1; j < speed + 1; j++) {
                    if (x - j < 0 || x - j >= width || cells[x - j][y].isDeadly()) {
                        abort = true;
                        break;
                    } else {
                        evaluated.add(new Tupel(x - j, y));
                        cells[x - j][y].raiseActionRisk(depth);
                    }
                }
                if (!abort) {
                    nextDepth(x - speed, y, evaluated, depth + 1, speed, new JumpCounter(jumpCounter, jumpCounter.getPseudoPlayer()));
                }
        }
        }
    }
    private void recursiveRiskByDirectionWithJumping(int x, int y, Set<Tupel> evaluated, int depth, int speed, Direction dir) {
        //TODO: Implement Jumping Logic
        boolean abort = false;
            switch (dir) {
                case UP:
                    for (int j = 1; j < speed + 1; j++) {
                        if (y - j < 0 || y - j >= height || cells[x][y - j].isDeadly()) {
                            abort = true;
                            break;
                        } else {
                            evaluated.add(new Tupel(x, y - j));
                            cells[x][y + j].raiseActionRisk(depth);
                        }
                    }
                    if (!abort) {
                        nextDepthWithJumping(x, y - speed, evaluated, depth + 1, speed);
                    }
                case DOWN:
                    for (int j = 1; j + 1 < speed; j++) {
                        if (y + j < 0 || y + j >= height|| cells[x][y + j].isDeadly()) {
                            abort = true;
                            break;
                        } else {
                            evaluated.add(new Tupel(x, y - j));
                            cells[x][y + j].raiseActionRisk(depth);
                        }
                    }
                    if (!abort) {
                        nextDepthWithJumping(x, y + speed, evaluated, depth + 1, speed);
                    }
                case RIGHT:
                    for (int j = 1; j + 1 < speed; j++) {
                        if (x + j < 0 || x + j >= width|| cells[x + j][y].isDeadly()) {
                            abort = true;
                            break;
                        } else {
                            evaluated.add(new Tupel(x + j, y));
                            cells[x + j][y].raiseActionRisk(depth);
                        }
                    }
                    if (!abort) {
                        nextDepthWithJumping(x + speed, y, evaluated, depth + 1, speed);
                    }
                case LEFT:
                    for (int j = 1; j < speed + 1; j++) {
                        if (x - j < 0 || x - j >= width || cells[x - j][y].isDeadly()) {
                            abort = true;
                            break;
                        } else {
                            evaluated.add(new Tupel(x - j, y));
                            cells[x - j][y].raiseActionRisk(depth);
                        }
                    }
                    if (!abort) {
                        nextDepthWithJumping(x - speed, y, evaluated, depth + 1, speed);
                    }
            }
        }
}
