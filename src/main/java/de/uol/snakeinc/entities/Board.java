package de.uol.snakeinc.entities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.vavr.Tuple2;
import lombok.CustomLog;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;

@CustomLog
public class Board {

    @Getter
    private int width;
    @Getter
    private int height;
    @Getter
    private int cells[][];
    @Getter
    private Player players[];
    @Getter
    private int turn;
    
    private int lastTurn = -1; // no turn is ever -1
    private CoordinateBag lastCoordinates = null; //  for memoized lazy getCoordinateBag;

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
            if (player.getX() >= 0 && player.getX() < width &&
                player.getY() >= 0 && player.getY() < height) {
                cells[player.getX()][player.getY()] = player.getId();
            }
        }
    }

    public Player getPlayer(int id) {
        return players[id];
    }

    public boolean isFree(int x, int y) {
        return cells[x][y] == 0;
    }

    public void setCells(int[][] cells) {
        if (cells.length == height && cells[0].length == width) {
            this.cells = cells;
        } else {
            log.error("setCells can only be used with same dimensions - Dimensions given: " +
                cells.length + "/" + cells[0].length + "- start parsing - performance may be impact");
            int finalCells[][] = new int[width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    finalCells[y][x] = cells[y][x];
                }
            }
            this.cells = finalCells;
        }
    }

    /**
     * Parse board based on json-format.
     * @param json json from websocket
     * @param players parsed players
     * @return parsed board
     */
    public static Board parseFromJson(JsonObject json, HashMap<Integer, Player> players) {
        int width = json.get("width").getAsInt();
        int height = json.get("height").getAsInt();

        Player[] playersArray = new Player[players.size()];
        int count = 0;
        for (Integer position : players.keySet()) {
            playersArray[count] = players.get(position);
            count++;
        }

        Gson gson = new Gson();

        log.debug(json.get("cells").toString());
        int[][] cells = gson.fromJson(json.get("cells").toString(), int[][].class);

        Board board = new Board(width, height, playersArray);
        board.setCells(cells);

        return board;
    }

    public boolean isOnBoard(int x, int y) {
        return (-1 < x && x < width && -1 < y && y < height);
    }

    public boolean isOnBoard(Tuple2<Integer, Integer> tuple) {
        return isOnBoard(tuple._1, tuple._2);
    }

    public CoordinateBag getCoordinateBag() {
        if (lastTurn != turn || lastCoordinates == null) {
            lastCoordinates = new CoordinateBag(this);
            lastTurn = turn;
        }
        return lastCoordinates;
    }
}
