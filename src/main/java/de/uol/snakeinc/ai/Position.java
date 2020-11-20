package de.uol.snakeinc.ai;

import de.uol.snakeinc.entities.Action;
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
            z = z - steps;
        } else if (direction == Direction.UP) {
            z = z + steps;
        } else if (direction == Direction.RIGHT) {
            x = x - steps;
        } else if (direction == Direction.LEFT) {
            x = x + steps;
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
            while (zLow <= zHeight) {
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
            while (xLow <= xHeight) {
                if (xLow != this.x) {
                    positions.add(new Position(xLow, z, position.getDirection()));
                }
                xLow++;
            }
        }
        return positions;
    }

    public boolean collides(Board board, PlayerOption option, boolean basic) {
        if (this.x < 0) {
            if (basic) {
                System.out.println("Action " + option.getAction() + " to collides wall x < 0 - " + x + " " + z);
            }
            return true;
        } else if(this.x >= board.getWidth()) {
            if (basic) {
                System.out.println("Action " + option.getAction() + " to collides wall x > width " + x + " " + z);
            }
            return true;
        } else if(this.z < 0 ) {
            if (basic) {
                System.out.println("Action " + option.getAction() + " to collides wall z < 0 " + x + " " + z);
            }
            return true;
        } else if (this.z >= board.getHeight()) {
            if (basic) {
                System.out.println("Action " + option.getAction() + " to collides wall z > height " + x + " " + z);
            }
            return true;
        } else if (!board.isFree(this.x, this.z)) {
            if (basic) {
                System.out.println("Action " + option.getAction() + " to collides position " + x + " " + z);
            }
            return true;
        }
        return false;
    }
}
