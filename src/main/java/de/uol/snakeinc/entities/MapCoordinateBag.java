package de.uol.snakeinc.entities;

import de.uol.snakeinc.possibleMoves.CombinationTree;
import de.uol.snakeinc.possibleMoves.PlayerMap;
import de.uol.snakeinc.possibleMoves.possibleMovesCalculation2;
import de.uol.snakeinc.util.CoordinateMap;

import java.util.Collection;
import java.util.stream.Stream;

public class MapCoordinateBag {

    private MapCoordinateBag parent;
    private CoordinateMap map;

    public MapCoordinateBag(int[][] map) {
        this.map = new CoordinateMap(map.length, map[0].length);
        for (int xdex = 0; xdex < map.length; xdex++) {
            for (int ydex = 0; ydex < map[0].length; ydex++) {
                if (map[xdex][ydex] != 0) {
                    this.map.put(xdex, ydex, map[xdex][ydex]);
                }
            }
        }
        this.parent = null;
    }

    public MapCoordinateBag(MapCoordinateBag parent) {
        this.parent = parent;
    }

    public MapCoordinateBag(MapCoordinateBag parent, CoordinateMap map) {
        this.parent = parent;
        this.map = map;
    }

    public MapCoordinateBag(Stream<Coordinates> start, int x, int y) {
        this.map = new CoordinateMap(x, y);
        this.parent = null;

        start.forEach(t -> map.put(t.getX(), t.getY(), t.getPlayer()));
    }

    public MapCoordinateBag addInternalAll(CombinationTree.CombinationNode node, PlayerMap players, int[] stats) {
        var current = node.getCoordinates();
        var dead = node.getDead();
        var iterator = node.toIterator();
        while (iterator.hasNext()) {
            var player = iterator.next().getPlayer();
            if (!dead.contains(player.getId())) {
                players.put(player);
            }
        }
        possibleMovesCalculation2.updateStats(stats, dead);
        return new MapCoordinateBag(this, current);
    }

    //public boolean isFree(int x, int y) {
    //    return map.find(tuple -> tuple.equals(x, y)).isEmpty();
    //}

    public boolean test(Collection<Coordinates> coordinates) {
        return coordinates.stream()
            .anyMatch(this::contains);
    }

    public boolean contains(Coordinates coordinates) {
        return this.map.contains(coordinates) || (parent != null && parent.contains(coordinates));
    }
}
