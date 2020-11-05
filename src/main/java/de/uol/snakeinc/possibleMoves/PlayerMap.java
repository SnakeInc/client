package de.uol.snakeinc.possibleMoves;

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

    public boolean containsKey(int key) {
        try {
            return players[key] != null;
        } catch (ArrayIndexOutOfBoundsException exception) {
            return false;
        }
    }

    public boolean containsValue(Player value) { // assumes players are stored at their id;
        try {
            return players[value.getId()] != null && players[value.getId()].equals(value);
        } catch (ArrayIndexOutOfBoundsException exception) {
            return false;
        }
    }

    public Player get(int key) {
        try {
            return players[key];
        } catch (ArrayIndexOutOfBoundsException exception) {
            return null;
        }
    }

    public Player put(int key, Player value) {
        try {
            players[key] = value;
            return value;
        } catch (ArrayIndexOutOfBoundsException exception) {
            return null;
        }
    }

    public Player remove(int key) {
        try {
            var res = players[key];
            players[key] = null;
            return res;
        } catch (ArrayIndexOutOfBoundsException exception) {
            return null;
        }
    }

    public void clear() {
        Arrays.fill(players, null);
    }

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
}
