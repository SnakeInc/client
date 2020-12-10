package de.uol.snakeinc.entities;

import lombok.Getter;

public class Cell {

    @Getter
    private double value;

    private double actionRisk;

    @Getter
    private Cell prevCell;
    @Getter
    private Cell nextCell;
    @Getter
    private int iD;

    public Cell() {
        value = 1;
        actionRisk = 1;
    }


    public void setId(int id) {
        this.iD = id;
        value = 10;
    }

    public void setNextCell(int id, Cell nextCell) {
        this.nextCell = nextCell;
    }

    public void setPrevCell(int id, Cell prevCell) {
        value = 10;
        this.prevCell = prevCell;
    }

    public void setPrevHoleCell(int id, Cell prevCell) {
        this.prevCell = prevCell;
    }

    public boolean isDeadly(){
        return value >= 10;
    }

    public void raiseActionRisk(int depth) {
        switch (depth) {
            case 1:
                actionRisk = actionRisk * 5/4;
            case 2:
                actionRisk = actionRisk * 17/16;
            case 3:
                actionRisk = actionRisk * 65/64;
        }
    }

    public double getValue() {
        return value;
    }

    public double getRisks() {
        //Not calculated: Speed-Up.
        //Not calculated: Interinteraktion between players.
        return getValue() * actionRisk;
    }

    public void prepareNextPhase() {
        actionRisk = 1;
    }

}
