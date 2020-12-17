package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Player;
import lombok.CustomLog;

import java.util.Set;

/**
 * Class that analyzes board configurations.
 */
@CustomLog
public class BoardAnalyzer {

    private Cell[][] cells;
    private Player[] players;
    private Player us;
    private Set<Cell> evaluatedCells;
    private int round = 0;

    public BoardAnalyzer(Cell[][] cells, Player[] players, Player us) {
        this.cells = cells;
        this.players = players;
        this.us = us;
    }

    /**
     * TODO JAVADOC.
     */
    public void analyze() {
        round++;
        OpponentMovesCalculation calc = new OpponentMovesCalculation(cells, players, us, this);
        evaluatedCells = calc.evaluate();
    }

    /**
     * Checks if players are in a defined reachable distance.
     * @param player1 player 1
     * @param player2 player 2
     * @return True - inDistance
     */
    public static Boolean inDistance(Player player1, Player player2) {
        int distance;
        distance = Math.abs(player1.getX() - player2.getX()) + Math.abs(player1.getY() - player2.getY());
        //TODO: Implement and use pathfinder-algorithm to check if the players can reach each other in max three rounds.
        return distance <= (player1.getSpeed() * 3) + (player2.getSpeed() * 3) + 6 && distance != 0;
    }

    /**
     * simple method to check if this is a jumping round.
     * @param roundsInFuture Todo this
     * @return Todo this
     */
    public boolean checkForJumping(int roundsInFuture) {
        return (round + roundsInFuture) % 6 == 0;
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
