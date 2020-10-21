package de.uol.snakeinc.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

@Log4j2 @EqualsAndHashCode
public class Board {


    @Getter
    int width;
    @Getter
    private int height;
    @Getter
    private int cells[][];
    @Getter
    private int turn;
    @Getter
    private Player players[];
    @Getter
    private int us;

    public Board(int width, int height, Player[] players, int us) {
        this.width = width;
        this.height = height;
        this.players = players;
        this.turn = 1;
        this.us = us;

        cells = new int[width][height];
        for (int it = 0; it < width; it++) {
            int[] row = cells[it];
            Arrays.fill(row, 0);
        }

        for (Player player : players) {
            if (player == null) {
                continue;
            }
            cells[player.getX()][player.getY()] = player.getId();
        }
    }

    public Board(Board board) {
        this.width = board.getWidth();
        this.height = board.getHeight();
        this.players = board.getPlayers();
        this.turn = board.turn + 1;

        this.cells = new int[board.getCells().length][];
        for (int it = 0; it < width; it++) {
            this.cells[it] = Arrays.copyOf(board.getCells()[it], board.getCells()[it].length);
        }
    }

    public boolean isJumpTurn() {
        return turn % 6 == 0;
    }

    public Player getPlayer(int id) {
        return players[id];
    }

    public Player getOurPlayer() {
        return getPlayer(us);
    }

    public boolean isFree(int x, int y) {
        if (x < 0 || y < 0 || x >= cells.length || y >= cells.length ) {
            return false;
        }
        return cells[x][y] == 0;
    }
}
