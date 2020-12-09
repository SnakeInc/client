package de.uol.snakeinc.entities;

import lombok.CustomLog;

import java.util.HashSet;
import java.util.Set;

@CustomLog
public class OpponentMovesCalculation {

    private Cell[][] cells;
    private Player[] players;
    private Player us;

    public OpponentMovesCalculation(Cell[][] cells, Player[] players, Player us) {
        this.cells = cells;
        this.players = players;
        this.us = us;
    }

    public Set<Tupel> evaluate() {
        int x;
        int y;
        int speed;
        Set<Tupel> evaluatedCells = new HashSet<>();

        for (int i = 0; i < players.length; i++) {
            if (BoardAnalyzer.inDistance(us, players[i])) {
                x = players[i].getX();
                y = players[i].getY();
                speed = players[i].getSpeed();
                nextDepth(x, y, evaluatedCells, 1, speed);
            }
        }
        return evaluatedCells;
    }



    private Set<Tupel> nextDepth(int x, int y, Set<Tupel> evaluated, int depth, int speed) {
        log.info("Calculating Oppenent moves in depth:" + depth);

        if(depth == 2) {
            return evaluated;
        }
        //Recursive call
        recursiveRiskByDirection(x, y, evaluated, depth, speed, Direction.UP);
        recursiveRiskByDirection(x, y, evaluated, depth, speed, Direction.DOWN);
        recursiveRiskByDirection(x, y, evaluated, depth, speed, Direction.LEFT);
        recursiveRiskByDirection(x, y, evaluated, depth, speed, Direction.RIGHT);
        return evaluated;
    }

    private void recursiveRiskByDirection(int x, int y, Set<Tupel> evaluated, int depth, int speed, Direction dir) {
        switch (dir) {
            case UP:
                for (int j = 0; j < speed; j++) {
                    if (cells[x][y+j]==null || cells[x][y + j].isDeadly()) {
                        break;
                    } else {
                        evaluated.add(new Tupel(x, y));
                        cells[x][y + j].raiseActionRisk(depth);
                    }
                }
                nextDepth(x, y + speed, evaluated, depth + 1, speed);
            case DOWN:
                for (int j = 0; j < speed; j++) {
                    if (cells[x][y-j]==null || cells[x][y - j].isDeadly()) {
                        break;
                    } else {
                        evaluated.add(new Tupel(x, y));
                        cells[x][y - j].raiseActionRisk(depth);
                    }
                }
                nextDepth(x, y - speed, evaluated, depth + 1, speed);
            case RIGHT:
                for (int j = 0; j < speed; j++) {
                    if (cells[x+j][y]==null ||cells[x + j][y].isDeadly()) {
                        break;
                    } else {
                        evaluated.add(new Tupel(x, y));
                        cells[x + j][y].raiseActionRisk(depth);
                    }
                }
                nextDepth(x + speed, y, evaluated, depth + 1, speed);
            case LEFT:
                for (int j = 0; j < speed; j++) {
                    if (cells[x-j][y]==null || cells[x - j][y].isDeadly()) {
                        break;
                    } else {
                        evaluated.add(new Tupel(x, y));
                        cells[x - j][y].raiseActionRisk(depth);
                    }
                }
                nextDepth(x - speed, y, evaluated, depth + 1, speed);
        }
    }
}
