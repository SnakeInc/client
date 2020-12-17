package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;

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
        this.height = cells[0].length;
        this.width = cells.length;
        this.boardAnalyzer = boardAnalyzer;
    }

    /**
     * todo this.
     * @return todo
     */
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
            log.debug("Action eval.: " + act + "with: " + tmp);
            if (tmp < bestActionTmp) {
                bestActionTmp = tmp;
                bestAction = act;
            }
        }
        log.debug("BestAction: " + bestAction + "result: " + bestActionTmp);
        return bestAction;
    }

    private double calculateAction(Direction dir, int x, int y, int speed, int depth) {
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

    private double calculate(Action act, Direction dir, int x, int y, int speed,
                             int depth, HashSet<Cell> pseudoEvaluatedCells) {
        var dirSpeedDepth = preCalculate(act, dir, speed);
        dir = dirSpeedDepth.direction;
        speed = dirSpeedDepth.speed;

        if (speed < 1 || speed > 10) {
            return deathValue(depth);
        } else {
            return calculateDirection(dir, x, y, speed, depth, pseudoEvaluatedCells);
        }
    }

    @AllArgsConstructor
    @Getter
    public static class DirSpeed {

        private final Direction direction;
        private final int speed;
    }

    /**
     * todo this.
     * @param act   todo
     * @param dir   todo
     * @param speed todo
     * @return todo
     */
    public DirSpeed preCalculate(Action act, Direction dir, int speed) {
        switch (act) {
            case SPEED_UP:
                return new DirSpeed(dir, speed + 1);
            case CHANGE_NOTHING:
                return new DirSpeed(dir, speed);
            case SLOW_DOWN:
                return new DirSpeed(dir, speed - 1);
            case TURN_LEFT:
                switch (dir) {
                    case UP:
                        dir = Direction.LEFT;
                        break;
                    case DOWN:
                        dir = Direction.RIGHT;
                        break;
                    case RIGHT:
                        dir = Direction.UP;
                        break;
                    case LEFT:
                        dir = Direction.DOWN;
                        break;
                    default:
                        throw new IllegalStateException();
                }
                return new DirSpeed(dir, speed);
            case TURN_RIGHT:
                switch (dir) {
                    case UP:
                        dir = Direction.RIGHT;
                        break;
                    case DOWN:
                        dir = Direction.LEFT;
                        break;
                    case RIGHT:
                        dir = Direction.DOWN;
                        break;
                    case LEFT:
                        dir = Direction.UP;
                        break;
                    default:
                        throw new IllegalStateException();
                }
                return new DirSpeed(dir, speed);
            default:
                throw new IllegalStateException();
        }
    }

    private double calculateDirection(Direction dir, int x, int y, int speed,
                                      int depth, HashSet<Cell> pseudEvaluatedCells) {
        double result = 1;
        switch (dir) {
            case LEFT:
                //Jumping-Cases
                if (boardAnalyzer.checkForJumping(depth)) {
                    if (offBoardOrDeadly(x - 1, y)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudEvaluatedCells, result, x - 1, y);

                    if (offBoardOrDeadly(x - speed, y)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudEvaluatedCells, result, x - speed, y);

                } else { //Normal Cases
                    for (int i = 1; i < speed + 1; i++) {
                        if (offBoardOrDeadly(x - i, y)) {
                            return deathValue(depth);
                        }
                        result = evaluateResult(pseudEvaluatedCells, result, x - i, y);
                    }
                }
                return result * calculateAction(Direction.LEFT, x - speed, y, speed, depth + 1);

            case RIGHT:
                //Jumping-Cases
                if (boardAnalyzer.checkForJumping(depth)) {
                    if (offBoardOrDeadly(x + 1, y)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudEvaluatedCells, result, x + 1, y);

                    if (offBoardOrDeadly(x + speed, y)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudEvaluatedCells, result, x + speed, y);

                } else { //Normal Cases
                    for (int i = 1; i < speed + 1; i++) {
                        if (offBoardOrDeadly(x + i, y)) {
                            return deathValue(depth);
                        }
                        result = evaluateResult(pseudEvaluatedCells, result, x + i, y);
                    }
                }
                return result * calculateAction(Direction.RIGHT, x + speed, y, speed, depth + 1);

            case DOWN:
                //Jumping-Cases
                if (boardAnalyzer.checkForJumping(depth)) {
                    if (offBoardOrDeadly(x, y + 1)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudEvaluatedCells, result, x, y + 1);

                    if (offBoardOrDeadly(x, y + speed)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudEvaluatedCells, result, x, y + speed);

                } else { //Normal Cases
                    for (int i = 1; i < speed + 1; i++) {
                        if (offBoardOrDeadly(x, y + i)) {
                            return deathValue(depth);
                        }
                        result = evaluateResult(pseudEvaluatedCells, result, x, y + i);
                    }
                }
                return result * calculateAction(Direction.DOWN, x, y + speed, speed, depth + 1);

            case UP:
                //Jumping-Cases
                if (boardAnalyzer.checkForJumping(depth)) {
                    if (offBoardOrDeadly(x, y - 1)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudEvaluatedCells, result, x, y - 1);

                    if (offBoardOrDeadly(x, y - speed)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudEvaluatedCells, result, x, y - speed);

                } else { //Normal Cases
                    for (int i = 1; i < speed + 1; i++) {
                        if (offBoardOrDeadly(x, y - i)) {
                            return deathValue(depth);
                        }
                        result = evaluateResult(pseudEvaluatedCells, result, x, y - i);
                    }
                }
                return result * calculateAction(Direction.UP, x, y - speed, speed, depth + 1);
            default:
                throw new IllegalStateException();
        }
        //return result; todo this or IllegalState ?
    }

    private double deathValue(int depth) {
        return 10 * (6 - depth);
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

    private double evaluateResult(HashSet<Cell> pseudEvaluatedCells, double result, int x, int y) {
        result = result * cells[x][y].getRisks();
        cells[x][y].setPseudoValue();
        pseudEvaluatedCells.add(cells[x][y]);
        return result;
    }
}
