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
}
