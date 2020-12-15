package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.CustomLog;

import java.util.HashSet;

@CustomLog
public class MoveCalculation {

    private Cell[][] cells;
    private Player us;
    int width;
    int height;
    BoardAnalyzer boardAnalyzer;

    public MoveCalculation(Cell[][] cells, Player us, BoardAnalyzer boardAnalyzer) {
        this.cells = cells;
        this.us = us;
        this.height= cells[1].length;
        this.width = cells.length;
        this.boardAnalyzer = boardAnalyzer;
    }

    public Action calculateBestAction() {
        log.info("calculating BestAction!");
        double bestActionTmp = 100;
        Action bestAction = Action.CHANGE_NOTHING;
        double tmp;
        for (Action act : Action.values()) {
            HashSet<Cell> pseudoEvaluatedCells = new HashSet<>();
            tmp = calculate(act, us.getDirection(), us.getX(), us.getY(), us.getSpeed(), 1, pseudoEvaluatedCells);
            for (Cell cell : pseudoEvaluatedCells) {
                cell.clearPseudoValue();
            }
            System.out.println("Action eval.: " + act + "with: " + tmp);
            if (tmp < bestActionTmp) {
                bestActionTmp = tmp;
                bestAction = act;
            }
        }
        System.out.println("BestAction: " + bestAction + "result: " + bestActionTmp);
        return bestAction;
    }

    private double calculateAction(Direction dir, int x, int y, int speed, int depth, HashSet<Cell> pseudoEvaluatedCells) {
        if (depth == 6) {
            return 1;
        }
        double bestAction = 100;
        double tmp;
        for (Action act : Action.values()) {
            HashSet<Cell> pseudoEvaluatedCellsNextDepth = new HashSet<>();
            tmp = calculate(act, dir, x, y, speed, depth, pseudoEvaluatedCellsNextDepth);
            for (Cell cell : pseudoEvaluatedCellsNextDepth) {
                cell.clearPseudoValue();
            }
            if (tmp < bestAction) {
                bestAction = tmp;
            }
        }

        return bestAction;
    }

    private double calculate(Action act, Direction dir, int x, int y, int speed, int depth, HashSet<Cell> pseudoEvaluatedCells) {
        switch (act) {
            case SPEED_UP:
                if (speed + 1 > 10) {
                    return 10 * (6 - depth);
                }
                return calculateDirection(dir, x, y, speed + 1, depth, pseudoEvaluatedCells);
            case CHANGE_NOTHING:
                return calculateDirection(dir, x, y, speed , depth, pseudoEvaluatedCells);
            case SLOW_DOWN:
                if (speed - 1 == 0) {
                    return 10 * (6-depth);
                }
                return calculateDirection(dir, x, y, speed - 1, depth, pseudoEvaluatedCells);
            case TURN_LEFT:
                switch (dir) {
                    case UP:
                        return calculateDirection(Direction.LEFT, x, y, speed, depth, pseudoEvaluatedCells);
                    case DOWN:
                        return calculateDirection(Direction.RIGHT, x, y, speed, depth, pseudoEvaluatedCells);
                    case RIGHT:
                        return calculateDirection(Direction.UP, x, y, speed, depth, pseudoEvaluatedCells);
                    case LEFT:
                        return calculateDirection(Direction.DOWN, x, y, speed, depth, pseudoEvaluatedCells);
                }
            case TURN_RIGHT:
                switch (dir) {
                    case UP:
                        return calculateDirection(Direction.RIGHT, x, y, speed, depth, pseudoEvaluatedCells);
                    case DOWN:
                        return calculateDirection(Direction.LEFT, x, y, speed, depth, pseudoEvaluatedCells);
                    case RIGHT:
                        return calculateDirection(Direction.DOWN, x, y, speed, depth, pseudoEvaluatedCells);
                    case LEFT:
                        return calculateDirection(Direction.UP, x, y, speed, depth, pseudoEvaluatedCells);
                }        }
        return 1;
    }

