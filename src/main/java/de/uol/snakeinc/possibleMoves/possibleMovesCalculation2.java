package de.uol.snakeinc.possibleMoves;

import de.uol.snakeinc.entities.Board;
import de.uol.snakeinc.entities.Direction;
import de.uol.snakeinc.entities.MapCoordinateBag;
import de.uol.snakeinc.entities.Player;
import lombok.CustomLog;
import lombok.ToString;

@CustomLog
public class possibleMovesCalculation2 {

    private static Stats3 linearStats3withCulling(Board from) {
        int length = from.getPlayers().length;
        Stats3 res = new Stats3(length);
        PlayerMap players = new PlayerMap(from.getPlayers());
        PlayerMap players1 = new PlayerMap(length);
        IntSet dead = IntSet.ofSize(length);
        int us = from.getUs();
        int height = from.getHeight();
        int width = from.getWidth();
        int turn = from.getTurn();

        CombinationTree combinationTree1 = new CombinationTree();
        players.addToCombinationTree(combinationTree1, height, width, turn, from.getMap());
        var combs1 = combinationTree1.getRawCombinations();
        for (var combination1 : combs1) {
            MapCoordinateBag map1 = from.getMap().addInternalAll(dead, combination1.toIterator(), players1);
            updateStats(res.deaths1, dead);
            res.overall1++;

            if (dead.contains(us)) {
                players1.clear();
                dead.clear();
                continue;
            }
            dead.clear();

            CombinationTree combinationTree2 = new CombinationTree();
            players1.addToCombinationTree(combinationTree2, height, width, turn + 1, map1);
            players1.clear();
            var combs2 = combinationTree2.getRawCombinations();
            for (var combination2 : combs2) {
                MapCoordinateBag map2 = map1.addInternalAll(dead, combination2.toIterator(), players1);
                updateStats(res.deaths2, dead);
                res.overall2++;

                if (dead.contains(us)) {
                    players1.clear();
                    dead.clear();
                    continue;
                }
                dead.clear();

                CombinationTree combinationTree3 = new CombinationTree();
                players1.addToCombinationTree(combinationTree3, height, width, turn + 2, map2);
                players1.clear();
                var combs3 = combinationTree3.getRawCombinations();
                for (var combination3 : combs3) {
                    MapCoordinateBag map3 = map2.addInternalAll(dead, combination3.toIterator(), players1);
                    updateStats(res.deaths3, dead);
                    res.overall3++;

                    if (dead.contains(us)) {
                        players1.clear();
                        dead.clear();
                        continue;
                    }
                    dead.clear();

                    CombinationTree combinationTree4 = new CombinationTree();
                    players1.addToCombinationTree(combinationTree4, height, width, turn + 3, map3);
                    players1.clear();
                    var combs4 = combinationTree4.getRawCombinations();
                    for (var combination4 : combs4) {
                        MapCoordinateBag map4 = map3.addInternalAll(dead, combination4.toIterator(), players1);
                        updateStats(res.deaths4, dead);
                        res.overall4++;

                        players1.clear();
                        dead.clear();
                    }
                }
            }
        }
        return res;
    }

    private static void updateStats(int[] stats, IntSet dead) {
        int length = stats.length;
        boolean nobodyDied = true;
        int i = 1;
        for (; i < length; i++) {
            if (dead.contains(i)) {
                stats[i]++;
                nobodyDied = false;
                i++;
                break;
            }
        }
        for (; i < length; i++) {
            if (dead.contains(i)) {
                stats[i]++;
            }
        }
        if (nobodyDied) {
            stats[0]++;
        }
    }

    @ToString
    public static class Stats3 {

        public int[] deaths1;
        public int overall1;

        public int[] deaths2;
        public int overall2;

        public int[] deaths3;
        public int overall3;

        public int[] deaths4;
        public int overall4;

        public Stats3(int length) {
            deaths1 = new int[length];
            deaths2 = new int[length];
            deaths3 = new int[length];
            deaths4 = new int[length];
            overall1 = 0;
            overall2 = 0;
            overall3 = 0;
            overall4 = 0;
        }
    }

    public static void main(String[] Args) {
        var players = new Player[] {null,
            new Player(1, 10, 10, Direction.UP, 5, true, "1"),
            new Player(2, 10, 30, Direction.UP, 5, true, "2"),
            new Player(3, 30, 10, Direction.UP, 5, true, "3"),
            new Player(4, 30, 30, Direction.UP, 5, true, "4"),
        };

        var board = new Board(40, 40, players, 2);

        var start = System.currentTimeMillis();
        var stats1 = linearStats3withCulling(board);
        var end = System.currentTimeMillis();
        log.debug("Time elapsed: " + (end - start) + "ms");
        log.debug(stats1);

    }

}
