package de.uol.snakeinc.entities;

import java.util.Set;

public class BoardAnalyzer {

    private Cell[][] cells;
    private Player[] players;
    private Player us;
    private Set<Tupel> evaluatedCells;

    public BoardAnalyzer(Cell[][] cells, Player[] players, Player us) {
        this.cells = cells;
        this.players = players;
        this.us = us;
    }

    public void analyze () {
        OpponentMovesCalculation calc = new OpponentMovesCalculation(cells, players, us);
        evaluatedCells = calc.evaluate();
    }

    public static Boolean inDistance(Player player1, Player player2) {
        int distance;
        distance = Math.abs(player1.getX()-player2.getX()+player1.getY() - player2.getY());
        if(distance > player1.getSpeed()*3 + player2.getSpeed()*3 || distance==0) {
            return false;
        } else {
            //TODO: Implement and use pathfinder-algorithm to check if the players can reach each other in max three rounds.
            return true;
        }
    }

    public void prepareNextPhase() {
        if (evaluatedCells != null)
        for (Tupel tupel : evaluatedCells) {
            cells[tupel.getX()][tupel.getY()].prepareNextPhase();
        }
    }

}
