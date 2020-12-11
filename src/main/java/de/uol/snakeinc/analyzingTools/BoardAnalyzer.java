package de.uol.snakeinc.analyzingTools;

import de.uol.snakeinc.entities.Cell;
import de.uol.snakeinc.entities.Player;
import de.uol.snakeinc.entities.Tupel;
import lombok.CustomLog;
import lombok.Getter;

import java.util.Set;

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

    public void analyze () {
        OpponentMovesCalculation calc = new OpponentMovesCalculation(cells, players, us, this);
        evaluatedCells = calc.evaluate();
        round++;
    }

    public static Boolean inDistance(Player player1, Player player2) {
        int distance;
        distance = Math.abs(player1.getX()-player2.getX()) + Math.abs(player1.getY() - player2.getY());
        if(distance > player1.getSpeed()*3 + player2.getSpeed()*3 || distance==0) {
            return false;
        } else {
            //TODO: Implement and use pathfinder-algorithm to check if the players can reach each other in max three rounds.
            return true;
        }
    }

    public boolean checkForJumping(int roundsInFuture) {
        return round +roundsInFuture % 6 == 0;
    }

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
