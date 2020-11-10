package de.uol.snakeinc.entities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.uol.snakeinc.possibleMoves.CombinationTree;
import de.uol.snakeinc.possibleMoves.IntSet;
import io.vavr.Tuple2;
import lombok.CustomLog;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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

    public Board(int numOfPlayers, int with, int hight, int us) {
        this.width = with;
        this.height = hight;
        this.players = new Player[numOfPlayers];
        this.us = us;
    }

    public Board(Board board, CombinationTree.CombinationIterator iterator) {
        this.width = board.width;
        this.height = board.height;
        this.turn = board.turn + 1;
        this.players = new Player[board.players.length];
        this.weight = board.weight;

        this.map = new MapCoordinateBag(board.map);
        var dead = IntSet.ofSize(players.length);
        var current = new HashSet<Coordinates.Tuple>();
        while (iterator.hasNext()) {
            var apc = iterator.next();
            //for (var coordinates : apc.getCoordinates()) {               //        -\
            //    map.addInternal(dead, current, coordinates);             //         /
            //}                                                            //         \
            var coordinates = apc.getCoordinates(); //          > not sure what is faster
            for (int i = 0; i < coordinates.size(); i++) {                 //         /
                map.addInternal(dead, current, coordinates.get(i));        //         \
            }                                                              //        -/
            if (apc.getPlayer().isActive()) {
                players[apc.getPlayer().getId()] = apc.getPlayer();
            }
        }
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null && dead.contains(i)) {
                players[i] = null;
            }
        }
    }

    public Board recycle(MapCoordinateBag map, int weight, CombinationTree.CombinationIterator iterator) {
        this.weight = weight;
        this.map = map;
        var dead = IntSet.ofSize(players.length);
        var current = new HashSet<Coordinates.Tuple>();
        while (iterator.hasNext()) {
            var apc = iterator.next();
            //for (var coordinates : apc.getCoordinates()) {               //
            //    map.addInternal(dead, current, coordinates);             //
            //}                                                            //
            var coordinates = apc.getCoordinates(); //
            var size = coordinates.size();                             //          > not sure what is faster
            for (int i = 0; i < size; i++) {                 //
                map.addInternal(dead, current, coordinates.get(i));        //
            }                                                              //
            if (apc.getPlayer().isActive()) {
                players[apc.getPlayer().getId()] = apc.getPlayer();
            }
        }
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null && dead.contains(i)) {
                players[i] = null;
            }
        }
        return this;
    }

    public void dispose() {
        this.map = null;
        Arrays.fill(players, null);
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
