package de.uol.snakeinc.entities;

import com.google.gson.JsonObject;
import de.uol.snakeinc.Common;
import de.uol.snakeinc.Config;
import de.uol.snakeinc.SnakeInc;
import de.uol.snakeinc.analyzingTools.BoardAnalyzer;
import de.uol.snakeinc.analyzingTools.MoveCalculation;
import de.uol.snakeinc.gui.Gui;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.text.DecimalFormat;
import java.util.HashMap;

/**
 * The main class to handle the evaluated cells.
 * Uses the boardAnalyzer to delegate the valuation of the cells. (f.e. OpponentMovesCalculation and Heuristics)
 * Calls the MoveCalculation to return the bestAction.
 */
@Log4j2
public class EvaluationBoard {

    @Getter
    private int width;
    @Getter
    private int height;
    @Getter
    private Player us;
    @Getter
    private int round;
    @Getter
    private BoardAnalyzer boardAnalyzer;



    @Getter
    private Cell[][] cells;

    private Player[] players;

    public EvaluationBoard(int width, int height, Player[] players, Player us, int round) {
        log.info("Initializing Board!");
        this.width = width;
        this.height = height;
        this.players = players;
        this.us = us;
        this.round = round;

        cells = new Cell[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }

        for (Player player : players) {
            if (player.getX() >= 0 && player.getX() < width &&
                player.getY() >= 0 && player.getY() < height) {
                cells[player.getX()][player.getY()].setId(player.getId());
            }
        }
        boardAnalyzer = new BoardAnalyzer(width, height);
    }

    /**
     * Updates the variables and the chosen moves of all players.
     * @param playerHashMap updated players
     * @param us us
     * @param round actual round
     */
    public void update(HashMap<Integer, Player> playerHashMap, Player us, int round) {
        log.info("Updating the evaluationBoard!");
        this.us = us;
        this.round = round;
        this.players = getActivePlayers(playerHashMap);

        updatePlayersCells(playerHashMap);

        //initiate analyzing process
        if (us.isActive()) {
            boardAnalyzer.analyze(cells, players, us);
        }
        logCurrentEvaluation(cells, us.isActive());
    }

    /**
     * logs the compute values for each cell.
     * @param cells cells
     * @param active todo
     */
    private void logCurrentEvaluation (Cell[][] cells, boolean active) {
        if (SnakeInc.hasGui()) {
            Gui gui = SnakeInc.getGui();
            if (!gui.getGuiBoard().isWidthAndHeight(cells.length, cells[0].length)) {
                gui.getGuiBoard().initializeCells(cells.length, cells[0].length);
            }
            gui.getGuiBoard().updateBoard(cells, this.us);
        } else if (active) {
            StringBuilder str = new StringBuilder();
            DecimalFormat f = new DecimalFormat("##.00");
            str.append("\n");
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[0].length; j++) {
                    str.append(f.format(cells[i][j].getRisks())).append("\t");
                }
                str.append("\n");
            }

            log.debug(str.toString());
        }
    }

    /**
     * gives back an array of active players.
     * @param playerHashMap players
     * @return              players[activePlayers]
     */
    private Player [] getActivePlayers (HashMap<Integer, Player> playerHashMap) {
        Player[] playersArrayTmp = new Player[playerHashMap.size()];
        int count = 0;
        for (Integer position : playerHashMap.keySet()) {
            if (playerHashMap.get(position).isActive()) {
                playersArrayTmp[count] = playerHashMap.get(position);
                count++;
            }
        }
        Player[] playersArray = new Player[count];
        if (count >= 0) {
            System.arraycopy(playersArrayTmp, 0, playersArray, 0, count);
        }
        return  playersArray;
    }

    /**
     * Updating Cells in jump-cases.
     * @param player the jumping player
     */
    private void updateJumpingPlayerCells(Player player) {
        int x = player.getX();
        int y = player.getY();
        int speed = player.getSpeed();
        int iD = player.getId();
        setCells(x, y, iD);
        var xy = Common.generateXY(player.getDirection(), x, y, -(speed - 1));
        setCells(xy.getX(), xy.getY(), iD);
    }


    private void setCells(int x, int y, int iD) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            cells[x][y].setId(iD);
        }
    }

    /**
     * updates the latest moves of all players to the cells.
     * @param playerHashMap all players
     */
    public void updatePlayersCells(HashMap<Integer, Player> playerHashMap) {
        int iD;
        int x;
        int y;
        int speed;
        Player tmpPlayer;
        for (int i = 1; i < playerHashMap.keySet().size() + 1; i++) {
            iD = playerHashMap.get(i).getId();
            x = playerHashMap.get(i).getX();
            y = playerHashMap.get(i).getY();
            speed = playerHashMap.get(i).getSpeed();
            tmpPlayer = playerHashMap.get(i);

            //Checking for jumping
            if (round % Config.ROUNDS_PER_JUMP == 0 && speed >= 3) {
                updateJumpingPlayerCells(tmpPlayer);
            } else {
                for (var xy : Common.generateAllXYUpTo(tmpPlayer.getDirection(), x, y, -speed)) {
                    setCells(xy.getX(), xy.getY(), iD);
                }
            }
        }
    }



    /**
     * Called after an action was chosen.
     */
    public void prepareNextPhase() {
        boardAnalyzer.prepareNextPhase();
    }

    /**
     * starts a MoveCalculation and returns the resulting action.
     * @return action
     */
    public Action getAction() {
        MoveCalculation moveCalculation = new MoveCalculation(cells, us, boardAnalyzer);
        return moveCalculation.calculateBestAction();
    }

    /**
     * Parse board based on json-format.
     * @param json    json from websocket
     * @param players parsed players
     * @param us      // todo this
     * @return parsed board
     */
    public static EvaluationBoard initParseFromJson(JsonObject json, HashMap<Integer, Player> players, Player us) {
        int width = json.get("width").getAsInt();
        int height = json.get("height").getAsInt();

        Player[] playersArray = new Player[players.size()];
        int count = 0;
        for (Integer position : players.keySet()) {
            playersArray[count] = players.get(position);
            count++;
        }

        return new EvaluationBoard(width, height, playersArray, us, 0);
    }
}
