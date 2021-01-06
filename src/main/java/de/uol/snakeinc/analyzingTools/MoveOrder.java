package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Action;

public abstract class MoveOrder {
    static Action[] weights(
            int speed, int leftRightBalance, int idealSpeedMin, int idealSpeedMax, SpiralForm spiral) {
        // negative left right balance means left
        //speed in right place
        if (idealSpeedMin <= speed && speed <= idealSpeedMax) {
            if (spiral == SpiralForm.Spiral) {
                if (leftRightBalance < 0) {
                    return new Action[]{Action.TURN_LEFT, Action.CHANGE_NOTHING,
                        Action.SLOW_DOWN, Action.SPEED_UP, Action.TURN_RIGHT};
                } else {
                    return new Action[]{Action.TURN_RIGHT, Action.CHANGE_NOTHING,
                        Action.SLOW_DOWN, Action.SPEED_UP, Action.TURN_LEFT};
                }
            } else { // not a spiral
                if (leftRightBalance < 0) {
                    return new Action[]{Action.CHANGE_NOTHING, Action.TURN_RIGHT,
                        Action.SLOW_DOWN, Action.SPEED_UP, Action.TURN_LEFT};
                } else {
                    return new Action[]{Action.CHANGE_NOTHING, Action.TURN_LEFT,
                        Action.SLOW_DOWN, Action.SPEED_UP, Action.TURN_RIGHT};
                }
            }
        } else if (idealSpeedMax < speed) {
            if (spiral == SpiralForm.Spiral) {
                if (leftRightBalance < 0) {
                    return new Action[]{Action.TURN_LEFT, Action.SLOW_DOWN,
                        Action.CHANGE_NOTHING, Action.SPEED_UP, Action.TURN_RIGHT};
                } else {
                    return new Action[]{Action.TURN_RIGHT, Action.SLOW_DOWN,
                        Action.CHANGE_NOTHING,  Action.SPEED_UP, Action.TURN_LEFT};
                }
            } else { // not a spiral
                if (leftRightBalance < 0) {
                    return new Action[]{Action.SLOW_DOWN, Action.TURN_RIGHT,
                        Action.CHANGE_NOTHING, Action.SPEED_UP, Action.TURN_LEFT};
                } else {
                    return new Action[]{Action.SLOW_DOWN, Action.TURN_LEFT,
                        Action.CHANGE_NOTHING, Action.SPEED_UP, Action.TURN_RIGHT};
                }
            }
        } else {
            if (spiral == SpiralForm.Spiral) {
                if (leftRightBalance < 0) {
                    return new Action[]{Action.TURN_LEFT, Action.SPEED_UP,
                        Action.CHANGE_NOTHING, Action.SLOW_DOWN, Action.TURN_RIGHT};
                } else {
                    return new Action[]{Action.TURN_RIGHT, Action.SPEED_UP,
                        Action.CHANGE_NOTHING, Action.SLOW_DOWN, Action.TURN_LEFT};
                }
            } else { // not a spiral
                if (leftRightBalance < 0) {
                    return new Action[]{Action.SPEED_UP, Action.TURN_RIGHT,
                        Action.CHANGE_NOTHING, Action.SLOW_DOWN, Action.TURN_LEFT};
                } else {
                    return new Action[]{Action.SPEED_UP, Action.TURN_LEFT,
                        Action.CHANGE_NOTHING, Action.SLOW_DOWN, Action.TURN_RIGHT};
                }
            }
        }
    }

    public static enum  SpiralForm {
        Spiral,
        NoSpiral,
    }


}
