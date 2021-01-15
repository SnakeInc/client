package de.uol.snakeinc;

import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Direction;

public abstract class Config {

    /********
     * Kill Algorithm
     *******/
    public static final int INITIAL_ATTACK_DISTANCE = 2;
    public static final int INITIAL_FLOOD_TERMINATION_COUNT = 300;

    /********
     * Move Calculation
     *******/
    public static final int SEARCHING_DEPTH = 10;
    public static final double BEST_ACTION_MULTIPLIER = 100D;
    public static final Action DEFAULT_BEST_ACTION = Action.CHANGE_NOTHING;

    /********
     * Opponent Moves Calculation
     *******/
    public static final int OPPONENT_MOVES_DEPTH = 3;

    /********
     * Section Calculator
     *******/
    public static final int RESOLUTION = 10;
    public static final double DIVISOR = 10D;
    public static final double CALCULATE_MIN = 100D;
    public static final double CALCULATE_MAX = 0D;
    public static final double AREA_RISK_INTERPOLATION_MAX = 1.2D;
    public static final double AREA_RISK_INTERPOLATION_MIN = 1.0D;

}
