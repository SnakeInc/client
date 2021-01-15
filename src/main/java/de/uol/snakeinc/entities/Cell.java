package de.uol.snakeinc.entities;

import de.uol.snakeinc.Config;
import de.uol.snakeinc.pathfinding.PathCell;
import lombok.Getter;

public class Cell extends PathCell {


    //Basic Value.
    @Getter
    private double value;

    //Value that is calculated temporarily
    private double tmpMoveCalcValue;

    //The risk that another agent will move to this cell
    private double opponentMovementRisk;

    private double killIncentive;

    //Area-risk
    private double areaRisk;

    //
    private double deadEndRisk;

    private boolean hardDeadly;

    private boolean tmpDeadly;

    @Getter
    private int iD;

    public Cell(int x, int y) {
        super(x, y);
        value = 1;
        opponentMovementRisk = 1;
        tmpMoveCalcValue = 1;
        areaRisk = 1;
        killIncentive = 1;
        deadEndRisk = 1;
        hardDeadly = false;
        tmpDeadly = false;
    }

    @Override
    public boolean isInUse() {
        return value != 1;
    }

    public void setId(int id) {
        this.iD = id;
        value = Config.DEATH_VALUE;
        this.hardDeadly = true;
    }

    public boolean isDeadly() {
        return hardDeadly || tmpDeadly;
    }

    /**
     * Todo this.
     * @param depth Todo this
     */
    public void raiseActionRisk(int depth) {
        switch (depth) {
            case 1:
                opponentMovementRisk = opponentMovementRisk * Config.MOVE_RISK_1;
                break;
            case 2:
                opponentMovementRisk = opponentMovementRisk * Config.MOVE_RISK_2;
                break;
            case 3:
                opponentMovementRisk = opponentMovementRisk * Config.MOVE_RISK_3;
                break;
            default:
                throw new IllegalStateException();
        }
    }

    public void setAreaRisk(double risk) {
        this.areaRisk = risk;
    }

    public double getAreaRisk() {
        return this.areaRisk;
    }

    public double getDeadEndRisk() {
        return this.deadEndRisk;
    }

    public double getKillAlgorithmRisk() {
        return this.killIncentive;
    }

    public double getOpponentMovementRisk() {
        return this.opponentMovementRisk;
    }

    public void setDeadEndRisk(double riskValue) {
        if ((! hardDeadly) && this.deadEndRisk < riskValue) {
            this.deadEndRisk = riskValue;
        }
    }

    public double getValue() {
        return value;
    }

    public double getRisks() {
        //Not calculated: Speed-Up.
        //Not calculated: Interaction between players.
        //Not calculated: Interinteraktion between players.
        if (hardDeadly || tmpDeadly) {
            return Config.DEATH_VALUE;
        }
        return getValue()  * opponentMovementRisk * tmpMoveCalcValue * areaRisk * deadEndRisk * killIncentive;
    }

    /**
     * TODO this and rename.
     */
    public void clearPseudoValue() {
        tmpMoveCalcValue = 1;
        tmpDeadly = false;
    }

    public void setTmpMoveCalcValue() {
        tmpMoveCalcValue = Config.DEATH_VALUE;
        tmpDeadly = true;
    }

    /**
     * Prepare the next phase.
     */
    public void prepareNextPhase() {
        this.opponentMovementRisk = 1;
        this.killIncentive = 1;
    }

    public void setKillIncentive() {
        this.killIncentive = Config.KILL_INCENTIVE;
    }
}
