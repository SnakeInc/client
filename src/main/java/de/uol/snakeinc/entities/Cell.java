package de.uol.snakeinc.entities;

import lombok.Getter;

public class Cell {

    @Getter
    private double value;

    private double actionRisk;
    private Cell prevCell;
    private Cell nextCell;
    @Getter
    private int iD;

    public Cell() {
        value = 0;
        actionRisk = 0;
    }


    public void setId(int id) {
        this.iD = id;
    }

    public void enqueue(int id, Cell nextCell) {
        value = id *10;
        this.nextCell = nextCell;
    }

    public void enqueue(int id, Cell prevCell, Cell nextCell) {
        value = id * 10;
        this.nextCell = nextCell;
        this.prevCell = prevCell;
    }

    public boolean isDeadly(){
        return value > 1;
    }

    public void raiseActionRisk(int depth) {
        switch (depth) {
            case 1:
                actionRisk = actionRisk +1/4;
            case 2:
                actionRisk = actionRisk + 1/16;
            case 3:
                actionRisk = actionRisk + 1/64;
        }
    }

    public double getRisks() {
        //Not calculated: Speed-Up.
        //Not calculated: Interinteraktion between players.
        return value + actionRisk;
    }

    public void prepareNextPhase() {
        actionRisk = 0;
    }


}
