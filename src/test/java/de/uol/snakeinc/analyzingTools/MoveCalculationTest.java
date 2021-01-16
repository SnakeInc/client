package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Direction;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

class MoveCalculationTest {

    @Property
    boolean testPreCalculateSpeed(@ForAll Action act,
                                  @ForAll Direction dir,
                                  @ForAll @IntRange(min = 1, max = 10) int speed) {
        var moveCalc = new MoveCalculation(new Cell[1][0], null, null);
        var res = moveCalc.preCalculate(act, dir, speed);
        int resSpeed = res.getSpeed();

        if (act == Action.CHANGE_NOTHING) {
            return speed == resSpeed;
        }

        if (act == Action.SPEED_UP) {
            return speed + 1 == resSpeed;
        }

        if (act == Action.SLOW_DOWN) {
            return speed - 1 == resSpeed;
        }

        if (act == Action.TURN_LEFT) {
            return speed == resSpeed;
        }

        if (act == Action.TURN_RIGHT) {
            return speed == resSpeed;
        }

        return false;
    }

    @Property
    boolean testPreCalculateDirection(@ForAll Action act,
                                      @ForAll Direction dir,
                                      @ForAll @IntRange(min = 1, max = 10) int speed) {
        var moveCalc = new MoveCalculation(new Cell[1][0], null, null);
        var res = moveCalc.preCalculate(act, dir, speed);
        var resDir = res.getDirection();

        if (act == Action.CHANGE_NOTHING) {
            return dir == resDir;
        }

        if (act == Action.SPEED_UP) {
            return dir == resDir;
        }

        if (act == Action.SLOW_DOWN) {
            return dir == resDir;
        }

        if (act == Action.TURN_LEFT) {
            return dir != resDir && moveCalc.preCalculate(Action.TURN_RIGHT, resDir, speed).getDirection() == dir;
        }

        if (act == Action.TURN_RIGHT) {
            return dir != resDir && moveCalc.preCalculate(Action.TURN_LEFT, resDir, speed).getDirection() == dir;
        }

        return false;
    }

    @Property
    boolean testPreCalculateDirection4x(@ForAll Direction dir, @ForAll @IntRange(min = 1, max = 10) int speed) {
        var moveCalc = new MoveCalculation(new Cell[1][0], null, null);
        var leftDir = dir;
        var rightDir = dir;
        for (int i = 0; i < 4; i++) {
            leftDir = moveCalc.preCalculate(Action.TURN_LEFT, leftDir, speed).getDirection();
            rightDir = moveCalc.preCalculate(Action.TURN_RIGHT, rightDir, speed).getDirection();
        }
        return leftDir == dir && rightDir == dir;
    }
    
}