    private double calculateDirection(Direction dir, int x, int y, int speed, int depth, HashSet<Cell> pseudEvaluatedCells) {
            double result = 1;
            switch (dir) {
                case LEFT:
                    //Jumping-Cases
                    if (boardAnalyzer.checkForJumping(depth)) {
                        if (x - 1 < 0 || x - 1 >= width || y < 0 || y >= height) {
                            return 10 * (6 - depth);
                        } else if (cells[x - 1][y].isDeadly()) {
                            return 10 * (6 - depth);
                        }
                        result = result * cells[x - 1][y].getRisks();
                        cells[x - 1][y].setPseudoValue();
                        pseudEvaluatedCells.add(cells[x - 1][y]);

                        if (x - speed < 0 || x - speed >= width || y < 0 /* todo: is always false remove?*/ || y >= height) {
                            return 10 * (6 - depth);
                        } else if (cells[x - speed][y].isDeadly()) {
                            return 10 * (6 - depth);
                        }
                        result = result * cells[x - speed][y].getRisks();
                        cells[x - speed][y].setPseudoValue();
                        pseudEvaluatedCells.add(cells[x - speed][y]);
                    } else { //Normal Cases
                        for (int i = 1; i < speed + 1; i++) {
                            if (x - i < 0 || x - i >= width || y < 0 || y >= height) {
                                return 10 * (6 - depth);
                            } else if (cells[x - i][y].isDeadly()) {
                                return 10 * (6 - depth);
                            }
                            result = result * cells[x - i][y].getRisks();
                            cells[x - i][y].setPseudoValue();
                            pseudEvaluatedCells.add(cells[x - i][y]);
                        }
                    }
                    return result * calculateAction(Direction.LEFT, x - speed, y, speed, depth + 1, pseudEvaluatedCells);

                case RIGHT:
                    //Jumping-Cases
                    if (boardAnalyzer.checkForJumping(depth)) {
                        if (x + 1 < 0 || x + 1 >= width || y < 0 || y >= height) {
                            return 10 * (6 - depth);
                        } else if (cells[x + 1][y].isDeadly()) {
                            return 10 * (6 - depth);
                        }
                        result = result * cells[x + 1][y].getRisks();
                        cells[x + 1][y].setPseudoValue();
                        pseudEvaluatedCells.add(cells[x + 1][y]);

                        if (x + speed < 0 || x + speed >= width || y < 0 || y >= height) {
                            return 10 * (6 - depth);
                        } else if (cells[x + speed][y].isDeadly()) {
                            return 10 * (6 - depth);
                        }
                        result = result * cells[x + speed][y].getRisks();
                        cells[x + speed][y].setPseudoValue();
                        pseudEvaluatedCells.add(cells[x + speed][y]);
                    } else { //Normal Cases
                        for (int i = 1; i < speed + 1; i++) {
                            if (x + i < 0 || x + i >= width || y < 0 || y >= height) {
                                return 10 * (6 - depth);
                            } else if (cells[x + i][y].isDeadly()) {
                                return 10 * (6 - depth);
                            }
                            result = result * cells[x + i][y].getRisks();
                            cells[x + i][y].setPseudoValue();
                            pseudEvaluatedCells.add(cells[x + i][y]);
                        }
                    }
                    return result * calculateAction(Direction.RIGHT, x + speed, y, speed, depth + 1, pseudEvaluatedCells);

                case DOWN:
                    //Jumping-Cases
                    if (boardAnalyzer.checkForJumping(depth)) {
                        if (x < 0 || x >= width || y + 1 < 0 || y + 1 >= height) {
                            return 10 * (6 - depth);
                        } else if (cells[x][y + 1].isDeadly()) {
                            return 10 * (6 - depth);
                        }
                        result = result * cells[x][y + 1].getRisks();
                        cells[x][y + 1].setPseudoValue();
                        pseudEvaluatedCells.add(cells[x][y + 1]);

                        if (x  < 0 || x >= width || y + speed < 0 || y + speed >= height) {
                            return 10 * (6 - depth);
                        } else if (cells[x][y + speed].isDeadly()) {
                            return 10 * (6 - depth);
                        }
                        result = result * cells[x][y + speed].getRisks();
                        cells[x][y + speed].setPseudoValue();
                        pseudEvaluatedCells.add(cells[x][y + speed]);
                    } else { //Normal Cases
                        for (int i = 1; i < speed + 1; i++) {
                            if (x < 0 || x >= width || y + i < 0 || y + i >= height) {
                                return 10 * (6 - depth);
                            } else if (cells[x][y + 1].isDeadly()) {
                                return 10 * (6 - depth);
                            }
                            result = result * cells[x][y + i].getRisks();
                            cells[x][y + i].setPseudoValue();
                            pseudEvaluatedCells.add(cells[x][y + i]);
                        }
                    }
                    return result * calculateAction(Direction.DOWN, x, y + speed, speed, depth + 1, pseudEvaluatedCells);

                case UP:
                    //Jumping-Cases
                    if (boardAnalyzer.checkForJumping(depth)) {
                        if (x < 0 || x >= width || y < - 1 || y - 1 >= height) {
                            return 10 * (6 - depth);
                        } else if (cells[x][y - 1].isDeadly()) {
                            return 10 * (6 - depth);
                        }
                        result = result * cells[x][y - 1].getRisks();
                        cells[x][y - 1].setPseudoValue();
                        pseudEvaluatedCells.add(cells[x][y - 1]);

                        if (x  < 0 || x >= width || y - speed < 0 || y - speed >= height) {
                            return 10 * (6 - depth);
                        } else if (cells[x][y - speed].isDeadly()) {
                            return 10 * (6 - depth);
                        }
                        result = result * cells[x][y - speed].getRisks();
                        cells[x][y - speed].setPseudoValue();
                        pseudEvaluatedCells.add(cells[x][y - speed]);
                    } else { //Normal Cases
                        for (int i = 1; i < speed + 1; i++) {
                            if (x < 0 || x >= width || y - i < 0 || y - i >= height) {
                                return 10 * (6 - depth);
                            } else if (cells[x][y - i].isDeadly()) {
                                return 10 * (6 - depth);
                            }
                            result = result * cells[x][y - i].getRisks();
                            cells[x][y - i].setPseudoValue();
                            pseudEvaluatedCells.add(cells[x][y - i]);
                        }
                    }
                    return result * calculateAction(Direction.UP, x, y - speed, speed, depth + 1, pseudEvaluatedCells);
            }
            return result;
        }
    }
