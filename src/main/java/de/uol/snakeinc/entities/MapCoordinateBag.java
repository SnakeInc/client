package de.uol.snakeinc.entities;

import de.uol.snakeinc.possibleMoves.CombinationTree;
import de.uol.snakeinc.possibleMoves.IntSet;
import de.uol.snakeinc.possibleMoves.PlayerMap;
import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;

public class MapCoordinateBag {

    private MapCoordinateBag parent;
    private HashMap<Coordinates.Tuple, Coordinates.Tuple> map;
    private int[] xs;
    private int[] ys;
    @Getter
    private static int tru = 0;
    @Getter
    private static int fals = 0;

    public MapCoordinateBag(int[][] map, int turn) {
        this.map = new HashMap<Coordinates.Tuple, Coordinates.Tuple>(map.length * map.length / 2); //assumes map is halfway full
        this.xs = new int[map.length]; // initialized with zero
        this.ys = new int[map[0].length];
        for (int xdex = 0; xdex < map.length; xdex++) {
            for (int ydex = 0; ydex < map[0].length; ydex++) {
                if (map[xdex][ydex] != 0) {
                    var tuple = new Coordinates(xdex, ydex, map[xdex][ydex], turn).getTuple();
                    this.map.put(tuple, tuple);
                    xs[xdex]++;
                    ys[ydex]++;
                }
            }
        }
        this.parent = null;
    }

    public MapCoordinateBag(MapCoordinateBag parent) {
        this.parent = parent;
    }

    public MapCoordinateBag(MapCoordinateBag parent, HashMap map, int[] xs, int[] ys) {
        this.parent = parent;
        this.map = map;
        this.xs = xs;
        this.ys = ys;
    }

    public MapCoordinateBag(Stream<Coordinates.Tuple> start, int x, int y) {
        this.map = new HashMap<>();
        this.parent = null;
        this.xs = new int[x];
        this.ys = new int[y];

        start.forEach(t -> {
            map.put(t, t);
            xs[t.getX()]++;
            ys[t.getY()]++;
        });
    }

    public MapCoordinateBag addInternalAll(IntSet dead, CombinationTree.CombinationIterator iterator, PlayerMap players) {
        HashMap<Coordinates.Tuple, Coordinates.Tuple> current = new HashMap<>(((players.length - 1) * 40) / 30);
        int[] ys = this.ys.clone();
        int[] xs = this.xs.clone();
        while (iterator.hasNext()) {
            var apc = iterator.next();
            var player = apc.getPlayer();
            if (!player.isActive()) {
                dead.add(player.getId());
            }
            var coordinates = apc.getCoordinates();
            for (int i = 0; i < coordinates.size(); i++) {
                var coordinate = coordinates.get(i);
                if (this.contains(coordinate)) {
                    dead.add(coordinate.getPlayer());
                } else {
                    if (current.containsKey(coordinate)) {
                        int dying = current.get(coordinate).getPlayer();
                        if (dying != -1) {
                            dead.add(dying);
                            players.remove(dying);
                            //current.remove(coordinate);
                            var newTuple = coordinate.getCoordinates().cross().getTuple();
                            current.put(newTuple, newTuple);
                        }
                        dead.add(coordinate.getPlayer());
                    } else {
                        current.put(coordinate, coordinate);
                        ys[coordinate.getY()]++;
                        xs[coordinate.getX()]++;
                    }
                }
            }
            if (!dead.contains(player.getId())) {
                players.put(player);
            }
        }
        return new MapCoordinateBag(this, current, xs, ys);
    }

    public void addInternal(IntSet dead, Set<Coordinates.Tuple> curr, Coordinates.Tuple coord) {
        if (parent.contains(coord)) {
            dead.add(coord.getPlayer());
        } else {
            if (curr.contains(coord)) {
                int dying = curr.stream().filter(coord::equals).findAny().get().getPlayer(); // ouch
                if (dying != -1) {
                    dead.add(dying);
                    curr.remove(coord);
                    curr.add(coord.getCoordinates().cross().getTuple());
                }
                dead.add(coord.getPlayer());
            } else {
                curr.add(coord);
            }
        }
    }

    //public boolean isFree(int x, int y) {
    //    return map.find(tuple -> tuple.equals(x, y)).isEmpty();
    //}

    public boolean test(Collection<Coordinates> coordinates) {
        return coordinates.stream()
            .map(Coordinates::getTuple)
            .anyMatch(this::contains);
    }

    public boolean contains(Coordinates.Tuple tuple) {
        var res = this.xs[tuple.getX()] > 0 && this.ys[tuple.getY()] > 0
            && ((map.containsKey(tuple)) || (parent != null && parent.contains(tuple)));
        if (res) {
            tru++;
        } else {
            fals++;
        }
        return res;
    }
}
