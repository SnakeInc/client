package de.uol.snakeinc.util;

import lombok.Builder;
import lombok.CustomLog;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

@CustomLog
@Getter
public class MaximinTree<V extends Expandable> {

    int maxDepth;
    private Node<V> parent;
    //List<V> grandchildren;

    public MaximinTree(V startcontent, int depth) {
        parent = (Node<V>) Node.builder()
            .content(startcontent)
            .depth(0)
            .probability(1L)
            .parent(null)
            .build();

        if (depth > 0) {
            parent.expand(depth);
        }
    }

    @Builder
    @ToString
    @EqualsAndHashCode
    @Getter
    @Setter
    @With
    private static class Node<V extends Expandable> {

        int depth;
        Node<V>[] children;
        Node<V> parent;
        V content;
        float probability;

        void expand(int maxDepth) {
            Expandable[] expandedContent = content.expand();
            children = new Node[expandedContent.length];
            for (int count = 0; count < children.length; count++) {
                children[count] = (Node<V>) Node.builder()
                    .depth(depth + 1)
                    .parent((Node<Expandable>) this)
                    .probability(1 / expandedContent.length)
                    .content(expandedContent[count])
                    .build();

                if (maxDepth > depth) {
                    children[count].expand(maxDepth);
                }
            }
        }
    }
}
