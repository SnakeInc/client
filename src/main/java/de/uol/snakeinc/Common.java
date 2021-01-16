package de.uol.snakeinc;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Class for common small Calculation.
 */
public abstract class Common {
    /**
     * tests if coordinates are on the board or the cell is deadly.
     * @param x x coordinate
     * @param y y coordinate
     * @param cells Board represented as Cell Array
     * @return returns the test value
     */
    static public boolean offBoardOrDeadly(int x, int y, Cell[][] cells) {
        if (x < 0 || x >= cells.length || y < 0 || y >= cells[0].length) {
            return true;
        } else {
            return cells[x][y].isDeadly();
        }
    }

    /**
     * Generates a new Tuple of x y coordinates off set from the x y position in a direction.
     * @param dir the direction to off set in
     * @param x the x coordinate of the start Position
     * @param y the y coordinate of the start Position
     * @param amount the offset
     * @return new Coordinates
     */
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

    /**
     * Generates a new Tuple of x y coordinates off set from the x y position for all directions.
     * @param x the x coordinate of the start Position
     * @param y the y coordinate of the start Position
     * @param amount the offset
     * @return Array Coordinates
     */
    static public Tuple[] generateXYAllDirections(int x, int y, int amount) {
        return new Tuple[] {generateXY(Direction.DOWN, x, y, amount),
                            generateXY(Direction.UP, x, y, amount),
                            generateXY(Direction.LEFT, x, y, amount),
                            generateXY(Direction.RIGHT, x, y, amount)};
    }

    /**
     * Generates all Tuples of x y coordinates off set from the x y position up to the off set in a direction.
     * @param direction the direction to off set in
     * @param x the x coordinate of the start Position
     * @param y the y coordinate of the start Position
     * @param amount the offset
     * @return new Coordinates
     */
    static public Tuple[] generateAllXYUpTo(Direction direction, int x, int y, int amount) {
        Tuple[] res;
        if (amount < 0) {
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

    /**
     * Generates all Tuples of x y coordinates off set from the x y position up to the off set in a direction,
     * but excludes the starting position.
     * @param direction the direction to off set in
     * @param x the x coordinate of the start Position
     * @param y the y coordinate of the start Position
     * @param amount the offset
     * @return new Coordinates
     */
    static public Tuple[] generateAllXYUpToFromOne(Direction direction, int x, int y, int amount) {
        Tuple[] res;
        res = new Tuple[amount - 1];
        for (int i = 1; i < amount; i++) {
            res[i - 1] = generateXY(direction, x, y, i);
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
     * Assert Cell.
     * @param x position X
     * @param y position Y
     * @param cells cells
     */
    public static void assertCellXY(int x, int y, Cell[][] cells) {
        if (x < 0 || x >= cells.length || y < 0 || y >= cells[0].length) {
            return;
        }
        assert cells[x][y].getX() == x && cells[x][y].getY() == y;
    }
}
