package de.uol.snakeinc;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class for common small Calculation
 */
public abstract class Common {
    /**
     * tests if coordinates are on the board or the cell is deadly.
     * @param x x coordinate
     * @param y y coordinate
     * @return returns the test value
     */
    static public boolean offBoardOrDeadly(int x, int y, Cell[][] cells) {
        if (x < 0 || x >= cells.length || y < 0 || y >= cells[0].length) {
            return true;
        } else {
            return cells[x][y].isDeadly();
        }
    }

    static public Tuple generateXY(Direction dir, int x, int y, int amount) {
        switch (dir) {
            case UP:
                return new Tuple(x, y - amount);
            case DOWN:
                return new Tuple(x, y + amount);
            case LEFT:
                return  new Tuple(x - amount, y);
            case RIGHT:
                return  new Tuple(x + amount, y);
            default:
                throw new IllegalStateException();
        }
    }

    static public Tuple[] generateXYAllDirections(int x, int y, int amount) {
        return new Tuple[] {generateXY(Direction.DOWN, x, y, amount),
                            generateXY(Direction.UP, x, y, amount),
                            generateXY(Direction.LEFT, x, y, amount),
                            generateXY(Direction.RIGHT, x, y, amount)};
    }

    static public Tuple[] generateAllXYUpTo(Direction direction, int x, int y, int amount) {
        Tuple[] res;
        if(amount < 0) {
            res = new Tuple[-amount];
            for (int i = 0; i > amount; i--) {
                res[-i] = generateXY(direction, x,y, i);
            }
        } else {
            res = new Tuple[amount];
            for (int i = 0; i < amount; i++) {
                res[i] = generateXY(direction, x, y, i);
            }
        }
        return res;
    }

    @AllArgsConstructor
    @Getter
    public static class Tuple {
        private final int x;
        private final int y;
    }

    /**
     * turns the direction to the left.
     * @param dir Direction
     * @return Direction
     */
    public static Direction turnLeft(Direction dir) {
        switch (dir) {
            case UP:
                return Direction.LEFT;
            case DOWN:
                return Direction.RIGHT;
            case LEFT:
                return Direction.DOWN;
            case RIGHT:
                return Direction.UP;
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * turns the direction to the right.
     * @param dir Direction
     * @return Direction
     */
    public static Direction turnRight(Direction dir) {
        switch (dir) {
            case UP:
                return Direction.RIGHT;
            case DOWN:
                return Direction.LEFT;
            case LEFT:
                return Direction.UP;
            case RIGHT:
                return Direction.DOWN;
            default:
                throw new IllegalStateException();
        }
    }
}
