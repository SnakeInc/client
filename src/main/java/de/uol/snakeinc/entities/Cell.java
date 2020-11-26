package de.uol.snakeinc.entities;

import lombok.Getter;

public class Cell {

    @Getter
    private int value;

    private Cell prevCell;
    private Cell nextCell;

    public Cell() {
        value = 0;
    }

    public void isHead(int id, Cell prevCell) {
        value = id *10;
        this.prevCell = prevCell;
    }

    public void isTail(int id, Cell nextCell) {
        value = id *10;
        this.nextCell = nextCell;
    }

    public void isTail(int id, Cell nextCell, Cell prevCell) {
        value = id * 10;
        this.nextCell = nextCell;
        this.prevCell = prevCell;
    }


}
