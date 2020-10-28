package de.uol.snakeinc.entities;

import io.vavr.collection.SortedSet;
import io.vavr.collection.TreeSet;

import java.util.Collection;

public class CoordinateBag {

    private SortedSet<Coordinates> coordinates;

    CoordinateBag(Collection<Coordinates> coordinates) {
        this.coordinates = TreeSet.ofAll(coordinates);
    }

    CoordinateBag(SortedSet<Coordinates> coordinates) {
        this.coordinates = TreeSet.ofAll(coordinates);
    }

    public java.util.SortedSet<Coordinates> overlapping() {
        java.util.TreeSet<Coordinates> res = new java.util.TreeSet<Coordinates>();
        coordinates.sliding(2)
            .filter(x -> x.head().localEquals(x.last()))
            .forEach(x -> {
                res.add(x.head());
                res.add(x.last());
            });
        return res;
    }

    public CoordinateBag add(Collection<Coordinates> coords) {
        return new CoordinateBag(coordinates.addAll(coords));
    }
}
