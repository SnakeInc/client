package de.uol.snakeinc.analyzingTools;

import static org.junit.jupiter.api.Assertions.*;

import de.uol.snakeinc.entities.Action;
import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

import java.util.Arrays;

class MoveOrderTest {

    @Property
    boolean isAlwaysFull(@ForAll @IntRange(min = 1, max = 10) int speed,
                         @ForAll @IntRange(min = -4, max = 4) int leftRightBalance,
                         @ForAll @IntRange(min = 1, max = 9) int idealSpeedMin,
                         @ForAll @IntRange(min = 2, max = 10) int idealSpeedMax, @ForAll MoveOrder.SpiralForm spiral) {
        Assume.that(idealSpeedMin != idealSpeedMax);

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


}