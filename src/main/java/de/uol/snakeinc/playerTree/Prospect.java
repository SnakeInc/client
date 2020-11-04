package de.uol.snakeinc.playerTree;

import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Board;
import de.uol.snakeinc.entities.Player;
import io.vavr.collection.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
public class Prospect {

    private int alive = 0;
    private int dead = 0;
    private int othersDead = 0;
    private int othersAlive = 0;
    private long overallCounted = 0;
    private int others;

    public Prospect(int others) {
        this.others = others;
    }

    void addAlive(int othersDead) {
        this.alive++;
        this.overallCounted++;
        this.othersDead += othersDead;
        this.othersAlive += (others - othersDead);
    }

    void addDead(int othersDead) {
        this.dead++;
        this.overallCounted++;
        this.othersDead += othersDead;
        this.othersAlive += (others - othersDead);
    }

    public static EnumMap<Action, Prospect> getProspects(Board board, int us, int depth) {
        Root ourRoot = new Root(board.getPlayer(us), board);
        ourRoot.expand(depth);
        var ourOptions = ourRoot.getChildren();
        var ourEnds = new EnumMap<Action, List<PlayerTree>>(Action.class);
        for (var entry : ourOptions.entrySet()) {
            ourEnds.put(entry.getKey(), entry.getValue().getChildren(depth - 1));
        }
        var others = Arrays.stream(board.getPlayers())
            .filter(Objects::nonNull)
            .filter(Player::isActive)
            .filter(player -> player.getId() != us)
            .map(player -> new Root(player, board))
            .peek(root -> root.expand(depth)) // bad use of peek? dont know smth better right now
            .map(root -> root.getChildren(depth - 1))
            .collect(Collectors.toList());

    }

}
