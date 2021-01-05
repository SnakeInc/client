package de.uol.snakeinc.pathfinding.astar;

import java.util.LinkedList;

/**
 A simple priority list, also called a priority queue.
 Objects in the list are ordered by their priority,
 determined by the object's Comparable interface.
 The highest priority item is first in the list.
 */
public class PriorityList extends LinkedList {

    public void add(Comparable object) {
        for (int i=0; i<size(); i++) {
            if (object.compareTo(get(i)) <= 0) {
                add(i, object);
                return;
            }
        }
        addLast(object);
    }
}
