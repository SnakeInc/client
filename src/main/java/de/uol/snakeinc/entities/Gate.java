package de.uol.snakeinc.entities;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Gate {

    @Getter
    private final Cell front;
    @Getter
    private final Cell back;

    @Getter
    final Cell blocked;

    @Getter
    private final Set<Gate> fromFront;
    @Getter
    private final Set<Gate> formBack;

    private Gate(Cell front, Cell back, Cell blocked) {
        this.front = front;
        this.back = back;
        this.blocked = blocked;

        this.fromFront = new HashSet<>();
        this.formBack = new HashSet<>();
    }

    static List<Gate> getGates(EvaluationBoard board) {
        var cells = board.getCells();
        var height = board.getHeight();
        var width = board.getWidth();

        var gates = new ArrayList<Gate>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // variable names refer to the positions on the numpad,
                // if _5 is position (x,y), then _4 is the one left of it
                var _5 = cells[x][y];
                if (_5.isDeadly()) {
                    continue;
                }
                var _7 = y > 0 && x > 0 ? cells[x - 1][y - 1] : null;
                var _8 = y > 0 ? cells[x][y - 1] : null;
                var _9 = y > 0 && x + 1 < width ? cells[x + 1][y - 1] : null;
                var _4 = x > 0 ? cells[x - 1][y] : null;
                var _6 = x + 1 < width ? cells[x + 1][y] : null;
                var _1 = y + 1 < height && x > 0 ? cells[x - 1][y + 1] : null;
                var _2 = y + 1 < height ? cells[x][y + 1] : null;
                var _3 = y + 1 < height && x + 1 < width ? cells[x + 1][y + 1] : null;

                boolean _7b = _7 == null || _7.isDeadly();
                boolean _8b = _8 == null || _8.isDeadly();
                boolean _9b = _9 == null || _9.isDeadly();
                boolean _4b = _4 == null || _4.isDeadly();
                boolean _6b = _6 == null || _6.isDeadly();
                boolean _1b = _1 == null || _1.isDeadly();
                boolean _2b = _2 == null || _2.isDeadly();
                boolean _3b = _3 == null || _3.isDeadly();

                // X
                //#o#
                // X
                if (_4b && _6b && !_8b && !_2b) {
                    gates.add(new Gate(_8, _2, _5));
                }

                // # 
                //XoX
                // #
                if (!_4b && !_6b && _8b && _2b) {
                    gates.add(new Gate(_4, _6, _5));
                }

                //#
                //XX
                // #
                if (_7b && _2b && !_4b) {
                    gates.add(new Gate(_4, _5, null));
                }

                //  #
                // XX
                // #
                if (_2b && _9b && !_6b) {
                    gates.add(new Gate(_5, _6, null));
                }

                //todo add rest later

                var res = new ArrayList<Gate>(gates.size());

                for (var gate : gates) {
                    gates.remove(gate);
                    if (gate.blocked != null) {
                        for (var other : res) {
                            if (other.blocked != null) {
                                if (other.blocked == gate.back) {
                                    gate = new Gate()
                                }
                            }
                        }
                    }
                    res.add(gate);
                }
            }
        }
    }
}
