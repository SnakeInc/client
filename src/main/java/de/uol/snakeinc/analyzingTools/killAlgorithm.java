package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

public class killAlgorithm {

    int [][][] floodCache;
    int floodTerminationCount = 400;

    public Cell[] killAlgorithm(Cell[][] cells, Player[] players, Player us) {
        Set<Cell> killingCells = new HashSet<>();
        int width = cells.length;
        int height = cells[1].length;
        for (int i = 0; i < players.length; i++) {
            if (BoardAnalyzer.inDistance(us, players[i], 4)) {
                clearFloodCache(width, height);
                closeCircle(cells, players[i], us);
                int x = players[i].getX();
                int y = players[i].getY();
                int speed = players[i].getSpeed();
                Direction dir = players[i].getDirection();
                switch (dir) {
                    case UP:
                        if (checkForDeadEnd(x - 1, y, width, height, cells)) {
                            killingCells = raiseKillIncentive(speed, x - (3 * speed), y, cells, killingCells, dir);
                        } else if (checkForDeadEnd(x + 1, y, width, height, cells)) {
                            killingCells = raiseKillIncentive(speed, x + (3 * speed), y, cells, killingCells, dir);
                        }
                        break;
                    case DOWN:
                        if (checkForDeadEnd(x - 1, y, width, height, cells)) {
                            killingCells = raiseKillIncentive(speed, x - (3 * speed), y, cells, killingCells, dir);
                        } else if (checkForDeadEnd(x + 1, y, width, height, cells)) {
                            killingCells = raiseKillIncentive(speed, x + (3 * speed), y, cells, killingCells, dir);
                        }
                            break;
                    case LEFT:
                        if (checkForDeadEnd(x, y - 1, width, height, cells)) {
                            killingCells = raiseKillIncentive(speed, x, y - (speed * 3), cells, killingCells, dir);
                        } else if (checkForDeadEnd(x, y + 1, width, height, cells)) {
                            killingCells = raiseKillIncentive(speed, x, y + (speed * 3), cells, killingCells, dir);
                        }
                            break;
                    case RIGHT:
                        if (checkForDeadEnd(x, y - 1, width, height, cells)) {
                            killingCells = raiseKillIncentive(speed, x, y - (speed * 3), cells, killingCells, dir);
                        } else if (checkForDeadEnd(x, y + 1, width, height, cells)) {
                            killingCells = raiseKillIncentive(speed, x, y + (speed * 3), cells, killingCells, dir);
                        }
                            break;
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
        if (floodCache[x][y][0] == 1 && x < 0 && x >= width && y < 0 && y >= height && cells[x][y].isDeadly() && floodTerminationCount > 0) {
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
                floodCache[opY][opY - i][0] = 1;
            }
        } else if (diffY < 0) {
            for (int i = 0; i < -diffY; i++) {
                floodCache[opY][opY + i][0] = 1;
            }
        }
    }

        private void clearFloodCache ( int width, int height){
            floodCache = new int[width][height][0];
            floodTerminationCount = 400;
        }

        private Cell raiseKillIncentive(int speed, int x, int y, Cell[][] cells, Set<Cell> killingCells, Direction dir) {
        switch (dir) {
            case UP:
                cells[x][y + (speed * 3)].setKillIncentive();
                killingCells.add(cells[x][y + (speed * 3)]);
                for (int i = 0; i < speed * 3; i++) {
                    cells[][]
                }
        }
        }
}
