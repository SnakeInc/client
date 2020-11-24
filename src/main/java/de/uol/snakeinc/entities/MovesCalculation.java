package de.uol.snakeinc.entities;

import de.uol.snakeinc.possibleMoves.CombinationTree;
import de.uol.snakeinc.possibleMoves.PlayerMap;
import lombok.ToString;

import java.util.EnumMap;

public class MovesCalculation {

    static Action getAction(Board from) {
        int length = from.getPlayers().length;
        PlayerMap originalPlayers = new PlayerMap(from.getPlayers());
        PlayerMap players = new PlayerMap(length);
        int depth;
        switch (length) {
            case 5:
            case 4:
                depth = 3;
                break;
            case 3:
                depth = 4;
                break;
            case 2:
                depth = 6;
                break;
            default:
                depth = 2;
                break;
        }
        var res = new EnumMap<Action, Stats>(Action.class);
        for (var action : Action.values()) {
            res.put(action, new Stats(originalPlayers.size(), depth));
        }
        int us = from.getUs();
        int height = from.getHeight();
        int width = from.getWidth();

        CombinationTree combinationTree1 = new CombinationTree();
        players.addToCombinationTree(combinationTree1, height, width, from.getMap());
        var combs1 = combinationTree1.getRawCombinations();
        for (int ind1 = 0; ind1 < combs1.size(); ind1++) {
            var combination1 = combs1.get(ind1);
            Action baseAction = Action.CHANGE_NOTHING;
            var iter = combination1.toIterator();
            while (iter.hasNext()) {
                var next = iter.next();
                if (next.getPlayer().getId() == us) {
                    baseAction = next.getAction();
                }
            }
            var resres = res.get(baseAction);
            MapCoordinateBag map1 = from.getMap().addInternalAll(combination1, players, resres.deaths[0]);
            resres.overall[0]++;

            if (depth == 1 || !players.containsKey(us)) {
                players.clear();
                continue;
            }

            CombinationTree combinationTree2 = new CombinationTree();
            players.addToCombinationTree(combinationTree2, height, width, map1);
            var combs2 = combinationTree2.getRawCombinations();
            for (int ind2 = 0; ind2 < combs2.size(); ind2++) {
                var combination2 = combs2.get(ind2);
                MapCoordinateBag map2 = from.getMap().addInternalAll(combination2, players, resres.deaths[1]);
                resres.overall[1]++;

                if (depth == 2 || !players.containsKey(us)) {
                    players.clear();
                    continue;
                }

                CombinationTree combinationTree3 = new CombinationTree();
                players.addToCombinationTree(combinationTree3, height, width, map2);
                var combs3 = combinationTree3.getRawCombinations();
                for (int ind3 = 0; ind3 < combs3.size(); ind3++) {
                    var combination3 = combs3.get(ind3);
                    MapCoordinateBag map3 = from.getMap().addInternalAll(combination3, players, resres.deaths[2]);
                    resres.overall[2]++;

                    if (depth == 3 || !players.containsKey(us)) {
                        players.clear();
                        continue;
                    }

                    CombinationTree combinationTree4 = new CombinationTree();
                    players.addToCombinationTree(combinationTree4, height, width, map3);
                    var combs4 = combinationTree4.getRawCombinations();
                    for (int ind4 = 0; ind4 < combs4.size(); ind4++) {
                        var combination4 = combs4.get(ind4);
                        MapCoordinateBag map4 = from.getMap().addInternalAll(combination4, players, resres.deaths[3]);
                        resres.overall[3]++;

                        if (depth == 4 || !players.containsKey(us)) {
                            players.clear();
                            continue;
                        }

                        CombinationTree combinationTree5 = new CombinationTree();
                        players.addToCombinationTree(combinationTree5, height, width, map4);
                        var combs5 = combinationTree5.getRawCombinations();
                        for (int ind5 = 0; ind5 < combs5.size(); ind5++) {
                            var combination5 = combs5.get(ind5);
                            MapCoordinateBag map5 = from.getMap().addInternalAll(combination5, players, resres.deaths[4]);
                            resres.overall[4]++;

                            if (depth == 5 || !players.containsKey(us)) {
                                players.clear();
                                continue;
                            }

                            CombinationTree combinationTree6 = new CombinationTree();
                            players.addToCombinationTree(combinationTree6, height, width, map5);
                            var combs6 = combinationTree6.getRawCombinations();
                            for (int ind6 = 0; ind6 < combs6.size(); ind6++) {
                                var combination6 = combs6.get(ind6);
                                MapCoordinateBag map6 = from.getMap().addInternalAll(combination6, players, resres.deaths[5]);
                                resres.overall[5]++;

                                if (depth == 6 || !players.containsKey(us)) {
                                    players.clear();
                                    continue;
                                }
                                players.clear();
                            }
                        }
                    }
                }
            }
        }
        int bestVal = 0;
        Action bestAct = Action.CHANGE_NOTHING;
        for (var action : Action.values()) {
            if (bestVal < res.get(action).end(us)) {
                bestAct = action;
            }
        }
        return bestAct;
    }

    @ToString
    public static class Stats {

        int[][] deaths;
        int[] overall;

        public Stats(int players, int depth) {
            deaths = new int[depth][players];
            overall = new int[depth];
        }

        public int end(int who) {
            return deaths[deaths.length - 1][who];
        }
    }
}
