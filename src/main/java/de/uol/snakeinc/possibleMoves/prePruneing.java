package de.uol.snakeinc.possibleMoves;

import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Board;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import de.uol.snakeinc.util.CoordinateMap;
import lombok.CustomLog;

import java.util.ArrayList;

@CustomLog
public class prePruneing {

    private static Action calculateMoves(Board from) {
        Action action = Action.CHANGE_NOTHING;
        int width = from.getWidth();
        int height = from.getHeight();
        PlayerMap players = new PlayerMap(from.getPlayers());
        int us = from.getUs();
        CoordinateMap map = new CoordinateMap(from.getCells());
        int depth = 4;

        Player we = players.get(us);
        players.remove(us);

        var ourMoves = we.getPossibleMoves(height, width, map);
        int best = 0;

        outer:
        for (var apc : ourMoves) {
            int res = 0;
            Player newWe = apc.player;
            var newPlayers = new PlayerMap(players.getPlayers().clone());
            CoordinateMap newMap = new CoordinateMap(map);
            var coordinates = apc.getCoordinates();

            for (var coord : coordinates) {
                newMap.put(coord.getX(), coord.getY(), coord.getPlayer());
            }

            if (players.size() <= 0) {
                throw new IllegalStateException("should be more than one player");
            }

            ArrayList<ArrayList<ActionPlayerCoordinates>> playerMoves = new ArrayList<ArrayList<ActionPlayerCoordinates>>(players.size());
            for (var player : players) {
                if (player == null) {
                    continue;
                }
                var apcs = player.getPossibleMoves(height, width, newMap);
                if (apcs.isEmpty()) {
                    newPlayers.remove(player.getId());
                    continue;
                }
                playerMoves.add(apcs);
            }

            res = iterateThroughCombinations(playerMoves, newWe, newMap, newPlayers, height, width, depth);

            log.debug(action.toString() + " " + res);
            if (res > best) {
                action = apc.action;
            }
        }
        return action;
    }

    private static int calculateMoves(int width, int height, PlayerMap players, int us, CoordinateMap map, int depth) {

        Player we = players.get(us);
        players.remove(us);

        var ourMoves = we.getPossibleMoves(height, width, map);
        int res = 0;

        for (var apc : ourMoves) {
            Player newWe = apc.player;
            var newPlayers = new PlayerMap(players.getPlayers().clone());
            CoordinateMap newMap = new CoordinateMap(map);
            var coordinates = apc.getCoordinates();

            for (var coord : coordinates) {
                newMap.put(coord.getX(), coord.getY(), coord.getPlayer());
            }

            if (players.size() <= 0) {
                return 625;
            }

            ArrayList<ArrayList<ActionPlayerCoordinates>> playerMoves = new ArrayList<ArrayList<ActionPlayerCoordinates>>(players.size());
            for (var player : players) {
                if (player == null) {
                    continue;
                }
                var apcs = player.getPossibleMoves(height, width, newMap);
                if (apcs.isEmpty()) {
                    newPlayers.remove(player.getId());
                    continue;
                }
                playerMoves.add(apcs);
            }
            res += iterateThroughCombinations(playerMoves, newWe, newMap, newPlayers, height, width, depth);
        }
        return res;
    }

    /**
     * main.
     * @param Args args
     */
    public static void main(String[] Args) {
        var players = new Player[] {null,
            new Player(1, 10, 10, Direction.UP, 2, true, "1"),
            new Player(2, 10, 30, Direction.RIGHT, 2, true, "2"),
            new Player(3, 30, 10, Direction.DOWN, 2, true, "3"),
            new Player(4, 30, 30, Direction.LEFT, 2, true, "4")//,
            //new Player(5, 10, 20, Direction.UP, 5, true, "5"),
            //new Player(6, 30, 20, Direction.UP, 5, true, "6")
        };

        var board = new Board(40, 40, players, 2);

        var start = System.currentTimeMillis();
        var stats1 = calculateMoves(board);
        var end = System.currentTimeMillis();
        log.debug("Time elapsed: " + (end - start) + "ms");
        log.debug(stats1);

    }

    private static int iterateThroughCombinations(ArrayList<ArrayList<ActionPlayerCoordinates>> moves, Player us, CoordinateMap map, PlayerMap players, int height, int width, int depth) {
        if (moves.size() == 0) {
            if (depth == 0) {
                return 1;
            }
            players.put(us);
            return calculateMoves(width, height, players, us.getId(), map, depth - 1);
        }

        var lastMoves = moves.get(moves.size() - 1);
        moves.remove(moves.size() - 1);

        int res = 0;

        for (var apc : lastMoves) {
            var newMap = new CoordinateMap(map);
            var newPlayers = new PlayerMap(players.getPlayers().clone());
            var coordinates = apc.getCoordinates();
            for (var coordinate : coordinates) {
                int enemy = map.get(coordinate);
                if (enemy != 0) {
                    newPlayers.remove(coordinate.getPlayer());
                    if (enemy > 0) {
                        newPlayers.remove(enemy);
                    }
                } else {
                    newMap.put(coordinate.getX(), coordinate.getY(), coordinate.getPlayer());
                }
            }

            res += iterateThroughCombinations(moves, us, newMap, newPlayers, height, width, depth);
        }
        return res;
    }
}
