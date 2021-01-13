package de.uol.snakeinc;

import de.uol.snakeinc.entities.Cell;

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
}
