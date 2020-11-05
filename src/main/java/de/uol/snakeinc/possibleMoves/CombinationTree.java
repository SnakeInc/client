package de.uol.snakeinc.possibleMoves;

import lombok.CustomLog;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@CustomLog
public class CombinationTree {

    private List<CombinationNode> starts = new ArrayList<CombinationNode>(5);
    @Getter
    int depth = 0;

    public CombinationTree() {
    }

    public void add(ArrayList<ActionPlayerCoordinates> toAdd) {
        if (depth == 0) {
            for (int i = 0; i < toAdd.size(); i++) {
                starts.add(new CombinationNode(toAdd.get(i), null));
            }
        } else {
            var old = starts;
            starts = new ArrayList<>(old.size() * 5);
            int oldIt = 0, toAddIt = 0;
            for (; oldIt < old.size(); oldIt++) {
                for (; toAddIt < toAdd.size(); toAddIt++) {
                    starts.add(new CombinationNode(toAdd.get(toAddIt), old.get(oldIt)));
                }
                toAddIt = 0;
            }
        }
        depth++;
    }

    public List<CombinationIterator> getCombinations() {
        return starts.stream().map(CombinationNode::toIterator).collect(Collectors.toList());
    }

    public Stream<CombinationIterator> getCombinationStream() {
        return starts.stream().map(CombinationNode::toIterator);
    }

    private class CombinationNode {

        volatile ActionPlayerCoordinates entry;
        volatile CombinationNode parent;

        CombinationNode(ActionPlayerCoordinates entry, CombinationNode node) {
            this.entry = entry;
            this.parent = node;
        }

        CombinationIterator toIterator() {
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
