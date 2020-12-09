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
        this.height= cells[1].length;
        this.width = cells.length;
    }

    public Action calculateBestAction() {
        log.info("calculating BestAction!");
        double bestActionTmp = 100;
        Action bestAction = Action.CHANGE_NOTHING;
        double tmp;
        for (Action act : Action.values()) {
            tmp = calculate(act, us.getDirection(), us.getX(), us.getY(), us.getSpeed(), 1);
            System.out.println(tmp);
            if (tmp < bestActionTmp) {
                bestActionTmp = tmp;
                bestAction = act;
                System.out.println("Action: " + bestAction + "result: " + bestActionTmp);
            }
        }
        return bestAction;
    }

    private double calculateAction(Direction dir, int x, int y, int speed, int depth) {
        if (depth ==3) {
            return 1;
        }
        double bestAction = 100;
        double tmp = 100;
        for (Action act : Action.values()) {
            tmp = calculate(act, dir, speed, x, y,depth);
            if (tmp < bestAction) {
                bestAction = tmp;
            }
        }
        return bestAction;
    }

    private double calculate(Action act, Direction dir,int x, int y, int speed, int depth) {
        switch (act) {
            case SPEED_UP:
                if (speed + 1 > 10) {
                    return 10 * depth;
                }
                return calculateDirection(dir, x, y, speed + 1, depth);
            case CHANGE_NOTHING:
                return calculateDirection(dir, x, y, speed , depth);
            case SLOW_DOWN:
                if (speed - 1 == 0) {
                    return 10 * depth;
                }
                return calculateDirection(dir, x, y, speed - 1, depth);
            case TURN_LEFT:
                switch (dir) {
                    case UP:
                        return calculateDirection(Direction.LEFT, x, y, speed, depth);
                    case DOWN:
                        return calculateDirection(Direction.RIGHT, x, y, speed, depth);
                    case RIGHT:
                        return calculateDirection(Direction.UP, x, y, speed, depth);
                    case LEFT:
                        return calculateDirection(Direction.DOWN, x, y, speed, depth);
                }
            case TURN_RIGHT:
                switch (dir) {
                    case UP:
                        return calculateDirection(Direction.RIGHT, x, y, speed, depth);
                    case DOWN:
                        return calculateDirection(Direction.LEFT, x, y, speed, depth);
                    case RIGHT:
                        return calculateDirection(Direction.DOWN, x, y, speed, depth);
                    case LEFT:
                        return calculateDirection(Direction.UP, x, y, speed, depth);
                }        }
        return 1;
    }

    private double calculateDirection(Direction dir, int x, int y, int speed, int depth) {
        double result = 1;
        switch (dir) {
            case LEFT:
                for (int i = 1; i < speed+1;i++) {
                    if (x-i < 0 || x - i >= width || y < 0 || y >= height) {
                        log.info("Down");
                        return 10 * depth;
                    } else if (cells[x - i][y].isDeadly()) {
                        return 10 * depth;
                    }
                    result = result * cells[x - i][y].getRisks();
                }
                return result * calculateAction(Direction.LEFT,x - speed, y - speed, speed, depth+1);

            case RIGHT:
                for (int i = 1; i<speed+1;i++) {
                    if (x + i < 0 || x + i >= width || y < 0 || y >= height) {
                        log.info("Right");
                        return 10 * depth;
                    } else if (cells[x + i][y].isDeadly()) {
                        return 10 * depth;
                    }
                    result = result * cells[x + i][y].getRisks();
                }
                return result * calculateAction(Direction.RIGHT,x + speed, y, speed, depth+1);

            case DOWN:
                for (int i = 1; i<speed+1;i++) {
                    if (x < 0 || x >= width || y + i < 0 || y + i >= height) {
                        log.info("Down");
                        return 10 * depth;
                    } else if (cells[x][y + 1].isDeadly()) {
                        return 10 * depth;
                    }
                    result = result * cells[x][y + i].getRisks();
                }
                return result * calculateAction(Direction.DOWN,x, y + speed, speed, depth+1);

            case UP:
                for (int i = 1; i<speed+1;i++) {
                    if (x < 0 || x >= width || y-i <0 || y-i >= height) {
                        log.info("Up");
                        return 10 * depth;
                        } else if (cells[x][y - i].isDeadly()) {
                        return 10 * depth;
                    }
                    result = result * cells[x][y - i].getRisks();
                }
                return result * calculateAction(Direction.UP,x, y - speed, speed,  depth+1);
        }
        return result;
    }



}
