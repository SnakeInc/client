package de.uol.snakeinc;


import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

class CommonTest {

    @Property
    boolean offBoardTestPositive(@ForAll @IntRange(min = 10, max = 100) int cellsX,
                         @ForAll @IntRange(min = 10, max = 100) int cellsY,
                         @ForAll @IntRange(min = 11, max = 200) int x,
                         @ForAll @IntRange(min = 11, max = 200) int y) {
        /*
         * Swap values, so that CellsX/Y is always smaller than x, y;
         */
        if (cellsX > x) {
            var swap = cellsX;
            cellsX = x;
            x = swap;
        }

        if (cellsY > y) {
            var swap = cellsY;
            cellsY = y;
            y = swap;
        }

        var cells = new Cell[cellsX][cellsY];

        boolean res = Common.offBoardOrDeadly(x,y,cells);
        if (res == true) {
            return true;
        } else {
            return false;
        }
    }

    @Property
    boolean offBoardTestNegative(@ForAll @IntRange(min = 10, max = 100) int cellsX,
                                 @ForAll @IntRange(min = 10, max = 100) int cellsY,
                                 @ForAll @IntRange(min = -10, max = -1) int x,
                                 @ForAll @IntRange(min = -10, max = -1) int y) {

        var cells = new Cell[cellsX][cellsY];

        boolean res = Common.offBoardOrDeadly(x,y,cells);
        if (res == true) {
            return true;
        } else {
            return false;
        }
    }

    @Property
    boolean turnTests(@ForAll Direction dir) {
        return Common.turnRight(Common.turnLeft(dir)) == dir
            && Common.turnLeft(Common.turnRight(dir)) == dir
            && Common.turnLeft(Common.turnLeft(Common.turnLeft(Common.turnLeft(dir)))) == dir
            && Common.turnRight(Common.turnRight(Common.turnRight(Common.turnRight(dir)))) == dir;
    }

}