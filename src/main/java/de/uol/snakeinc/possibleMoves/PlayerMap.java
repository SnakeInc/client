package de.uol.snakeinc.possibleMoves;

import de.uol.snakeinc.entities.MapCoordinateBag;
import de.uol.snakeinc.entities.Player;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class PlayerMap implements Iterable<Player> {

    @Getter
    private Player[] players;
    public final int length;

    public PlayerMap(int size) {
        this.length = size + 1;
        this.players = new Player[length];
    }

    public PlayerMap(Player[] players) {
        this.players = players;
        this.length = players.length;
    }

    /**
     * tests whether the map contains a player under the given key.
     * @param key the key of where to look
     * @return true if there is a player else false
     */
    public boolean containsKey(int key) {
        try {
            return players[key] != null;
        } catch (ArrayIndexOutOfBoundsException exception) {
            return false;
        }
    }

    /**
     * tests whether the map contains exactly this player. it assumes that players are saved under with their id as
     * key.
     * @param value the player
     * @return true if there is a player saved under value.id for that player.equals holds true
     */
    public boolean containsValue(Player value) { // assumes players are stored at their id;
        try {
            return players[value.getId()] != null && players[value.getId()].equals(value);
        } catch (ArrayIndexOutOfBoundsException exception) {
            return false;
        }
    }

    /**
     * gets the player associated with the key.
     * @param key the key
     * @return the player if there is one else null
     */
    public Player get(int key) {
        try {
            return players[key];
        } catch (ArrayIndexOutOfBoundsException exception) {
            return null;
        }
    }

    /**
     * associates the player with the id in the map.
     * @param key   the key
     * @param value the player
     * @return the player if it was successfully placed in the map, else null
     */
    public Player put(int key, Player value) {
        try {
            players[key] = value;
            return value;
        } catch (ArrayIndexOutOfBoundsException exception) {
            return null;
        }
    }

    /**
     * places the player under its own id in the map.
     * @param value the player
     * @return the playeer if it was successfully placed, else null
     */
    public Player put(Player value) {
        try {
            players[value.getId()] = value;
            return value;
        } catch (ArrayIndexOutOfBoundsException exception) {
            return null;
        }
    }

    /**
     * removes the player under the key from the map.
     * @param key the key
     * @return the player if there was one under the key, else null
     */
    public Player remove(int key) {
        try {
            var res = players[key];
            players[key] = null;
            return res;
        } catch (ArrayIndexOutOfBoundsException exception) {
            return null;
        }
    }

    /**
     * clears the whole map, the map is empty afterwards.
     */
    public void clear() {
        Arrays.fill(players, null);
    }

    /**
     * return a set of all keys that have a player value associated with them.
     * @return the set
     */
    public Set<Integer> keySet() {
        var res = new HashSet<Integer>();
        for (int i = 0; i < players.length; i++) {
            if (players[i] != null) {
                res.add(i);
            }
        }
        return res;
    }

    public Collection<Player> values() {
        return Arrays.stream(players).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * creates an Iterator that iterates though all players in the map.
     * @return the iterator
     */
    public Iterator<Player> iterator() {
        return new Iterator<Player>() {
            int next;

            @Override
            public boolean hasNext() {
                return next < players.length;
            }

            @Override
            public Player next() {
                var res = players[next];
                while (next < players.length) {
                    next++;
                    if (containsKey(next)) {
                        return res;
                    }
                }
                return res;
            }
        };
    }

    @Override
    public void forEach(Consumer<? super Player> action) {
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                continue;
            } else {
                action.accept(players[i]);
            }
        }
    }

    /**
     * adds all possible Moves of the players in the map to the CombinationTree.
     * @param tree   the Tree to add
     * @param height the height of the map
     * @param width  the width of the map
     * @param map    the map
     */
    public void addToCombinationTree(CombinationTree tree, int height, int width, MapCoordinateBag map) {
        for (int i = 1; i < players.length; i++) {
            if (players[i] == null) {
                continue;
            } else {
                tree.add(players[i].getPossibleMoves(height, width, map), players.length, map.getMap());
            }
        }
    }
}
