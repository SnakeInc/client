package de.uol.snakeinc.entities;

import java.util.HashSet;
import java.util.Set;

public class MovesCalculation {

    private Cell[][] cells;
    private Player [] players;
    private Player us;

    public MovesCalculation(Cell[][] cells, Player[] players, Player us) {
        this.cells = cells;
        this.players = players;
        this.us = us;
    }

    public Set<Tupel> evaluate(){
        int iD;
        int x;
        int y;
        int speed;

        for (int i=0; i < players.length; i++) {
            if (BoardAnalyzer.inDistance(us, players[i]))
            iD = players[i].getId();
            x = players[i].getX();
            y = players[i].getY();
            speed = players[i].getSpeed();


}
