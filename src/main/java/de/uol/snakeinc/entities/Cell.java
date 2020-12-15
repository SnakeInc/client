package de.uol.snakeinc.entities;

import lombok.Getter;

public class Cell {

    @Getter
    private double value;
    private double pseudoValue;

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
        pseudoValue = 1;
    }


    public void setId(int id) {
        this.iD = id;
        value = 10;
    }

    //??? Why id?
    public void setNextCell(int id, Cell nextCell) {
        this.nextCell = nextCell;
    }

    //??? Why id?
    public void setPrevCell(int id, Cell prevCell) {
        value = 10;
        this.prevCell = prevCell;
    }

    //??? Why id?
    public void setPrevHoleCell(int id, Cell prevCell) {
        this.prevCell = prevCell;
    }

    public boolean isDeadly(){
        return value >= 10;
    }

    public void raiseActionRisk(int depth) {
        switch (depth) {
            case 1:
                actionRisk = actionRisk * 1.25;
            case 2:
                actionRisk = actionRisk * 1.0625;
            case 3:
                actionRisk = actionRisk * 1.015625;
        }
    }

    public double getValue() {
        return value;
    }

    public double getRisks() {
        //Not calculated: Speed-Up.
        //Not calculated: Interinteraktion between players.
        return getValue() * actionRisk * pseudoValue;
    }

    public void clearPseudoValue() {
        pseudoValue = 1;
    }

    public void setPseudoValue() {
        pseudoValue = 10;
    }

    public void prepareNextPhase() {
        this.actionRisk = 1;
    }
}
