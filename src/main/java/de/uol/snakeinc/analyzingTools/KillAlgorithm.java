package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.Common;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.pathfinding.PathCell;
import de.uol.snakeinc.pathfinding.Pathfinder;
import de.uol.snakeinc.pathfinding.astar.AStarSearch;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.sf.saxon.expr.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class KillAlgorithm {


    public static final int initialAttackDistance = 2;
    public static final int initialFloodTerminationCount = 100;
    /**
     * Sets incentives if another Player can be attacked.
     * @param cells     cells
     * @param players   players
     * @param us        us
     * @return          Set of cells
     */
    public static Set<Cell> killAlgorithm(Cell[][] cells, Player[] players, Player us) {
        Set<Cell> evaluatedCells = new HashSet<>();
        int width = cells.length;
        int height = cells[1].length;
        Direction usDirection = us.getDirection();
        for (int i = 0; i < players.length; i++) {
            if (BoardAnalyzer.inDistance(us, players[i], 4)) {
                int[][] floodCache = new int [width][height];
                floodCache = closeCircle(cells, floodCache, players[i], us);
                FloodVar floodVarIf = new FloodVar(initialFloodTerminationCount, floodCache);
                FloodVar floodVarElse = new FloodVar(initialFloodTerminationCount, floodCache);
                Player op = players[i];
                int x = players[i].getX();
                int y = players[i].getY();
                Direction dir = players[i].getDirection();
                Boolean boolIf = false;
                Boolean boolElse = false;
                switch (usDirection) {
                    case DOWN:
                    case UP:
                        boolIf = checkForDeadEnd(x + 1, y, cells, floodVarIf);
                        boolElse = checkForDeadEnd(x - 1, y, cells, floodVarElse);
                        break;
                    case RIGHT:
                    case LEFT:
                        boolIf = checkForDeadEnd(x, y + 1, cells, floodVarIf);
                        boolElse = checkForDeadEnd(x, y - 1, cells, floodVarElse);
                        break;
                }
                switch (dir) {
                    case DOWN:
                    case UP:
                        boolIf = checkForDeadEnd(x - 1, y, cells, floodVarIf) || boolIf;
                        boolElse = checkForDeadEnd(x + 1, y, cells, floodVarElse) || boolElse;
                        if (boolIf) {
                            if (boolElse) {
                                decideAttackDirection(op, cells, evaluatedCells, dir,
                                    width, height, floodVarIf, floodVarElse);
                                break;
                            }
                            raiseKillIncentive(op, cells, evaluatedCells, dir, Direction.LEFT, width, height);
                            break;
                        }
                        if (boolElse) {
                            raiseKillIncentive(op, cells, evaluatedCells, dir, Direction.RIGHT, width, height);
                        }
                        break;
                    case RIGHT:
                    case LEFT:
                        boolIf = checkForDeadEnd(x, y - 1, cells, floodVarIf) || boolIf;
                        boolElse = checkForDeadEnd(x, y + 1, cells, floodVarElse) || boolElse;
                        if (boolIf) {
                            if (boolElse) {
                                decideAttackDirection(op, cells, evaluatedCells, dir,
                                    width, height, floodVarIf, floodVarElse);
                                break;
                            }
                            raiseKillIncentive(op, cells, evaluatedCells, dir, Direction.UP, width, height);
                            break;
                        }
                        if (boolElse) {
                            raiseKillIncentive(op, cells, evaluatedCells, dir, Direction.DOWN, width, height);
                        }
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }
        }
        return evaluatedCells;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class FloodVar {

        int floodTerminationCount;
        int[][] floodCache;
    }

    /**
     * checks for dead end.
     * @param floodVar  floodVariables
     * @param x         x coordinate
     * @param y         y coordinate
     * @param cells     cells
     * @return          true if theres a dead end
     */
    private static boolean checkForDeadEnd(int x, int y, Cell[][] cells, FloodVar floodVar) {
        if (!Common.offBoardOrDeadly(x, y, cells) && floodVar.floodCache[x][y] != 1) {
            int terminationCount = flood(floodVar, x, y, cells).getFloodTerminationCount();
            return terminationCount > 0 && terminationCount < 399 ;
        }
        return false;
    }

    /**
     * floods to find a dead end.
     * @param floodVar  floodVar
     * @param x x coordinate
     * @param y y coordinate
     * @param cells cells
     * @return floodVar
     */
    private static FloodVar flood(FloodVar floodVar , int x, int y, Cell[][] cells) {
        if (!Common.offBoardOrDeadly(x, y, cells) && floodVar.getFloodTerminationCount() > 0
            && floodVar.getFloodCache()[x][y] != 1) {

            floodVar.floodTerminationCount--;
            floodVar.floodCache[x][y] = 1;

            for(var xy : Common.generateXYAllDirections(x,y,1)) {
                floodVar = flood(floodVar, xy, cells);
            }
        }
        return floodVar;
    }

    private static FloodVar flood(FloodVar floodVar, Common.Tuple tuple, Cell[][] cells) {
        return flood(floodVar, tuple.getX(), tuple.getY(), cells);
    }

    /**
     * Marks cells between the heads of us and op.
     * @param floodCache floodCache
     * @param op         opponent
     * @param us          us
     * @return           floodCache
     */
    private static int[][] closeCircle(Cell[][] cells, int[][] floodCache, Player op, Player us) {
        int usX = us.getX();
        int usY = us.getY();
        int opX = op.getX();
        int opY = op.getY();
        List<PathCell> pathCells = new AStarSearch(cells).findPath(cells[opX][opY], cells[usX][usY]);
        if (pathCells != null) {
            for (PathCell pathCell : pathCells) {
                floodCache[pathCell.getX()][pathCell.getY()] = 1;
            }
        }
        return floodCache;
    }

    /**
     * Raises the kill incentives by direction - combinations.
     * @param player             opponent
     * @param cells              cells
     * @param killingCells       cells with incentive
     * @param opDirection        direction of opponent
     * @param attackDirection    direction of the attack
     * @param width              width
     * @param height             height
     * @return                   Set of cells
     */
    private static Set<Cell> raiseKillIncentive(Player player, Cell[][] cells, Set<Cell> killingCells,
                                                Direction opDirection, Direction attackDirection, int width, int height) {
        int x = player.getX();
        int y = player.getY();
        int attackDistance = player.getSpeed() * initialAttackDistance;
        switch (opDirection) {
            case UP:
            case DOWN:
                if (Direction.LEFT == attackDirection) {
                    for (int i = 0; i < attackDistance; i++) {
                        if (x - i >= 0 && y - attackDistance >= 0 && y + attackDistance < height) {
                            evaluateCell(cells[x - i][y - attackDistance], killingCells);
                            evaluateCell(cells[x - i][y + attackDistance], killingCells);
                        }
                        if (x - attackDistance >= 0 && y - i >= 0 && y + i < height) {
                            evaluateCell(cells[x - attackDistance][y + i], killingCells);
                            evaluateCell(cells[x - attackDistance][y - i], killingCells);
                        }
                    }
                } else if (Direction.RIGHT == attackDirection) {
                    for (int i = 0; i < attackDistance; i++) {
                        if (x + i < width && y - attackDistance >= 0 && y + attackDistance < height) {
                            evaluateCell(cells[x + i][y - attackDistance], killingCells);
                            evaluateCell(cells[x + i][y + attackDistance], killingCells);
                        }
                        if (x + attackDistance < width && y - i >= 0 && y + i < height) {
                            evaluateCell(cells[x + attackDistance][y + i], killingCells);
                            evaluateCell(cells[x + attackDistance][y - i], killingCells);
                        }
                    }
                }
                break;
            case RIGHT:
            case LEFT:
                if (Direction.UP == attackDirection) {
                    for (int i = 0; i < attackDistance; i++) {
                        if (x - attackDistance >= 0 && x + attackDistance < width && y - i >= 0) {
                            evaluateCell(cells[x - attackDistance][y - i], killingCells);
                            evaluateCell(cells[x + attackDistance][y - i], killingCells);
                        }
                        if (x - i >= 0 && x + i < width && y - attackDistance >= 0) {
                            evaluateCell(cells[x - i][y - attackDistance], killingCells);
                            evaluateCell(cells[x + i][y - attackDistance], killingCells);
                        }
                    }
                } else if (Direction.DOWN == attackDirection) {
                    for (int i = 0; i < attackDistance; i++) {
                        if (x - attackDistance >= 0 && x + attackDistance < width && y + i < height) {
                            evaluateCell(cells[x - attackDistance][y + i], killingCells);
                            evaluateCell(cells[x + attackDistance][y + i], killingCells);
                        }
                        if (x - i >= 0 && x + i < width && y + attackDistance < height) {
                            evaluateCell(cells[x - i][y + attackDistance], killingCells);
                            evaluateCell(cells[x + i][y + attackDistance], killingCells);
                        }
                    }
                }
                break;
            default:
                throw new IllegalStateException();
        }
        return killingCells;
    }

    /**
     * Evaluate the cells.
     * @param cell          cell
     * @param killingCells  Set of Cells
     */
    private static void evaluateCell(Cell cell, Set<Cell> killingCells) {
        cell.setKillIncentive();
        killingCells.add(cell);
    }

    /**
     * Decides the better AttackDirection if both options are deadends.
     * @param op opponent
     * @param cells cells
     * @param evaluatedCells evaluatedCells
     * @param dir Direction
     * @param width width
     * @param height height
     * @param floodVarIf floodVar
     * @param floodVarElse floodVar
     */
    private static void decideAttackDirection(Player op, Cell[][] cells, Set<Cell> evaluatedCells, Direction dir,
                                              int width, int height, FloodVar floodVarIf, FloodVar floodVarElse) {
        if (floodVarIf.floodTerminationCount > floodVarElse.floodTerminationCount) {
            if (dir == Direction.UP || dir == Direction.DOWN) {
                raiseKillIncentive(op, cells, evaluatedCells, dir, Direction.LEFT, width, height);
            } else {
                raiseKillIncentive(op, cells, evaluatedCells, dir, Direction.UP, width, height);
            }
        } else {
            if (dir == Direction.UP || dir == Direction.DOWN) {
                raiseKillIncentive(op, cells, evaluatedCells, dir, Direction.RIGHT, width, height);
            } else {
                raiseKillIncentive(op, cells, evaluatedCells, dir, Direction.DOWN, width, height);
            }
        }
    }
}

