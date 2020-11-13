package de.uol.snakeinc.possibleMoves;

import de.uol.snakeinc.util.CoordinateMap;
import lombok.CustomLog;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CustomLog
public class CombinationTree {

    private ArrayList<CombinationNode> starts = new ArrayList<CombinationNode>(5);
    @Getter
    int depth = 0;

    public CombinationTree() {
    }

    public void add(ArrayList<ActionPlayerCoordinates> toAdd, int players, int x, int y) {
        if (depth == 0) {
            for (int i = 0; i < toAdd.size(); i++) {
                var apc = toAdd.get(i);
                var dead = IntSet.ofSize(players);
                var coordinates = new CoordinateMap(x, y);
                dead = coordinates.putAll(apc.getCoordinates(), dead);
                if (!apc.getPlayer().isActive()) {
                    dead.add(apc.getPlayer().getId());
                }
                starts.add(new CombinationNode(toAdd.get(i), null, coordinates, dead));
            }
        } else {
            var old = starts;
            starts = new ArrayList<>(old.size() * 5);
            int oldIt = 0, toAddIt = 0;
            for (; oldIt < old.size(); oldIt++) {
                var oldNode = old.get(oldIt);
                for (; toAddIt < toAdd.size(); toAddIt++) {
                    var apc = toAdd.get(toAddIt);
                    var dead = IntSet.of(oldNode.dead);
                    var coordinates = new CoordinateMap(oldNode.coordinates);
                    dead = coordinates.putAll(toAdd.get(toAddIt).getCoordinates(), dead);
                    if (!apc.getPlayer().isActive()) {
                        dead.add(apc.getPlayer().getId());
                    }
                    starts.add(new CombinationNode(toAdd.get(toAddIt), oldNode, coordinates, dead));
                }
                toAddIt = 0;
            }
        }
        depth++;
    }

    public List<CombinationIterator> getCombinations() {
        return starts.stream().map(CombinationNode::toIterator).collect(Collectors.toList());
    }

    public ArrayList<CombinationNode> getRawCombinations() {
        return starts;
    }

    public class CombinationNode {

        @Getter
        private ActionPlayerCoordinates entry;
        @Getter
        private CombinationNode parent;
        @Getter
        private CoordinateMap coordinates;
        @Getter
        private IntSet dead;

        CombinationNode(ActionPlayerCoordinates entry, CombinationNode node, CoordinateMap coordinates, IntSet dead) {
            this.entry = entry;
            this.parent = node;
            this.coordinates = coordinates;
            this.dead = dead;
        }

        public CombinationIterator toIterator() {
            return new CombinationIterator(this);
        }

    }

    public class CombinationIterator {

        private final CombinationNode head;
        private CombinationNode current;

        CombinationIterator(CombinationNode node) {
            this.head = node;
            this.current = node;
        }

        public void reset() {
            this.current = head;
        }

        public boolean hasNext() {
            return current != null;
        }

        public ActionPlayerCoordinates next() {
            var res = current.entry;
            current = current.parent;
            return res;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return new CombinationIterator(current);
        }
    }
}
