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
    public static final int ROUNDS_AHEAD_FOR_KILL = 4;

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
    public static final double AREA_RISK_INTERPOLATION_MAX = 1.1D;
    public static final double AREA_RISK_INTERPOLATION_MIN = 1.0D;
    public static final double PATH_HIGHLIGHT_INTERPOLATION_MAX = 1.0D;
    public static final double PATH_HIGHLIGHT_INTERPOLATION_MIN = 0.9D;
    public static final int PATH_HIGHLIGHT_PATH_RANGE = 12;

    /********
     * Cell.
     *******/
    public static final double DEATH_VALUE = 10;
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

    /********
     * Dead End Flooding.
     *******/
    public static final int BLOCKS = 700;
    public static final double DEAD_END_INCENTIVE = 0.000000000000000000001D;
    public static final double INTERPOLATION_MIN = 1.0D;
    public static final double INTERPOLATION_MAX = 1.2D;
    public static final double FLOOD_ADD = 0.3D;
    public static final double DEAD_ABORT_BLOCKS = 10;

    /********
     * Connection Thread.
     *******/
    public static final int SLEEP_MILLIS = 100;

    /********
     * GUI.
     *******/
    public static final double BASE_RANGE = 0.2;
    public static final double DEAD_END_FLOODING_RANGE = 2.5;

    /********
     * GUI Cell.
     *******/
    public static final int GUI_CELL_SIZE = 10;
    public static final String BASE_COLOR = "BLACK";
    public static final String OUR_COLOR = "GREEN";
    public static final String ENEMY_COLOR = "RED";
    public static final double OPACITY_DIVISOR = 2.0D;

    /********
     * DeadEndRecognition.
     *******/
    public static final int MAX_RECOGNITION_CELL_COUNT = 775;
    public static final double LN_MULTIPLIER = 0.06;
    public static final double FUNCTION_Y_OFFSET = 1.4;
    public static final boolean RANDOM_TESTING = true;
    public static final int RANDOM_TESTING_LENGTH = 6;

}
