package de.uol.snakeinc.entities;

public class BoardAnalyzer {

    private Cell[][] cells;
    private Player[] players;
    private Player us;

    public BoardAnalyzer(Cell[][] cells, Player[] players, Player us) {
        this.cells = cells;
        this.players = players;
        this.us = us;
    }

    public void analyze (Cell[][] cells, Player[] players, Player us) {

    }

    public static Boolean inDistance(Player player1, Player player2) {
        int distance;
        distance = Math.abs(player1.getX()-player2.getX()+player1.getY() - player2.getY());
        if(distance > player1.getSpeed()*3 + player2.getSpeed()*3) {
            return false;
        } else {
            //TODO: Implement and use pathfinder-algorithm to check if the players can reach each other in max three rounds.
            return true;
        }
    }

}
