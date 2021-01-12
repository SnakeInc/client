package de.uol.snakeinc.entities;

import com.google.gson.JsonObject;
import de.uol.snakeinc.analyzingTools.BoardAnalyzer;
import de.uol.snakeinc.analyzingTools.MoveCalculation;
import lombok.CustomLog;
import lombok.Getter;

import java.util.HashMap;

/**
 * The main class to handle the evaluated cells.
 * Uses the boardAnalyzer to delegate the valuation of the cells. (f.e. OpponentMovesCalculation and Heuristics)
 * Calls the MoveCalculation to return the bestAction.
 */
@CustomLog
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
                cells[i][j] = new Cell(j, i);
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
        boardAnalyzer.analyze(cells, players, us);

        logCurrentEvaluation(cells);
    }

    /**
     * logs the compute values for each cell.
     * @param cells cells
     */
    private void logCurrentEvaluation (Cell[][] cells) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[1].length; j++) {
                str.append(cells[i][j].getRisks()).append(" ");
            }
            str.append("\n");
        }

        log.debug(str.toString());
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
        for (int i = 0; i < count; i++) {
            playersArray[i] = playersArrayTmp[i];
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
        switch (player.getDirection()) {
            case UP:
                setCells(x, y, iD);
                setCells(x, y + speed - 1, iD);
                break;
            case DOWN:
                setCells(x, y, iD);
                setCells(x, y - speed + 1, iD);
                break;
            case LEFT:
                setCells(x, y, iD);
                setCells(x + speed - 1, y, iD);
                break;
            case RIGHT:
                setCells(x, y, iD);
                setCells(x - speed + 1, y, iD);
                break;
            default:
                throw new IllegalStateException();
        }
    }
    private void setCells(int x, int y, int iD) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            cells[x][y].setCell(iD);
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
            if (round % 6 == 0 && speed >= 3) {
                updateJumpingPlayerCells(tmpPlayer);
            } else {
                //Updating Cells without jumping. Implements an iteration-logic for snakes
                switch (tmpPlayer.getDirection()) {
                    case UP:
                        for (int j = 0; j < speed; j++) {
                            setCells(x, y + j, iD);
                        }
                        break;
                    case DOWN:
                        for (int j = 0; j < speed; j++) {
                            setCells(x, y - j, iD);
                        }
                        break;
                    case LEFT:
                        for (int j = 0; j < speed; j++) {
                            setCells(x + j, y, iD);
                        }
                        break;
                    case RIGHT:
                        for (int j = 0; j < speed; j++) {
                            setCells(x - j, y, iD);
                        }
                        break;
                    default:
                        throw new IllegalStateException();
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
     * Todo javadoc.
     * @return todo this
     */
    public Action startingStrategy() {
        //TODO: Implement this.
        return Action.CHANGE_NOTHING;
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
