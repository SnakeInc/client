package de.uol.snakeinc.entities;

import de.uol.snakeinc.possibleMoves.CombinationTree;
import de.uol.snakeinc.possibleMoves.IntSet;
import de.uol.snakeinc.possibleMoves.PlayerMap;

import java.util.Collection;
import java.util.stream.Stream;

public class MapCoordinateBag {

    private MapCoordinateBag parent;
    //private HashMap<Coordinates.Tuple, Coordinates.Tuple> map;
    private int[][] map;
    private boolean[] xs;
    private boolean[] localys;
    private boolean[] ys;

    public MapCoordinateBag(int[][] map, int turn) {
        this.map = new int[map.length][];
        this.ys = new boolean[map[0].length];
        this.localys = new boolean[map[0].length];
        this.xs = new boolean[map.length];
        for (int xdex = 0; xdex < map.length; xdex++) {
            for (int ydex = 0; ydex < map[0].length; ydex++) {
                if (map[xdex][ydex] != 0) {
                    if (this.map[xdex] == null) {
                        this.map[xdex] = new int[map[0].length];
                    }
                    this.map[xdex][ydex] = map[xdex][ydex];
                    localys[ydex] = true;
                    ys[ydex] = true;
                    xs[xdex] = true;
                }
            }
        }
        this.parent = null;
    }

    public MapCoordinateBag(MapCoordinateBag parent) {
        this.parent = parent;
    }

    public MapCoordinateBag(MapCoordinateBag parent, int[][] map, boolean[] xs, boolean[] ys, boolean[] localys) {
        this.parent = parent;
        this.map = map;
        this.xs = xs;
        this.ys = ys;
        this.localys = localys;
    }

    public MapCoordinateBag(Stream<Coordinates.Tuple> start, int x, int y) {
        this.map = new int[x][];
        this.parent = null;
        this.xs = new boolean[x];
        this.ys = new boolean[y];
        this.localys = new boolean[y];

        start.forEach(t -> {
            if (map[t.getX()] == null) {
                map[t.getX()] = new int[y];
            }
            map[t.getX()][t.getY()] = t.getPlayer();
            xs[t.getX()] = true;
            ys[t.getY()] = true;
            localys[t.getY()] = true;
        });
    }

    public MapCoordinateBag addInternalAll(IntSet dead, CombinationTree.CombinationIterator iterator, PlayerMap players) {
        int[][] current = new int[this.map.length][];
        boolean[] ys = this.ys.clone();
        boolean[] xs = this.xs.clone();
        boolean[] localys = new boolean[this.localys.length];
        while (iterator.hasNext()) {
            var apc = iterator.next();
            var player = apc.getPlayer();
            if (!player.isActive()) {
                dead.add(player.getId());
            }
            var coordinates = apc.getCoordinates();
            for (int i = 0; i < coordinates.size(); i++) {
                var coordinate = coordinates.get(i);
                var slice = current[coordinate.getX()];
                if (slice != null) {
                    if (slice[coordinate.getY()] != 0) {
                        int dying = slice[coordinate.getY()];
                        if (dying != -1) {
                            dead.add(dying);
                            players.remove(dying);
                            slice[coordinate.getY()] = -1;
                        }
                        dead.add(coordinate.getPlayer());
                    } else {
                        slice[coordinate.getY()] = coordinate.getPlayer();
                        ys[coordinate.getY()] = true;
                        xs[coordinate.getX()] = true;
                    }
                } else {
                    current[coordinate.getX()] = new int[ys.length];
                    current[coordinate.getX()][coordinate.getY()] = coordinate.getPlayer();
                    xs[coordinate.getX()] = true;
                    ys[coordinate.getY()] = true;
                    localys[coordinate.getY()] = true;

                }
            }
            if (!dead.contains(player.getId())) {
                players.put(player);
            }
        }
        return new MapCoordinateBag(this, current, xs, ys, localys);
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
        return (ys[tuple.getY()] && xs[tuple.getX()])
            && ((localys[tuple.getY()] && map[tuple.getX()] != null
            && map[tuple.getX()][tuple.getY()] == tuple.getPlayer())
            || (parent != null && parent.contains(tuple)));
    }
}
