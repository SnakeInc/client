package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.AllArgsConstructor;
import lombok.CustomLog;
import lombok.Getter;

@CustomLog
public class MoveCalculation {

    private Cell[][] cells;
    private Player us;
    int width;
    int height;
    BoardAnalyzer boardAnalyzer;
    private int searchingDepth = 12;

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
            tmp = calculate(act, us.getDirection(), us.getX(), us.getY(), us.getSpeed(), 1);
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
        if (this.searchingDepth == depth) {
            return 1;
        }
        double bestAction = 100;
        double tmp;
        for (Action act : Action.values()) {
            tmp = calculate(act, dir, x, y, speed, depth);
            if (tmp < bestAction) {
                bestAction = tmp;
            }
        }

        return bestAction;
    }

    private double calculate(Action act, Direction dir, int x, int y, int speed, int depth) {
        var dirSpeedDepth = preCalculate(act, dir, speed);
        dir = dirSpeedDepth.direction;
        speed = dirSpeedDepth.speed;

        if (speed < 1 || speed > 10) {
            return deathValue(depth);
        } else {
            return calculateDirection(dir, x, y, speed, depth);
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
    public static DirSpeed preCalculate(Action act, Direction dir, int speed) {
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

    private double calculateDirection(Direction dir, int x, int y, int speed, int depth) {
        Cell[] pseudoEvaluatedCells = new Cell[11];
        double res;
        double result = 1;
        switch (dir) {
            case LEFT:
                //Jumping-Cases
                if (boardAnalyzer.checkForJumping(depth) && speed >= 3) {
                    if (offBoardOrDeadly(x - 1, y)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudoEvaluatedCells, result, x - 1, y, 0);

                    if (offBoardOrDeadly(x - speed, y)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudoEvaluatedCells, result, x - speed, y, 1);

                } else { //Normal Cases
                    for (int i = 1; i < speed + 1; i++) {
                        if (offBoardOrDeadly(x - i, y)) {
                            return deathValue(depth);
                        }
                        result = evaluateResult(pseudoEvaluatedCells, result, x - i, y, i);
                    }
                }
                res = result * calculateAction(Direction.LEFT, x - speed, y, speed, depth + 1);
                break;
            case RIGHT:
                //Jumping-Cases
                if (boardAnalyzer.checkForJumping(depth) && speed >= 3) {
                    if (offBoardOrDeadly(x + 1, y)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudoEvaluatedCells, result, x + 1, y, 0);

                    if (offBoardOrDeadly(x + speed, y)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudoEvaluatedCells, result, x + speed, y, 1);

                } else { //Normal Cases
                    for (int i = 1; i < speed + 1; i++) {
                        if (offBoardOrDeadly(x + i, y)) {
                            return deathValue(depth);
                        }
                        result = evaluateResult(pseudoEvaluatedCells, result, x + i, y, i);
                    }
                }
                res = result * calculateAction(Direction.RIGHT, x + speed, y, speed, depth + 1);
                break;
            case DOWN:
                //Jumping-Cases
                if (boardAnalyzer.checkForJumping(depth) && speed >= 3) {
                    if (offBoardOrDeadly(x, y + 1)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudoEvaluatedCells, result, x, y + 1, 0);

                    if (offBoardOrDeadly(x, y + speed)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudoEvaluatedCells, result, x, y + speed, 1);

                } else { //Normal Cases
                    for (int i = 1; i < speed + 1; i++) {
                        if (offBoardOrDeadly(x, y + i)) {
                            return deathValue(depth);
                        }
                        result = evaluateResult(pseudoEvaluatedCells, result, x, y + i, i);
                    }
                }
                res = result * calculateAction(Direction.DOWN, x, y + speed, speed, depth + 1);
                break;
            case UP:
                //Jumping-Cases
                if (boardAnalyzer.checkForJumping(depth) && speed >= 3) {
                    if (offBoardOrDeadly(x, y - 1)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudoEvaluatedCells, result, x, y - 1, 0);

                    if (offBoardOrDeadly(x, y - speed)) {
                        return deathValue(depth);
                    }
                    result = evaluateResult(pseudoEvaluatedCells, result, x, y - speed, 1);

                } else { //Normal Cases
                    for (int i = 1; i < speed + 1; i++) {
                        if (offBoardOrDeadly(x, y - i)) {
                            return deathValue(depth);
                        }
                        result = evaluateResult(pseudoEvaluatedCells, result, x, y - i, i);
                    }
                }
                res = result * calculateAction(Direction.UP, x, y - speed, speed, depth + 1);
                break;
            default:
                throw new IllegalStateException();
        }
        for (var cell : pseudoEvaluatedCells) {
            if (cell == null) {
                continue;
            }
            cell.clearPseudoValue();
        }
        return res;
    }

    private double deathValue(int depth) {
        return 10 * (this.searchingDepth - depth);
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

    private double evaluateResult(Cell[] pseudoEvaluatedCells, double result, int x, int y, int j) {
        result = result * cells[x][y].getRisks();
        cells[x][y].setTmpMoveCalcValue();
        pseudoEvaluatedCells[j] = cells[x][y];
        return result;
    }
}
