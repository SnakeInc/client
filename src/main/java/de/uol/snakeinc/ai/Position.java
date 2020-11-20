package de.uol.snakeinc.ai;

import de.uol.snakeinc.entities.Board;
import de.uol.snakeinc.entities.Direction;

import java.util.ArrayList;
import java.util.List;

public class Position {

    private int x;
    private int z;

    private Direction direction;

    public Position(int x, int z, Direction direction) {
        this.x = x;
        this.z = z;
        this.direction = direction;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return this.x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getZ() {
        return this.z;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void turnLeft() {
        if (direction == Direction.DOWN) {
            this.direction = Direction.LEFT;
        } else if (direction == Direction.LEFT) {
            this.direction = Direction.UP;
        } else if (direction == Direction.UP) {
            this.direction = Direction.RIGHT;
        } else {
            this.direction = Direction.DOWN;
        }
    }

    public void turnRight() {
        if (direction == Direction.DOWN) {
            this.direction = Direction.RIGHT;
        } else if (direction == Direction.RIGHT) {
            this.direction = Direction.UP;
        } else if (direction == Direction.UP) {
            this.direction = Direction.LEFT;
        } else {
            this.direction = Direction.DOWN;
        }
    }

    public void move(int steps) {
        if (direction == Direction.DOWN) {
            x = x - steps;
        } else if (direction == Direction.UP) {
            x = x + steps;
        } else if (direction == Direction.RIGHT) {
            z = z + steps;
        } else if (direction == Direction.LEFT) {
            z = z - steps;
        }
    }

    public Position clone() {
        return new Position(this.x, this.z, this.direction);
    }

    public List<Position> getFromCurrentPosition(Position position) {
        List<Position> positions = new ArrayList<Position>();
        if (position.x == x) {
            int zLow = this.z;
            int zHeight = position.z;
            if (position.z < this.z) {
                zLow = position.z;
                zHeight = this.z;
            }
            while (zLow < zHeight) {
                if (zLow != this.z) {
                    positions.add(new Position(x, zLow, position.getDirection()));
                }
                zLow++;
            }
        } else {
            int xLow = this.x;
            int xHeight = position.x;
            if (position.x < this.x) {
                xLow = position.x;
                xHeight = this.x;
            }
            while (xLow < xHeight) {
                if (xLow != this.x) {
                    positions.add(new Position(xLow, z, position.getDirection()));
                }
                xLow++;
            }
        }
        return positions;
    }

    public boolean collides(Board board) {
        if (this.x < 0 || this.x >= board.getWidth() ||
            this.z < 0 || this.z >= board.getHeight() ||
            board.getCells()[z][x] != 0) {
            return true;
        }
        return false;
    }
}
