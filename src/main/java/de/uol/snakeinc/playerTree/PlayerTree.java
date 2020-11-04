package de.uol.snakeinc.playerTree;

import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Board;
import de.uol.snakeinc.entities.CoordinateBag;
import de.uol.snakeinc.entities.Direction;
import io.vavr.collection.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Getter
@Setter(AccessLevel.PROTECTED)
public abstract class PlayerTree {

    private int depth;
    private int turn;
    private Board board;

    private CoordinateBag occupied;
    private int x;
    private int y;
    private int id;
    private int speed;
    private Direction direction;
    private boolean active;
    private boolean expanded = false;
    private Map<Integer, List<PlayerTree>> childrenAtDepth = new HashMap<>();

    private EnumMap<Action, PlayerTree> children = new EnumMap<Action, PlayerTree>(Action.class);

    protected PlayerTree(int depth, int turn, Board board, int id) {
        this.depth = depth;
        this.turn = turn;
        this.board = board;
        this.id = id;
    }

    protected boolean isJumpTurn() {
        return false;
    } //TODO this

    protected boolean isOnBoard() {
        return board.isOnBoard(x, y);
    }

    public void expand(int intendedDepth) {
        if (!expanded) {
            if (this.depth < intendedDepth) {
                if (this.active) {
                    for (Action action : Action.values()) {
                        var child = new ActiveSubTree(this, action);
                        children.put(action, child);
                    }
                } else {
                    int depth = this.depth;
                    PlayerTree last = this;
                    while (depth < intendedDepth) {
                        depth++;
                        var child = new EmptySubTree(last);
                        last.children.put(Action.CHANGE_NOTHING, child);
                        last.expanded = true;
                        last = child;
                    }
                }
            }
            expanded = true;
        } else {
            children.values().forEach(child -> child.expand(intendedDepth));
        }
    }

    public Stream<PlayerTree> getChildStream(int depth) {
        if (this.depth < depth) {
            return children.values().stream().flatMap(playerTree -> playerTree.getChildStream(depth));
        } else if (this.depth == depth) {
            return this.children.values().stream().filter(Objects::nonNull);
        } else
            return null;
    }

    public List<PlayerTree> getChildren(int depth) {
        if (childrenAtDepth.containsKey(depth)) {
            return childrenAtDepth.get(depth);
        } else {
            var res = List.ofAll(getChildStream(depth));
            childrenAtDepth.put(depth, res);
            return res;
        }
    }
}
