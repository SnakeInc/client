package de.uol.snakeinc.entities;

import de.uol.snakeinc.possibleMoves.CombinationTree;
import de.uol.snakeinc.possibleMoves.IntSet;
import de.uol.snakeinc.possibleMoves.PlayerMap;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;

public class MapCoordinateBag {

    private MapCoordinateBag parent;
    private HashMap<Coordinates.Tuple, Coordinates.Tuple> map;

    public MapCoordinateBag(int[][] map, int turn) {
        this.map = new HashMap<Coordinates.Tuple, Coordinates.Tuple>(map.length * map.length / 2); //assumes map is halfway full
        for (int xdex = 0; xdex < map.length; xdex++) {
            for (int ydex = 0; ydex < map[0].length; ydex++) {
                if (map[xdex][ydex] != 0) {
                    var tuple = new Coordinates(xdex, ydex, map[xdex][ydex], turn).getTuple();
                    this.map.put(tuple, tuple);
                }
            }
        }
        this.parent = null;
    }

    public MapCoordinateBag(MapCoordinateBag parent) {
        this.parent = parent;
    }

    public MapCoordinateBag(MapCoordinateBag parent, HashMap map) {
        this.parent = parent;
        this.map = map;
    }

    public MapCoordinateBag(Stream<Coordinates.Tuple> start) {
        this.map = new HashMap<>();
        start.forEach(t -> map.put(t, t));
    }

    public MapCoordinateBag addInternalAll(IntSet dead, CombinationTree.CombinationIterator iterator, PlayerMap players) {
        HashMap<Coordinates.Tuple, Coordinates.Tuple> current = new HashMap<>((players.length - 1) * 10);
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
                    }
                }
            }
            if (!dead.contains(player.getId())) {
                players.put(player);
            }
        }
        return new MapCoordinateBag(this, current);
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
        return map.containsKey(tuple) || (parent != null && parent.contains(tuple));
    }
}
