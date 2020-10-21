package de.uol.snakeinc.entities;


public enum Direction {

    UP,
    DOWN,
    LEFT,
    RIGHT,
    INVALID;

    /**
     * Changes the direction according to the given move.
     * @param move the move
     * @return the new Direction, stays the same if the move doesn't change the direction
     */
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
                return this;
        }
        return INVALID;
    }
}
