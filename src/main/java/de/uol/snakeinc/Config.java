package de.uol.snakeinc;

import de.uol.snakeinc.analyzingTools.MoveOrder;
import de.uol.snakeinc.entities.Action;

public abstract class Config {

    public static final int ROUNDS_PER_JUMP = 6;
    public static final int SPEED_MIN = 1;
    public static final int SPEED_MAX = 10;
    public static final int MINIMUM_JUMP_SPEED = 3;

    /********
     * Kill Algorithm.
     *******/
    public static final int INITIAL_ATTACK_DISTANCE = 2;
    public static final int INITIAL_FLOOD_TERMINATION_COUNT = 200;

    /********
     * Move Calculation.
     *******/
    public static final int SEARCHING_DEPTH = 10;
    public static final double BEST_ACTION_MULTIPLIER = 100D;
    public static final Action DEFAULT_BEST_ACTION = Action.CHANGE_NOTHING;
    public static final double DEATH_VALUE_BASE = 3D;

    /********
     * Opponent Moves Calculation.
     *******/
    public static final int OPPONENT_MOVES_DEPTH = 3;

    /********
     * Section Calculator.
     *******/
    public static final int RESOLUTION = 32; // / 2 = 16 / 2 = 8 / 2 = 4 / 2 = 2
    public static final int ITERATIONS = 4;
    public static final int[] MULTIPLICATIONS = new int[] { 1, 3, 9, 27, 81 };
    public static final double DIVISOR = 32D;
    public static final double AREA_RISK_INTERPOLATION_MAX = 1.2D;
    public static final double AREA_RISK_INTERPOLATION_MIN = 1.0D;
    public static final double PATH_HIGHLIGHT_INTERPOLATION_MAX = 1.0D;
    public static final double PATH_HIGHLIGHT_INTERPOLATION_MIN = 0.8D;
    public static final int PATH_HIGHLIGHT_PATH_RANGE = 12;

    /********
     * Cell.
     *******/
    public static final int DEATH_VALUE = 10;
    public static final double MOVE_RISK_1 = 1.25D;
    public static final double MOVE_RISK_2 = 1.0625D;
    public static final double MOVE_RISK_3 = 1.015625D;
    public static final double KILL_INCENTIVE = 0.1D;

    /********
     * MoveOrder.
     *******/
    public static final int LEFT_RIGHT_BALANCE_MAX = 3;
    public static final int IDEAL_MIN_SPEED = 1;
    public static final int IDEAL_MAX_SPEED = 2;
    public static final MoveOrder.SpiralForm IDEAL_SPIRAL_FORM = MoveOrder.SpiralForm.NoSpiral;



}
