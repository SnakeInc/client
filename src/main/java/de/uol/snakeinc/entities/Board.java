package de.uol.snakeinc.entities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.uol.snakeinc.possibleMoves.ActionPlayerCoordinates;
import io.vavr.Tuple2;
import lombok.CustomLog;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@EqualsAndHashCode
@CustomLog
public class Board {

    @Getter
    private int width;
    @Getter
    private int height;
    @Getter
    private int turn;
    @Getter
    private HashMap<Integer, Player> players;
    @Getter
    private int us;

    @Getter
    private MapCoordinateBag map; //

    private int weight = 1;

    public Board(int width, int height, Player[] players, int us) {
        this.width = width;
        this.height = height;
        this.players = new HashMap<>(players.length - 1);
        this.turn = 1;
        this.us = us;

        for (int i = 1; i < players.length; i++) {
            this.players.put(i, players[i]);
        }

        var start = Arrays.stream(players)
            .filter(player -> player.getX() >= 0 && player.getX() < width
                && player.getY() >= 0 && player.getY() < height)
            .map(player -> new Coordinates(player.getX(), player.getY(), player.getId(), turn).getTuple());
        this.map = new MapCoordinateBag(start);

    }

    public Board(Board board) {
        this.width = board.getWidth();
        this.height = board.getHeight();
        this.players = board.getPlayers();
        this.turn = board.turn + 1;

        this.map = new MapCoordinateBag(board.map);
    }

    public Board(Board board, Iterator<ActionPlayerCoordinates> iterator, int depth) {
        this.width = board.width;
        this.height = board.height;
        this.turn = board.turn + 1;
        this.players = new HashMap<>();
        this.weight = board.weight;

        this.map = new MapCoordinateBag(board.map);
        var apcs = new ArrayList<ActionPlayerCoordinates>(depth);
        while (iterator.hasNext()) {
            apcs.add(iterator.next());
        }
        var dead = map.add(apcs.stream()
            .flatMap(apc -> apc.getCoordinates().stream())
            .filter(this::isOnBoard)
            .map(Coordinates::getTuple));

        for (var apc : apcs) {
            var player = apc.getPlayer();
            if (player.isActive() && !dead.contains(player.getId())) {
                players.put(player.getId(), player);
            } else {
                weight++;
            }
        }
    }

    public boolean isJumpTurn() {
        return turn % 6 == 0;
    }

    public Player getPlayer(int id) {
        return players.get(id);
    }

    public Player getOurPlayer() {
        return getPlayer(us);
    }

    public boolean isFree(int x, int y) {
        return map.isFree(x, y) && isOnBoard(x, y);
    }

    public void setCells(int[][] cells) {
        this.map = new MapCoordinateBag(cells, turn);
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
