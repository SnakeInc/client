package de.uol.snakeinc.entities;

public enum Direction {

    UP("LEFT", "RIGHT"),
    DOWN("RIGHT", "LEFT"),
    LEFT("DOWN", "UP"),
    RIGHT("UP", "DOWN");

    String right;
    String left;

    Direction(String left, String right) {
        this.right = right;
        this.left = left;
    }

    public Direction getRight() {
        return this.valueOf(right);
    }

    public Direction getLeft() {
        return this.valueOf(left);
    }
}
