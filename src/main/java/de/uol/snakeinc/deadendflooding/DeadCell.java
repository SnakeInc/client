package de.uol.snakeinc.deadendflooding;

public interface DeadCell {

    boolean hasHit();

    void setHit(boolean hit);

    boolean flooded();

    void setFlooded(boolean flooded);

}
