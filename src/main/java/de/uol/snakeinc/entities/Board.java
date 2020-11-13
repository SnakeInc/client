package de.uol.snakeinc.entities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.vavr.Tuple2;
import lombok.CustomLog;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode
@CustomLog
public class Board {

    @Getter
    private final int width;
    @Getter
    private final int height;
    @Getter
    private int turn;
    @Getter
    private final Player[] players;
    @Getter
    private int us;

    @Getter
    private MapCoordinateBag map; //

    @Getter
    private int weight = 1;

    public Board(int width, int height, Player[] players, int us) {
        this.width = width;
        this.height = height;
        this.players = players;
        this.turn = 1;
        this.us = us;

        var start = Arrays.stream(players)
            .filter(Objects::nonNull)
            .filter(player -> player.getX() >= 0 && player.getX() < width
                && player.getY() >= 0 && player.getY() < height)
            .map(player -> new Coordinates(player.getX(), player.getY(), player.getId()));
        this.map = new MapCoordinateBag(start, width, height);

    }

    public Board(Board board) {
        this.width = board.getWidth();
        this.height = board.getHeight();
        this.players = board.getPlayers();
        this.turn = board.turn + 1;

        this.map = new MapCoordinateBag(board.map);
    }

    public Board(int numOfPlayers, int with, int hight, int us) {
        this.width = with;
        this.height = hight;
        this.players = new Player[numOfPlayers];
        this.us = us;
    }

    public void mapDispose() {
        map = null;
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

    public void setCells(int[][] cells) {
        this.map = new MapCoordinateBag(cells);
    }

    public boolean test(List<Coordinates> coordinates) {
        return map.test(coordinates);
    }

    /**
     * Parse board based on json-format.
     * @param json    json from websocket
     * @param players parsed players
     * @return parsed board
     */
    public static Board parseFromJson(JsonObject json, HashMap<Integer, Player> players) {
        int width = json.get("width").getAsInt();
        int height = json.get("height").getAsInt();
        int us = json.get("you").getAsInt();

        Player[] playersArray = new Player[players.size()];
        int count = 0;
        for (Integer position : players.keySet()) {
            playersArray[count] = players.get(position);
            count++;
        }

        Gson gson = new Gson();

        log.debug(json.get("cells").toString());
        int[][] cells = gson.fromJson(json.get("cells").toString(), int[][].class);

        Board board = new Board(width, height, playersArray, us);
        board.setCells(cells);

        return board;
    }

    public boolean isOnBoard(int x, int y) {
        return (-1 < x && x < width && -1 < y && y < height);
    }

    public boolean isOnBoard(Tuple2<Integer, Integer> tuple) {
        return isOnBoard(tuple._1, tuple._2);
    }

    public boolean isOnBoard(Coordinates coordinates) {
        return isOnBoard(coordinates.getX(), coordinates.getY());
    }

}
