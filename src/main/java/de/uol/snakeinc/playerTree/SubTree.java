package de.uol.snakeinc.playerTree;

import lombok.Getter;

@Getter
public abstract class SubTree extends PlayerTree {

    private PlayerTree parent;

    protected SubTree(PlayerTree parent) {
        super(parent.getDepth() + 1, parent.getTurn() + 1, parent.getBoard(), parent.getId());
        this.parent = parent;
    }

}
