package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;

public class Gates {

    static void markGates(Cell[][] cells) {
        var height = cells[0].length;
        var width = cells.length;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // variable names refer to the positions on the numpad,
                // if _5 is position (x,y), then _4 is the one left of it
                var _5 = cells[x][y];
                if (_5.isDeadly()) {
                    continue;
                }
                //var _7 = y > 0 && x > 0 ? cells[x - 1][y - 1] : null;
                var _8 = y > 0 ? cells[x][y - 1] : null;
                //var _9 = y > 0 && x + 1 < width ? cells[x + 1][y - 1] : null;
                var _4 = x > 0 ? cells[x - 1][y] : null;
                var _6 = x + 1 < width ? cells[x + 1][y] : null;
                //var _1 = y + 1 < height && x > 0 ? cells[x - 1][y + 1] : null;
                var _2 = y + 1 < height ? cells[x][y + 1] : null;
                //var _3 = y + 1 < height && x + 1 < width ? cells[x + 1][y + 1] : null;

                //boolean _7b = _7 == null || _7.isDeadly();
                boolean _8b = _8 == null || _8.isDeadly();
                //boolean _9b = _9 == null || _9.isDeadly();
                boolean _4b = _4 == null || _4.isDeadly();
                boolean _6b = _6 == null || _6.isDeadly();
                //boolean _1b = _1 == null || _1.isDeadly();
                boolean _2b = _2 == null || _2.isDeadly();
                //boolean _3b = _3 == null || _3.isDeadly();

                //
                //#o#
                //
                if (_4b && _6b) {
                    _5.raiseActionRisk(1);
                }

                // # 
                // o
                // #
                if (_8b && _2b) {
                    _5.raiseActionRisk(1);
                }
            }
        }
    }
}
