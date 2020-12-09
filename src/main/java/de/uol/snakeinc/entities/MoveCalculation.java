package de.uol.snakeinc.entities;

import lombok.CustomLog;

@CustomLog
public class MoveCalculation {

    private Cell[][] cells;
    private Player us;
    int width;
    int height;

    public MoveCalculation(Cell[][] cells, Player us) {
        this.cells = cells;
        this.us = us;
        this.width= cells[1].length;
        this.height = cells.length;

    }

    public Action calculateBestAction() {
        log.info("calculating BestAction!");
        double bestActionTmp = 100;
        Action bestAction = Action.CHANGE_NOTHING;
        double tmp;
        for (Action act : Action.values()) {
            tmp = calculate(act, us.getDirection(), us.getSpeed(), us.getX(), us.getY(), 0);
            if (tmp < bestActionTmp) {
                bestActionTmp = tmp;
                bestAction = act;
            }
        }
        return bestAction;
    }

    private double calculateAction(Direction dir, int x, int y, int speed, int depth) {
        log.info("Calculating in depth:" + depth);
        if (depth ==2) {
            return 0;
        }
        double bestAction = 100;
        double tmp = 100;
        for (Action act : Action.values()) {
            tmp = calculate(act, dir, speed, x, y,depth);
            if (tmp < bestAction) {
                bestAction = tmp;
            }
        }
        return tmp;
    }

    private double calculate(Action act, Direction dir,int x, int y, int speed, int depth) {
        switch (act) {
            case SPEED_UP:
                return calculateDirection(dir, x, y, speed + 1, depth);
            case CHANGE_NOTHING:
                return calculateDirection(dir, x, y, speed , depth);
            case SLOW_DOWN:
                return calculateDirection(dir, x, y, speed - 1, depth);
            case TURN_LEFT:
                return calculateDirection(Direction.LEFT, x, y, speed , depth);
            case TURN_RIGHT:
                return calculateDirection(Direction.RIGHT, x, y, speed + 1, depth);
        }
        return 0;
    }

    private double calculateDirection(Direction dir, int x, int y, int speed, int depth) {
        double result = 0;
        switch (dir) {
            case LEFT:
                for (int i = 1; i<speed+1;i++) {
                    if (x-i < 0 || x-i >= width || y <0 || y >= height ) {
                        return 100;
                    }
                    result = result + cells[x - i][y].getRisks();
                }
                return result + calculateAction(Direction.LEFT,x - speed, y - speed, speed, depth+1);

            case RIGHT:
                for (int i = 0; i<speed+1;i++) {
                    if (x+i < 0 || x+i >= width || y <0 || y >= height ) {
                        return 100;
                    }
                    result = result + cells[x + i][y].getRisks();
                }
                return result + calculateAction(Direction.RIGHT,x + speed, y, speed, depth+1);

            case DOWN:
                for (int i = 0; i<speed+1;i++) {
                    if (x < 0 || x >= width || y+i <0 || y+i >= height ) {
                        return 100;
                    }
                    result = result + cells[x][y + i].getRisks();
                }
                return result + calculateAction(Direction.DOWN,x, y + speed, speed, depth+1);

            case UP:
                for (int i = 0; i<speed+1;i++) {
                    if (x < 0 || x >= width || y-i <0 || y-i >= height ) {
                        return 100;
                    }
                    result = result + cells[x][y - i].getRisks();
                }
                return result + calculateAction(Direction.UP,x, y - speed, speed,  depth+1);

        }
        return result;
    }



}
