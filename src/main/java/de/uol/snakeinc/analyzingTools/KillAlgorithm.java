package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

public class KillAlgorithm {

    /**
     * Sets incentives if another Player can be attacked.
     * @param cells     cells
     * @param players   players
     * @param us        us
     * @return          Set of cells
     */
    public Set<Cell> killAlgorithm(Cell[][] cells, Player[] players, Player us) {
        Set<Cell> evaluatedCells = new HashSet<>();
        int width = cells.length;
        int height = cells[1].length;
        for (int i = 0; i < players.length; i++) {
            if (BoardAnalyzer.inDistance(us, players[i], 4)) {
                int[][][] floodCache = new int [width][height][1];
                floodCache = closeCircle(floodCache, players[i], us);
                FloodVar floodVarIf = new FloodVar(400, floodCache);
                FloodVar floodVarElse = new FloodVar(400, floodCache);
                Player op = players[i];
                int x = players[i].getX();
                int y = players[i].getY();
                Direction dir = players[i].getDirection();
                switch (dir) {
                    case DOWN:
                    case UP:
                        if (checkForDeadEnd(x - 1, y, width, height, cells, floodVarIf)) {
                            evaluatedCells =
                                raiseKillIncentive(op, cells, evaluatedCells, dir, Direction.LEFT, width, height);
                        } else if (checkForDeadEnd(x + 1, y, width, height, cells, floodVarElse)) {
                            evaluatedCells =
                                raiseKillIncentive(op, cells, evaluatedCells, dir, Direction.RIGHT, width, height);
                        }
                        break;
                    case RIGHT:
                    case LEFT:
                        if (checkForDeadEnd(x, y - 1, width, height, cells, floodVarIf)) {
                            evaluatedCells =
                                raiseKillIncentive(op, cells, evaluatedCells, dir, Direction.UP, width, height);
                        } else if (checkForDeadEnd(x, y + 1, width, height, cells, floodVarElse)) {
                            evaluatedCells =
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
    public class FloodVar {

        int floodTerminationCount;
        int[][][] floodCache;
    }

    /**
     * checks for dead end.
     * @param floodVar  floodVariables
     * @param x         x coordinate
     * @param y         y coordinate
     * @param width     width
     * @param height    height
     * @param cells     cells
     * @return          true if theres a dead end
     */
    private boolean checkForDeadEnd(int x, int y, int width, int height, Cell[][] cells, FloodVar floodVar) {
        return flood(floodVar, x, y, width, height, cells).getFloodTerminationCount() > 0;
    }

    /**
     * floods to find a dead end.
     * @param floodVar  floodVar
     * @param x         x coordinate
     * @param y         y coordinate
     * @param width     width
     * @param height    height
     * @param cells     cells
     */
    private FloodVar flood(FloodVar floodVar ,int x, int y, int width, int height, Cell[][] cells) {
        if (x >= 0 && x < width && y >= 0 && y < height && !cells[x][y].isDeadly()
            && floodVar.getFloodTerminationCount() > 0 && floodVar.getFloodCache()[x][y][0] != 1) {

            floodVar.floodTerminationCount--;
            floodVar.floodCache[x][y][0] = 1;

            floodVar = flood(floodVar, x - 1, y, width, height, cells);
            floodVar = flood(floodVar, x + 1, y, width, height, cells);
            floodVar = flood(floodVar, x, y + 1, width, height, cells);
            floodVar = flood(floodVar, x, y - 1, width, height, cells);
        }

        return floodVar;
    }

    /**
     * Marks cells between the heads of us and op.
     * @param floodCache floodCache
     * @param op         opponent
     * @param us          us
     * @return           floodCache
     */
    private int[][][] closeCircle(int[][][] floodCache, Player op, Player us) {
        int usX = us.getX();
        int usY = us.getY();
        int opX = op.getX();
        int opY = op.getY();
        int diffX = usX - opX;
        int diffY = usY - opY;
        if (diffX >= 0) {
            for (int i = 0; i < diffX; i++) {
                floodCache[usX - i][usY][0] = 1;
            }
        } else if (diffX < 0) {
            for (int i = 0; i < - diffX; i++) {
                floodCache[usX + i][usY][0] = 1;
            }
        }
        if (diffY >= 0) {
            for (int i = 0; i < diffY; i++) {
                floodCache[opX][opY + i][0] = 1;
            }
        } else if (diffY < 0) {
            for (int i = 0; i < - diffY; i++) {
                floodCache[opX][opY - i][0] = 1;
            }
        }
        return floodCache;
    }

    /**
     * Raises the kill incentives by direction - combinations
     * @param player             opponent
     * @param cells              cells
     * @param killingCells       cells with incentive
     * @param opDirection        direction of opponent
     * @param attackDirection    direction of the attack
     * @param width              width
     * @param height             height
     * @return                   Set of cells
     */
    private Set<Cell> raiseKillIncentive(Player player, Cell[][] cells, Set<Cell> killingCells,
                                         Direction opDirection, Direction attackDirection, int width, int height) {
        int x = player.getX();
        int y = player.getY();
        int attackDistance = player.getSpeed() * 3;
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
    private void evaluateCell(Cell cell, Set<Cell> killingCells) {
        cell.setKillIncentive();
        killingCells.add(cell);
    }
}
