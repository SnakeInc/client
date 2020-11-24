package de.uol.snakeinc.entities;

import de.uol.snakeinc.possibleMoves.CombinationTree;
import de.uol.snakeinc.possibleMoves.PlayerMap;
import de.uol.snakeinc.util.CoordinateMap;
import lombok.Getter;

import java.util.Collection;
import java.util.stream.Stream;

public class MapCoordinateBag {

    @Getter
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
    }

    public MapCoordinateBag(MapCoordinateBag parent) {
        this.map = new CoordinateMap(parent.getMap());
    }

    public MapCoordinateBag(MapCoordinateBag parent, CoordinateMap map) {
        this.map = map;
    }

    public MapCoordinateBag(Stream<Coordinates> start, int x, int y) {
        this.map = new CoordinateMap(x, y);

        start.forEach(t -> map.put(t.getX(), t.getY(), t.getPlayer()));
    }

    public int[][] getCells() {
        return map.getCells();
    }

    /**
     * creates a new MapCoordinateBag where all Players executed the Moves they execute in the branch of the
     * CombinationTree that node represents. Also constructs a new PlayerMap with all players that survive this Turn,
     * and reports the stats of the Turn.
     * @param node    the node representing the player Moves
     * @param players the PlayerMap that contains the surviving Players
     * @param stats   the stats to be updated
     * @return a new MapCoordinate Bag
     */
    public MapCoordinateBag addInternalAll(CombinationTree.CombinationNode node, PlayerMap players, int[] stats) {
        var current = node.getCoordinates();
        var dead = node.getDead();
        var iterator = node.toIterator();
        while (iterator.hasNext()) {
            var player = iterator.next().getPlayer();
            if (!dead.contains(player.getId())) {
                players.put(player);
            } else {
                stats[player.getId()]++;
            }
        }
        return new MapCoordinateBag(this, current);
    }

    public boolean test(Collection<Coordinates> coordinates) {
        return coordinates.stream().anyMatch(this::contains);
    }

    public boolean contains(Coordinates coordinates) {
        return this.map.contains(coordinates);
    }
}
