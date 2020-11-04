package de.uol.snakeinc.playerTree;

public class EmptySubTree extends SubTree {

    public EmptySubTree(PlayerTree parent) {
        super(parent);
        setActive(false);
        //Hopefully nothing more to do
    }
}
