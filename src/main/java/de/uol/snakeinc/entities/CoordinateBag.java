package de.uol.snakeinc.entities;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CoordinateBag {

    @Getter
    protected SortedSet<Coordinates> coordinates;

    public CoordinateBag() {
        //this.id = count.getAndIncrement();
        this.coordinates = TreeSet.empty();
    }

    public CoordinateBag(Collection<Coordinates> coordinates) {
        //this.id = count.getAndIncrement();
        this.coordinates = TreeSet.ofAll(coordinates);
    }

    public CoordinateBag(SortedSet<Coordinates> coordinates) {
        //this.id = count.getAndIncrement();
        this.coordinates = TreeSet.ofAll(coordinates);
    }

    public CoordinateBag(int x, int y, int speed, Direction dir, int turn, int playerId) {
        //this.id = count.getAndIncrement();
        var res = new ArrayList<Coordinates>(speed);
        switch (dir) {
            case DOWN:
                int limit = y - speed;
                while (y > limit) {
                    y--;
                    res.add(new Coordinates(x, y, turn, playerId));
                }
                break;
            case UP:
                limit = y + speed;
                while (y < limit) {
                    y++;
                    res.add(new Coordinates(x, y, turn, playerId));
                }
                break;
            case LEFT:
                limit = x - speed;
                while (x > limit) {
                    x--;
                    res.add(new Coordinates(x, y, turn, playerId));
                }
                break;
            case RIGHT:
                limit = x + speed;
                while (x < limit) {
                    x++;
                    res.add(new Coordinates(x, y, turn, playerId));
                }
                break;
        }
        this.coordinates = TreeSet.ofAll(res);
    }

    public CoordinateBag(int x, int y, int speed, Direction dir, int turn, int playerId, CoordinateBag bag) {
        //this.id = count.getAndIncrement();
        var res = new ArrayList<Coordinates>(speed);
        switch (dir) {
            case DOWN:
                int limit = y - speed;
                while (y > limit) {
                    y--;
                    res.add(new Coordinates(x, y, turn, playerId));
                }
                break;
            case UP:
                limit = y + speed;
                while (y < limit) {
                    y++;
                    res.add(new Coordinates(x, y, turn, playerId));
                }
                break;
            case LEFT:
                limit = x - speed;
                while (x > limit) {
                    x--;
                    res.add(new Coordinates(x, y, turn, playerId));
                }
                break;
            case RIGHT:
                limit = x + speed;
                while (x < limit) {
                    x++;
                    res.add(new Coordinates(x, y, turn, playerId));
                }
                break;
        }
        this.coordinates = bag.coordinates.addAll(res);
    }

    public CoordinateBag(Board board) {
        //this.id = count.getAndIncrement();
        var res = new ArrayList<Coordinates>(25 * board.getTurn());
        int turn = board.getTurn();
        for (int x = 0; x < board.getWidth(); x++) {
            for (int y = 0; y < board.getHeight(); y++) {
                int val = board.getCells()[x][y];
                if (val != 0) {
                    res.add(new Coordinates(x, y, board.getTurn(), val));
                }
            }
        }
        this.coordinates = TreeSet.ofAll(res);
    }

    public boolean noneOverlapping() {
        return coordinates.sliding(2).toJavaStream().noneMatch(x -> x.head().localEquals(x.last()));
    }

    public CoordinateBag add(Collection<Coordinates> coords) {
        return new CoordinateBag(coordinates.addAll(coords));
    }

    public Set<Integer> prune(int from, int to) {
        HashSet<Tuple2<Integer, Integer>> previous = new HashSet<>();
        HashSet<Integer> dead = new HashSet<>();
        for (; from <= to; from++) {
            HashMap<Tuple2<Integer, Integer>, Integer> current = new HashMap<>();
            Set<Integer> pruneAfterThis = new java.util.TreeSet<>();
            int finalFrom = from; // for lambda
            var currentSet = coordinates.filter(coord -> coord.getTurn() == finalFrom);
            for (var coord : currentSet) {
                var tuple = Tuple.of(coord.getX(), coord.getY());
                if (previous.contains(tuple)) {
                    pruneAfterThis.add(coord.getPlayer());
                } else if (current.containsKey(tuple)) {
                    pruneAfterThis.add(current.get(tuple));
                    pruneAfterThis.add(coord.getPlayer());
                } else {
                    current.put(tuple, coord.getPlayer());
                }
            }
            previous.addAll(current.keySet());
            dead.addAll(pruneAfterThis);
            for (int player : pruneAfterThis) {
                coordinates = coordinates.filter(coordinates1 -> !coordinates1.samePlayerLater(player, finalFrom));
            }
        }

    }

}
