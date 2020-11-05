package de.uol.snakeinc.entities;

import io.vavr.collection.HashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

public class MapCoordinateBag {

    private HashSet<Coordinates.Tuple> map;

    public MapCoordinateBag(int[][] map, int turn) {
        var list = new ArrayList<Coordinates.Tuple>(map.length * map.length / 2); //assumes map is halfway full
        for (int xdex = 0; xdex < map.length; xdex++) {
            for (int ydex = 0; ydex < map[0].length; ydex++) {
                if (map[xdex][ydex] != 0) {
                    list.add(new Coordinates(xdex, ydex, map[xdex][ydex], turn).getTuple());
                }
            }
        }
        this.map = HashSet.ofAll(list);
    }

    public MapCoordinateBag(MapCoordinateBag parent) {
        this.map = parent.map;
    }

    public MapCoordinateBag(Stream<Coordinates.Tuple> start) {
        this.map = HashSet.ofAll(start);
    }

    public Set<Integer> add(Iterable<Coordinates.Tuple> toadd) {
        var dead = new TreeSet<Integer>();
        var curr = new TreeSet<Coordinates.Tuple>();
        for (var coord : toadd) {
            addInternal(dead, curr, coord);
        }
        this.map = this.map.addAll(curr);
        return dead;
    }

    public Set<Integer> add(Stream<Coordinates.Tuple> toadd) {
        var dead = new TreeSet<Integer>();
        var curr = new TreeSet<Coordinates.Tuple>();
        toadd.forEach(coord -> addInternal(dead, curr, coord));
        this.map = this.map.addAll(curr);
        return dead;
    }

    private void addInternal(TreeSet<Integer> dead, TreeSet<Coordinates.Tuple> curr, Coordinates.Tuple coord) {
        if (map.contains(coord)) {
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

    public boolean isFree(int x, int y) {
        return map.find(tuple -> tuple.equals(x, y)).isEmpty();
    }

    public boolean test(Collection<Coordinates> coordinates) {
        return coordinates.stream()
            .map(Coordinates::getTuple)
            .anyMatch(map::contains);
    }
}
