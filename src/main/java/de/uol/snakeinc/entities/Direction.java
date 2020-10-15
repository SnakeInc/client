package de.uol.snakeinc.entities;

import com.puppycrawl.tools.checkstyle.checks.blocks.LeftCurlyCheck;

public enum Direction {

    UP,
    DOWN,
    LEFT,
    RIGHT,
    INVALID;


    public Direction change(PlayerMove move) {
        switch (move) {
            case TURN_RIGHT:
                switch (this) {
                    case UP:
                        return RIGHT;
                    case DOWN:
                        return LEFT;
                    case LEFT:
                        return DOWN;
                    case RIGHT:
                        return UP;
                }
                break;
            case TURN_LEFT:
                switch (this) {
                    case UP:
                        return LEFT;
                    case DOWN:
                        return RIGHT;
                    case LEFT:
                        return UP;
                    case RIGHT:
                        return DOWN;
                }
                break;
            default:
                return INVALID;
        }
        return INVALID;
    }
}
