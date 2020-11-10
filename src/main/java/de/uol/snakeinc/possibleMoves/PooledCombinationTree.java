package de.uol.snakeinc.possibleMoves;

import java.util.ArrayList;

public class PooledCombinationTree {

    private ArrayList<CombinationNode>[] lists;
    private int depth = 0;

    public PooledCombinationTree(int size) {
        lists = new ArrayList[size];

        for (int mult = 5, i = 0; i < size; i++, mult *= 5) {
            lists[i] = new ArrayList<CombinationNode>(mult);
        }
    }

    public void add(ArrayList<ActionPlayerCoordinates> toAdd) {
        if (depth == 0) {
            for (int i = 0; i < toAdd.size(); i++) {
                lists[depth].add(new CombinationNode(toAdd.get(i), null));
            }
        } else {
            var old = lists[depth - 1];
            var starts = lists[depth];
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

    public void recycle() {
        for (int i = 0; i < depth; i++) {
            lists[i].clear();
        }
        depth = 0;
    }

    public ArrayList<CombinationNode> getRawCombinations() {
        return lists[depth - 1];
    }

    public class CombinationNode {

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

        private CombinationNode current;

        CombinationIterator(CombinationNode node) {
            this.current = node;
        }

        public boolean hasNext() {
            return current != null;
        }

        public ActionPlayerCoordinates next() {
            var res = current.entry;
            current = current.parent;
            return res;
        }
    }
}
