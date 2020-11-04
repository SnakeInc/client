package de.uol.snakeinc.entities;

public enum Direction {

    UP,
    DOWN,
    LEFT,
    RIGHT;

    public Direction turn(Action action) {
        switch (action) {
            case TURN_RIGHT:
                switch (this) {
                    case RIGHT:
                        return DOWN;
                    case DOWN:
                        return LEFT;
                    case LEFT:
                        return UP;
                    case UP:
                        return RIGHT;
                }
            case TURN_LEFT:
                switch (this) {
                    case RIGHT:
                        return UP;
                    case DOWN:
                        return RIGHT;
                    case LEFT:
                        return DOWN;
                    case UP:
                        return LEFT;
                }
            default:
                return this;
        }
    }
}
