package de.uol.snakeinc.analyzingTools;

import static org.junit.jupiter.api.Assertions.*;

import de.uol.snakeinc.entities.Action;
import lombok.CustomLog;
import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.GenerationMode;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;

import java.util.Arrays;

class MoveOrderTest {

    /**
     * Tests that all actions are in the return value of MoveOrder.weights.
     * @param speed params
     * @param leftRightBalance params
     * @param idealSpeedMin params
     * @param idealSpeedMax params
     * @param spiral params
     * @return test value
     */
    @Property(generation = GenerationMode.EXHAUSTIVE)
    boolean isAlwaysFull(@ForAll @IntRange(min = 1, max = 10) int speed,
                         @ForAll @IntRange(min = -4, max = 4) int leftRightBalance,
                         @ForAll @IntRange(min = 1, max = 10) int idealSpeedMin,
                         @ForAll @IntRange(min = 1, max = 10) int idealSpeedMax, @ForAll MoveOrder.SpiralForm spiral) {

        if (idealSpeedMin > idealSpeedMax) {
            var swap = idealSpeedMax;
            idealSpeedMax = idealSpeedMin;
            idealSpeedMin = swap;
        }

        var order = MoveOrder.weights(speed, leftRightBalance, idealSpeedMin, idealSpeedMax, spiral);

        for (var act : Action.values()) {
            if (Arrays.stream(order).noneMatch(action -> action == act)) {
                return false;
            }
        }
        return true;
    }

    /**
     * tests that in a spiral the spiral direction comes before the other.
     * @param speed params
     * @param leftRightBalance params
     * @param idealSpeedMin params
     * @param idealSpeedMax params
     * @return test result
     */
    @Property(generation = GenerationMode.EXHAUSTIVE)
    boolean spiralTest(@ForAll @IntRange(min = 1, max = 10) int speed,
                        @ForAll @IntRange(min = -4, max = 4) int leftRightBalance,
                        @ForAll @IntRange(min = 1, max = 10) int idealSpeedMin,
                        @ForAll @IntRange(min = 1, max = 10) int idealSpeedMax) {


        if (idealSpeedMin > idealSpeedMax) {
            var swap = idealSpeedMax;
            idealSpeedMax = idealSpeedMin;
            idealSpeedMin = swap;
        }

        if(leftRightBalance == 0) {
            return true;
        }

        var res = MoveOrder.weights(speed, leftRightBalance, idealSpeedMin, idealSpeedMax, MoveOrder.SpiralForm.Spiral);

        if (leftRightBalance < 0) {
            for(var act : res) {
                switch (act) {
                    case TURN_LEFT: return true;
                    case TURN_RIGHT: return false;
                    default: continue;
                }
            }
        } else {
            for(var act : res) {
                switch (act) {
                    case TURN_LEFT: return false;
                    case TURN_RIGHT: return true;
                    default: continue;
                }
            }
        }
        return false; // should not com to this
    }

    /**
     * tests that in a nospiral the nospiral direction comes before the other.
     * @param speed params
     * @param leftRightBalance params
     * @param idealSpeedMin params
     * @param idealSpeedMax params
     * @return test result
     */
    @Property(generation = GenerationMode.EXHAUSTIVE)
    boolean noSpiralTest(@ForAll @IntRange(min = 1, max = 10) int speed,
                        @ForAll @IntRange(min = -4, max = 4) int leftRightBalance,
                        @ForAll @IntRange(min = 1, max = 10) int idealSpeedMin,
                        @ForAll @IntRange(min = 1, max = 10) int idealSpeedMax) {


        if (idealSpeedMin > idealSpeedMax) {
            var swap = idealSpeedMax;
            idealSpeedMax = idealSpeedMin;
            idealSpeedMin = swap;
        }

        if(leftRightBalance == 0) {
            return true;
        }

        var res = MoveOrder.weights(speed, leftRightBalance, idealSpeedMin, idealSpeedMax, MoveOrder.SpiralForm.NoSpiral);

        if (leftRightBalance < 0) {
            for(var act : res) {
                switch (act) {
                    case TURN_LEFT: return false;
                    case TURN_RIGHT: return true;
                    default: continue;
                }
            }
        } else {
            for(var act : res) {
                switch (act) {
                    case TURN_LEFT: return true;
                    case TURN_RIGHT: return false;
                    default: continue;
                }
            }
        }
        return false; // should not com to this
    }

    /**
     * tests that MoveOrder.weights goes to the ideal speed.
     * @param speed params
     * @param leftRightBalance params
     * @param idealSpeedMin params
     * @param idealSpeedMax params
     * @param spiralForm params
     * @return the test value.
     */
    @Property(generation = GenerationMode.EXHAUSTIVE)
    boolean SpeedTest(@ForAll @IntRange(min = 1, max = 10) int speed,
                      @ForAll @IntRange(min = -4, max = 4) int leftRightBalance,
                      @ForAll @IntRange(min = 1, max = 10) int idealSpeedMin,
                      @ForAll @IntRange(min = 1, max = 10) int idealSpeedMax,
                      @ForAll MoveOrder.SpiralForm spiralForm) {

        if (idealSpeedMin > idealSpeedMax) {
            var swap = idealSpeedMax;
            idealSpeedMax = idealSpeedMin;
            idealSpeedMin = swap;
        }

        var res = MoveOrder.weights(speed, leftRightBalance, idealSpeedMin, idealSpeedMax, spiralForm);

        if(speed <= idealSpeedMax && speed >= idealSpeedMin) {
            for(var act : res) {
                switch (act) {
                    case CHANGE_NOTHING: return true;
                    case SPEED_UP: return false;
                    case SLOW_DOWN: return  false;
                    default: continue;
                }
            }
        }

        if(speed < idealSpeedMin) {
            for(var act : res) {
                switch (act) {
                    case CHANGE_NOTHING: return false;
                    case SPEED_UP: return true;
                    case SLOW_DOWN: return  false;
                    default: continue;
                }
            }
        }

        if(speed > idealSpeedMax) {
            for(var act : res) {
                switch (act) {
                    case CHANGE_NOTHING: return false;
                    case SPEED_UP: return false;
                    case SLOW_DOWN: return true;
                    default: continue;
                }
            }
        }
        return false; // should not com to this
    }
}