package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.Config;
import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Player;
import de.uol.snakeinc.timetracking.TimeTracker;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Set;

/**
 * Class that analyzes board configurations.
 */
@Log4j2
public class BoardAnalyzer {

    @Getter
    private Set<Cell> evaluatedCells;

    @Getter
    private int round = 0;

    private SectionCalculator sectionCalculator;
    private DeadEndFlooding deadEndFlooding;

    public BoardAnalyzer(int width, int height) {
        this.sectionCalculator = new SectionCalculator(width, height);
        this.deadEndFlooding = new DeadEndFlooding(width, height);
    }

    /**
     * Is called once per round and initiates the analyzing.
     * The method to initiate other heuristics
     * @param us us
     * @param players active players
     * @param cells cells
     */
    public void analyze(Cell[][] cells, Player[] players, Player us) {
        TimeTracker timeTracker = new TimeTracker();
        round++;
        sectionCalculator.calculate(cells, us);
        timeTracker.logTime("Section-Calculation");
        OpponentMovesCalculation calc = new OpponentMovesCalculation(this);
        evaluatedCells = calc.evaluate(cells, players, us);
        timeTracker.logTime("OpponentMovement-Calculation");
        deadEndFlooding.calculate(cells, us);
        timeTracker.logTime("DeadEndFlooding-Calculation");
        DeadEndRecognition deadEndRecognition = new DeadEndRecognition(cells, us,this);
        deadEndRecognition.findDeadEnds();
        timeTracker.logTime("DeadEnd-Calculation");
        evaluatedCells.addAll(KillAlgorithm.killAlgorithm(cells, players, us));
        timeTracker.logTime("KillAlgorithm-Calculation");
        timeTracker.logFinal();
    }

    /**
     * Checks if players are in a defined reachable distance.
     * @param player1 player 1
     * @param player2 player 2
     * @param inRounds rounds till possible collision
     * @return True - inDistance
     */
    public static Boolean inDistance(Player player1, Player player2, int inRounds) {
        int distance = Math.abs(player1.getX() - player2.getX()) + Math.abs(player1.getY() - player2.getY());
        return distance <= (player1.getSpeed() * inRounds) + (player2.getSpeed() * inRounds) && distance != 0;
    }

    /**
     * simple method to check if this is a jumping round.
     * @param roundsInFuture how many rounds in future to check
     * @return if there is a jump in the round
     */
    public boolean checkForJumping(int roundsInFuture) {
        return (round + roundsInFuture) % Config.ROUNDS_PER_JUMP == 0;
    }

    /**
     * Prepares the cells for the next phase (f.e. deleting old values)
     */
    public void prepareNextPhase() {
        if (evaluatedCells != null) {
            log.info("Preparing Next Phase " + evaluatedCells.size());
            for (Cell cell : evaluatedCells) {
                cell.prepareNextPhase();
            }
            evaluatedCells.clear();
        }
    }

}
