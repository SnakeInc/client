package de.uol.snakeinc.entities;

import de.uol.snakeinc.deadendflooding.DeadCell;
import de.uol.snakeinc.Config;
import de.uol.snakeinc.pathfinding.PathCell;
import lombok.Getter;
import lombok.Setter;

public class Cell extends PathCell implements DeadCell {


    //Basic Value.
    @Getter
    private double value;

    //Value that is calculated temporarily
    private double tmpMoveCalcValue;

    //The risk that another agent will move to this cell
    @Getter
    private double opponentMovementRisk;

    @Getter
    private double killIncentive;

    //Area-risk
    @Getter @Setter
    private double areaRisk;

    //Highlighted path
    @Getter
    private double pathHighlight;

    @Getter
    private double deadEndRisk;

    private boolean hardDeadly;

    private boolean tmpDeadly;

    private double deadEndFlooding;

    private double deadEndJumping;

    @Getter
    private int iD;

    private boolean hit;

    private boolean flooded;

    public Cell(int x, int y) {
        super(x, y);
        value = 1;
        opponentMovementRisk = 1;
        tmpMoveCalcValue = 1;
        areaRisk = 1;
        pathHighlight = 1;
        killIncentive = 1;
        deadEndRisk = 1;
        deadEndFlooding = 1;
        deadEndJumping = 1;
        hardDeadly = false;
        tmpDeadly = false;
        flooded = false;
    }

    @Override
    public boolean isInUse() {
        return iD != 0;
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
     * Raises the opponent movement risk according to the depth.
     * @param depth the depth
     */
    public void raiseOpponentMovementRisk(int depth) {
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

    public void setPathHighlight(double risk) {
        this.pathHighlight = risk;
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

    public void setDeadEndFlooding(double deadEndFlooding) {
        if (deadEndFlooding > this.deadEndFlooding) {
            this.deadEndFlooding = deadEndFlooding;
        }
    }

    public void setDeadEndFloodingReset(double deadEndFlooding) {
        this.deadEndFlooding = deadEndFlooding;
    }

    public double getDeadEndFlooding() {
        return this.deadEndFlooding;
    }

    public void setDeadEndJumping(double deadEndJumping) {
        this.deadEndJumping = deadEndJumping;
    }

    public double getDeadEndJumping() {
        return this.deadEndJumping;
    }

    public void setDeadEndRisk(double riskValue) {
        if ((! hardDeadly) && this.deadEndRisk < riskValue) {
            this.deadEndRisk = riskValue;
        }
    }

    public double getRisks() {
        //Not calculated: Speed-Up.
        //Not calculated: Interaction between players.
        //Not calculated: Interinteraktion between players.
        if (hardDeadly || tmpDeadly) {
            return Config.DEATH_VALUE;
        }
        return getValue() *
            opponentMovementRisk *
            tmpMoveCalcValue *
            areaRisk *
            deadEndRisk *
            killIncentive *
            pathHighlight *
            deadEndFlooding;
        //* deadEndJumping;
    }

    /**
     * Clears the temporary move calculation value.
     */
    public void clearTmpMoveCalcValue() {
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

    @Override
    public boolean hasHit() {
        return this.hit;
    }

    @Override
    public void setHit(boolean hit) {
        this.hit = hit;
    }

    @Override
    public boolean flooded() {
        return this.flooded;
    }

    @Override
    public void setFlooded(boolean flooded) {
        this.flooded = flooded;
    }

    @Override
    public Cell clone() {
        Cell cell = new Cell(getX(), getY());
        cell.deadEndRisk = this.deadEndRisk;
        cell.iD = this.iD;
        cell.value = this.value;
        cell.hardDeadly = this.hardDeadly;
        return cell;
    }

    public void setNoneDeadly() {
        value = 1;
        tmpDeadly = false;
        hardDeadly = false;
    }
}