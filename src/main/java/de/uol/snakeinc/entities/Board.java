package de.uol.snakeinc.entities;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

@Log4j2
public class Board {


    @Getter
    int width;
    @Getter
    private int height;

    @Getter
    private int cells[][];

    private Player players[];

    public Board(int width, int height, Player[] players) {
        this.width = width;
        this.height = height;
        this.players = players;

        cells = new int[width][height];
        for (int it = 0; it < width; it++) {
            int[] row = cells[it];
            Arrays.fill(row, 0);
        }

        for (Player player : players) {
            cells[player.getX()][player.getY()] = player.getId();
        }
    }

    public Player getPlayer(int id) {
        return players[id];
    }

    public boolean isFree(int x, int y) {
        return cells[x][y] == 0;
    }
}
