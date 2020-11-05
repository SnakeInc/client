package de.uol.snakeinc.possibleMoves;

import lombok.CustomLog;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
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

    public void add(List<ActionPlayerCoordinates> toAdd) {
        if (depth == 0) {
            for (var apc : toAdd) {
                starts.add(new CombinationNode(apc, null));
            }
        } else {
            var old = starts;
            starts = new ArrayList<>(old.size() * 5);
            for (var node : old) {
                for (var apc : toAdd) {
                    starts.add(new CombinationNode(apc, node));
                }
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

    public class CombinationIterator implements Iterator<ActionPlayerCoordinates> {

        private CombinationNode current;

        CombinationIterator(CombinationNode node) {
            this.current = node;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
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
