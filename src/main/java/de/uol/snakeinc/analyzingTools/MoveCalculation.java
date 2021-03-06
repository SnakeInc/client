package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.Common;
import de.uol.snakeinc.Config;
import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.HashSet;

import static de.uol.snakeinc.Common.generateXY;
import static de.uol.snakeinc.Common.offBoardOrDeadly;

@Log4j2
public class MoveCalculation {

    private Cell[][] cells;
    private Player us;
    BoardAnalyzer boardAnalyzer;
    private final int searchingDepth = Config.SEARCHING_DEPTH;

    public MoveCalculation(Cell[][] cells, Player us, BoardAnalyzer boardAnalyzer) {
        this.cells = cells;
        this.us = us;
        this.boardAnalyzer = boardAnalyzer;
    }

    /**
     * Calculates the best Action. That is the least penalized Action by the factors on the Board.
     * @return the selected Action
     */
    public Action calculateBestAction() {
        log.info("calculating BestAction!");
        log.info("Speed = " + us.getSpeed());
        //log.info("jumping in " );
        double bestActionTmp = Config.BEST_ACTION_MULTIPLIER * deathValue(1);
        Action bestAction = Config.DEFAULT_BEST_ACTION;
        double tmp;
        Action[] actions = MoveOrder.weights(us.getSpeed(), us.getLeftRightBalance(),
                Config.IDEAL_MIN_SPEED, Config.IDEAL_MAX_SPEED, Config.IDEAL_SPIRAL_FORM);
        for (Action act : actions) {
            HashSet<Cell> pseudoEvaluatedCells = new HashSet<>();
            tmp = calculate(act, us.getDirection(), us.getX(), us.getY(), us.getSpeed(), 1, pseudoEvaluatedCells);
            for (Cell cell : pseudoEvaluatedCells) {
                cell.clearTmpMoveCalcValue();
            }
            log.debug("Action eval.: " + act + "with: " + tmp);
            if (tmp < bestActionTmp) {
                bestActionTmp = tmp;
                bestAction = act;
            }
        }
        if (bestAction == Action.TURN_LEFT || bestAction == Action.TURN_RIGHT) {
            us.updateLeftRightBalance(bestAction);
        }
        log.debug("BestAction: " + bestAction + "result: " + bestActionTmp);
        return bestAction;
    }

    private double calculateAction(Direction dir, int x, int y, int speed, int depth) {
        if (this.searchingDepth == depth) {
            return 1;
        }
        double bestAction = Config.BEST_ACTION_MULTIPLIER * deathValue(1);
        double tmp;
        for (Action act : Action.values()) {
            HashSet<Cell> pseudoEvaluatedCellsNextDepth = new HashSet<>();
            tmp = calculate(act, dir, x, y, speed, depth, pseudoEvaluatedCellsNextDepth);
            for (Cell cell : pseudoEvaluatedCellsNextDepth) {
                cell.clearTmpMoveCalcValue();
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

        if (speed < Config.SPEED_MIN || speed > Config.SPEED_MAX) {
            return deathValue(depth);
        } else {
            return calculateDirection(dir, x, y, speed, depth, pseudoEvaluatedCells,
                    boardAnalyzer.checkForJumping(depth), deathValue(depth));
        }
    }

    @AllArgsConstructor
    @Getter
    public static class DirSpeed {
        private final Direction direction;
        private final int speed;
    }

    /**
     * Calculates the Direction and Speed from the action.
     * @param act   the Action
     * @param dir   the current Direction
     * @param speed the current Speed
     * @return the new Direction and Speed
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

    private double calculateDirection(Direction dir, int x, int y, int speed, int depth,
                                      HashSet<Cell> pseudEvaluatedCells, boolean jumping, double deathValue) {

        double result = 1;

        if (jumping && speed >= Config.MINIMUM_JUMP_SPEED) {
            var xy = generateXY(dir, x, y, 1);
            int xval = xy.getX();
            int yval = xy.getY();

            if (offBoardOrDeadly(xval, yval, cells)) {
                return deathValue;
            }
            result = evaluateResult(pseudEvaluatedCells, result, cells[xval][yval]);

            xy = generateXY(dir, x, y, speed);
            xval = xy.getX();
            yval = xy.getY();

            if (offBoardOrDeadly(xval, yval, cells)) {
                return deathValue;
            }
            result = evaluateResult(pseudEvaluatedCells, result, cells[xval][yval]);

        } else { //Normal Cases
            for (var xy : Common.generateAllXYUpToFromOne(dir, x, y, speed + 1)) {
                int xval = xy.getX();
                int yval = xy.getY();

                if (offBoardOrDeadly(xval, yval, cells)) {
                    return deathValue;
                }
                result = evaluateResult(pseudEvaluatedCells, result, cells[xval][yval]);
            }
        }
        var xy = generateXY(dir, x, y, speed);
        return result * calculateAction(dir, xy.getX(), xy.getY(), speed, depth + 1);
    }

    private double deathValue(int depth) {
        return Config.DEATH_VALUE * Math.pow(Config.DEATH_VALUE_BASE,(this.searchingDepth - depth) + 1);
    }

    private double evaluateResult(HashSet<Cell> pseudEvaluatedCells, double result, Cell cell) {
        result = result * cell.getRisks();
        cell.setTmpMoveCalcValue();
        pseudEvaluatedCells.add(cell);
        return result;
    }
}
