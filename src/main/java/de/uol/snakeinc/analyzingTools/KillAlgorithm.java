package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;

import java.util.HashSet;
import java.util.Set;

public class KillAlgorithm {

    int[][][] floodCache;
    int floodTerminationCount = 400;

    public Set<Cell> killAlgorithm(Cell[][] cells, Player[] players, Player us) {
        Set<Cell> killingCells = new HashSet<>();
        int width = cells.length;
        int height = cells[1].length;
        for (int i = 0; i < players.length; i++) {
            if (BoardAnalyzer.inDistance(us, players[i], 4)) {
                clearFloodCache(width, height);
                closeCircle(cells, players[i], us);
                Player op = players[i];
                int x = players[i].getX();
                int y = players[i].getY();
                int speed = players[i].getSpeed();
                Direction dir = players[i].getDirection();
                switch (dir) {
                    case DOWN:
                    case UP:
                        if (checkForDeadEnd(x - 1, y, width, height, cells)) {
                            killingCells = raiseKillIncentive(op, cells, killingCells, dir, Direction.LEFT, width, height);
                        } else if (checkForDeadEnd(x + 1, y, width, height, cells)) {
                            killingCells = raiseKillIncentive(op, cells, killingCells, dir, Direction.RIGHT, width, height);
                        }
                        break;
                    //      case DOWN:
                    //        if (checkForDeadEnd(x - 1, y, width, height, cells)) {
                    //          killingCells = raiseKillIncentive(op, cells, killingCells, dir, Direction.LEFT, width, height);
                    //    } else if (checkForDeadEnd(x + 1, y, width, height, cells)) {
                    //      killingCells = raiseKillIncentive(op, cells, killingCells, dir, Direction.RIGHT, width, height);
                    //}
                    //  break;
                    case RIGHT:
                    case LEFT:
                        if (checkForDeadEnd(x, y - 1, width, height, cells)) {
                            killingCells = raiseKillIncentive(op, cells, killingCells, dir, Direction.UP, width, height);
                        } else if (checkForDeadEnd(x, y + 1, width, height, cells)) {
                            killingCells = raiseKillIncentive(op, cells, killingCells, dir, Direction.DOWN, width, height);
                        }
                        break;
                    //            case RIGHT:
                    //              if (checkForDeadEnd(x, y - 1, width, height, cells)) {
                    //                killingCells = raiseKillIncentive(op, cells, killingCells, dir, Direction.UP, width, height);
                    //          } else if (checkForDeadEnd(x, y + 1, width, height, cells)) {
                    //            killingCells = raiseKillIncentive(op, cells, killingCells, dir, Direction.DOWN, width, height);
                    //      }
                    //        break;
                }
            }
        }
        return killingCells;
    }

    private boolean checkForDeadEnd(int x, int y, int width, int height, Cell[][] cells) {
        flood(x, y, width, height, cells);
        return floodTerminationCount > 0;
    }

    private void flood(int x, int y, int width, int height, Cell[][] cells) {
        if (x >= 0 && x < width && y >= 0 && y < height && !cells[x][y].isDeadly() && floodTerminationCount > 0 && floodCache[x][y][0] != 1) {
            floodTerminationCount--;
            floodCache[x][y][0] = 1;

            flood(x - 1, y, width, height, cells);
            flood(x + 1, y, width, height, cells);
            flood(x, y + 1, width, height, cells);
            flood(x, y - 1, width, height, cells);
        }
    }

    private void closeCircle(Cell[][] cells, Player op, Player us) {
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
            for (int i = 0; i < -diffX; i++) {
                floodCache[usX + i][usY][0] = 1;
            }
        }
        if (diffY >= 0) {
            for (int i = 0; i < diffY; i++) {
                floodCache[opX][opY + i][0] = 1;
            }
        } else if (diffY < 0) {
            for (int i = 0; i < -diffY; i++) {
                floodCache[opX][opY - i][0] = 1;
            }
        }
    }

    private void clearFloodCache(int width, int height) {
        floodCache = new int[width][height][1];
        floodTerminationCount = 400;
    }

    private Set<Cell> raiseKillIncentive(Player player, Cell[][] cells, Set<Cell> killingCells, Direction opDirection, Direction attackDirection, int width, int height) {
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
        }
        return killingCells;
    }

    private void evaluateCell(Cell cell, Set<Cell> killingCells) {
        cell.setKillIncentive();
        killingCells.add(cell);
    }
}
