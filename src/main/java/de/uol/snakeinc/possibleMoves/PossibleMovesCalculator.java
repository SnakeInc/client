package de.uol.snakeinc.possibleMoves;

import de.uol.snakeinc.entities.Board;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.Player;
import lombok.CustomLog;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@CustomLog
public class PossibleMovesCalculator {

    private Board base;
    private LocalBoardPool pool;

    public PossibleMovesCalculator(Board base) {
        this.base = base;
        this.pool = new LocalBoardPool(3, base.getUs(), base.getHeight(), base.getWidth(), base.getPlayers().length, 3);
    }

    public Stream<Board> calculateBoards(int depth) {
        return calculateBoards(base).flatMap(this::calculateBoards).flatMap(this::calculateBoards);
    }

    public void release(Board board) {
        pool.release(board);
    }

    private Stream<Board> calculateBoards(Board from) {
        var combinations = new CombinationTree();
        var players = from.getPlayers();
        for (int i = 1; i < players.length; i++) {
            if (players[i] != null) {
                combinations.add(players[i].getPossibleMoves(from));
                // players[i] = null;
            }
        }
        var map = from.getMap();
        //from.mapDispose();
        //pool.release(from);
        return combinations.getCombinationStream()
            .map(iterator -> new Board(from, iterator));
    }

    private Stream<Board> calculateBoardsParallel(Board from) {

        var combinations = new CombinationTree();
        var players = from.getPlayers();
        for (int i = 1; i < players.length; i++) {
            if (players[i] != null) {
                combinations.add(players[i].getPossibleMoves(from));
                //players[i] = null;
            }
        }
        var map = from.getMap();
        //from.mapDispose();
        //pool.release(from);
        return combinations.getCombinationStream().parallel()
            .map(iterator -> new Board(from, iterator));
    }

    public static Stats stats2(Board from) {
        var stats = new Stats(0, 0, 0, 0, 0, 0);
        var combinations = new CombinationTree();
        var players = from.getPlayers();
        for (int i = 1; i < players.length; i++) {
            if (players[i] != null) {
                combinations.add(players[i].getPossibleMoves(from));
                players[i] = null;
            }
        }
        var map = from.getMap();
        for (var node : combinations.getRawCombinations()) {
            var it = node.toIterator();
            from.recycle(map, 1, it);
            stats1(from, stats);
        }
        return stats;
    }

    public static Stats stats2inlined(Board from) {
        int overallCounted = 0;
        int ourDeaths = 0;
        int otherDeaths = 0;
        int noDeaths = 0;
        var combinations = new CombinationTree();
        var players = from.getPlayers();
        for (int i = 1; i < players.length; i++) {
            if (players[i] != null) {
                combinations.add(players[i].getPossibleMoves(from));
                players[i] = null;
            }
        }
        var map = from.getMap();
        for (var node : combinations.getRawCombinations()) {
            var it = node.toIterator();
            from.recycle(map, 1, it);
            var combinations1 = new CombinationTree();
            var players1 = from.getPlayers();
            for (int i = 1; i < players1.length; i++) {
                if (players1[i] != null) {
                    combinations1.add(players1[i].getPossibleMoves(from));
                    players1[i] = null;
                }
            }
            var map1 = from.getMap();
            for (var node1 : combinations1.getRawCombinations()) {
                var it1 = node1.toIterator();
                from.recycle(map1, 1, it1);
                for (int i = 1; i < from.getPlayers().length; i++) {
                    Player player = from.getPlayer(i);
                    if (player == null) {
                        if (i == from.getUs()) {
                            ourDeaths++;
                        } else {
                            otherDeaths++;
                        }
                    } else {
                        from.getPlayers()[i] = null;
                    }
                }
                if (otherDeaths == 0 && ourDeaths == 0) {
                    noDeaths++;
                }
                overallCounted++;
            }
        }
        return new Stats(overallCounted, ourDeaths, otherDeaths, noDeaths, 0, 0);
    }

    public static void stats1(Board from, Stats stats) {
        var combinations = new CombinationTree();
        var players = from.getPlayers();
        for (int i = 1; i < players.length; i++) {
            if (players[i] != null) {
                combinations.add(players[i].getPossibleMoves(from));
                players[i] = null;
            }
        }
        var map = from.getMap();
        for (var node : combinations.getRawCombinations()) {
            var it = node.toIterator();
            from.recycle(map, 1, it);
            stats0(from, stats);
        }
    }

    public static void stats0(Board board, Stats stats) {
        //int overallCounted = 0;
        int ourDeaths = 0;
        int otherDeaths = 0;
        int noDeaths = 0;
        for (int i = 1; i < board.getPlayers().length; i++) {
            Player player = board.getPlayer(i);
            if (player == null) {
                if (i == board.getUs()) {
                    ourDeaths++;
                } else {
                    otherDeaths++;
                }
            } else {
                board.getPlayers()[i] = null;
            }
        }
        if (otherDeaths == 0 && ourDeaths == 0) {
            noDeaths = 1;
        }
        stats.addUnary(1, ourDeaths, otherDeaths, noDeaths);
    }

    public static void main(String[] Args) {
        var players = new Player[] {null,
            new Player(1, 10, 10, Direction.UP, 5, true, "1"),
            new Player(2, 10, 30, Direction.UP, 5, true, "2"),
            new Player(3, 30, 10, Direction.UP, 5, true, "3"),
            new Player(4, 30, 30, Direction.UP, 5, true, "4"),
        };

        var board = new Board(40, 40, players, 2);
        var calc = new PossibleMovesCalculator(board);

        AtomicInteger i = new AtomicInteger(0);
        //var start = System.currentTimeMillis();
        //var count = calc.calculateBoards(2).count();
        //var end = System.currentTimeMillis();
        //log.debug("Time elapsed: " + (end - start) + "ms");
        //log.debug(count);
        var start = System.currentTimeMillis();
        var stats1 = calc.calculateBoardsParallel(board)
            .flatMap(calc::calculateBoards)
            .flatMap(calc::calculateBoards)
            .map(Stats::new)
            .reduce(new Stats(0, 0, 0, 0, 0, 0), Stats::add);
        var end = System.currentTimeMillis();
        log.debug("Time elapsed: " + (end - start) + "ms");
        log.debug(stats1);
        start = System.currentTimeMillis();
        var stats2 = calc.calculateBoardsParallel(calc.base)
            .map(PossibleMovesCalculator::stats2)
            .reduce(new Stats(0, 0, 0, 0, 0, 0), Stats::add);
        end = System.currentTimeMillis();
        log.debug("Time elapsed: " + (end - start) + "ms");
        log.debug(stats2);
        start = System.currentTimeMillis();
        var stats3 = calc.calculateBoardsParallel(calc.base)
            .map(PossibleMovesCalculator::stats2inlined)
            .reduce(new Stats(0, 0, 0, 0, 0, 0), Stats::add);
        end = System.currentTimeMillis();
        log.debug("Time elapsed: " + (end - start) + "ms");
        log.debug(stats3);
    }
}
