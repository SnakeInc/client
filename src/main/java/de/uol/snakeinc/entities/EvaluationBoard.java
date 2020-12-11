package de.uol.snakeinc.entities;

import com.google.gson.JsonObject;
import de.uol.snakeinc.analyzingTools.BoardAnalyzer;
import de.uol.snakeinc.analyzingTools.MoveCalculation;
import lombok.CustomLog;
import lombok.Getter;

import java.util.HashMap;
import java.util.Set;

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

    private BoardAnalyzer boardAnalyzer;

    private Set<Tupel> neighbourCells;


    @Getter
    private Cell cells[][];

    private Player players[];

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
                cells[i][j] = new Cell();
            }
        }

        for (Player player : players) {
            if (player.getX() >= 0 && player.getX() < width &&
                player.getY() >= 0 && player.getY() < height) {
                cells[player.getX()][player.getY()].setId(player.getId());
            }
        }
        boardAnalyzer = new BoardAnalyzer(cells, players, us);
    }

    public void update(HashMap<Integer, Player> playerHashMap, Player us, int round) {
        log.info("Updating the evaluationBoard!");
        this.us = us;
        this.round = round;
        Player[] playersArray = new Player[playerHashMap.size()];
        int count = 0;
        for (Integer position : playerHashMap.keySet()) {
            if(playerHashMap.get(position).isActive()) {
            playersArray[count] = playerHashMap.get(position);
            count++;}
        }
        players = playersArray;
        int iD;
        int x;
        int y;
        int speed;
        for (int i=0; i < count; i++) {
            iD = players[i].getId();
            x = players[i].getX();
            y = players[i].getY();
            speed = players[i].getSpeed();
            Cell tmp;
            if (round % 6 == 0 && speed >= 3) {
                updateJumpingPlayer(players[i]);
            } else {
                switch (players[i].getDirection()) {
                    case UP:
                        tmp = cells[x][y + speed];
                        for (int j = 0; j < speed; j++) {
                            tmp.setNextCell(iD, cells[x][y + speed - j - 1]);
                            cells[x][y + speed - j - 1].setPrevCell(iD, tmp);
                            tmp = cells[x][y + speed - j - 1];
                        }
                        break;
                    case DOWN:
                        tmp = cells[x][y - speed];
                        for (int j = 0; j < speed; j++) {
                            tmp.setNextCell(iD, cells[x][y - speed + j + 1]);
                            cells[x][y - speed + j + 1].setPrevCell(iD, tmp);
                            tmp = cells[x][y - speed + j + 1];
                        }
                        break;
                    case LEFT:
                        tmp = cells[x + speed][y];
                        for (int j = 0; j < speed; j++) {
                            tmp.setNextCell(iD, cells[x + speed - j - 1][y]);
                            cells[x + speed - j - 1][y].setPrevCell(iD, tmp);
                            tmp = cells[x + speed - j - 1][y];
                        }
                        break;
                    case RIGHT:
                        tmp = cells[x - speed][y];
                        for (int j = 0; j < speed; j++) {
                            tmp.setNextCell(iD, cells[x - speed + j + 1][y]);
                            cells[x - speed + j + 1][y].setPrevCell(iD, tmp);
                            tmp = cells[x - speed + j + 1][y];
                        }
                        break;
                }
            }
        }
        boardAnalyzer.analyze();
        String log = "";
        for (int i = 0; i< cells.length; i++) {
            for (int j=0; j< cells[1].length; j++) {
                log = log + cells[i][j].getRisks() + " ";
            }
            log = log + "\n";
        }
        System.out.println(log);
    }

    private void updateJumpingPlayer (Player player){
        Cell tmp;
        int x = player.getX();
        int y = player.getY();
        int speed = player.getSpeed();
        int iD = player.getId();
        switch (player.getDirection()) {
            case UP:
                tmp = cells[x][y + speed];
                for (int j = 0; j < speed; j++) {
                    if (j == 0 || j == speed - 1) {
                        tmp.setNextCell(iD, cells[x][y + speed - j - 1]);
                        cells[x][y + speed - j - 1].setPrevCell(iD, tmp);
                        tmp = cells[x][y + speed - j - 1];
                    } else {
                        tmp.setNextCell(iD, cells[x][y + speed - j - 1]);
                        cells[x][y + speed - j - 1].setPrevHoleCell(iD, tmp);
                        tmp = cells[x][y + speed - j - 1];
                    }
                }
                break;
            case DOWN:
                tmp = cells[x][y - speed];
                for (int j = 0; j < speed; j++) {
                    if (j == 0 || j == speed - 1) {

                        tmp.setNextCell(iD, cells[x][y - speed + j + 1]);
                        cells[x][y - speed + j + 1].setPrevCell(iD, tmp);
                        tmp = cells[x][y - speed + j + 1];
                    } else {
                        tmp.setNextCell(iD, cells[x][y - speed + j + 1]);
                        cells[x][y - speed + j + 1].setPrevHoleCell(iD, tmp);
                        tmp = cells[x][y - speed + j + 1];
                    }
                }
                break;
            case LEFT:
                tmp = cells[x + speed][y];
                for (int j = 0; j < speed; j++) {
                    if (j == 0 || j == speed - 1) {
                        tmp.setNextCell(iD, cells[x + speed - j - 1][y]);
                        cells[x + speed - j - 1][y].setPrevCell(iD, tmp);
                        tmp = cells[x + speed - j - 1][y];
                    } else {
                        tmp.setNextCell(iD, cells[x + speed - j - 1][y]);
                        cells[x + speed - j - 1][y].setPrevHoleCell(iD, tmp);
                        tmp = cells[x + speed - j - 1][y];

                    }
                }
                break;
            case RIGHT:
                tmp = cells[x - speed][y];
                for (int j = 0; j < speed; j++) {
                    if (j == 0 || j == speed - 1) {
                        tmp.setNextCell(iD, cells[x - speed + j + 1][y]);
                        cells[x - speed + j + 1][y].setPrevCell(iD, tmp);
                        tmp = cells[x - speed + j + 1][y];

                } else {
                        tmp.setNextCell(iD, cells[x - speed + j + 1][y]);
                        cells[x - speed + j + 1][y].setPrevHoleCell(iD, tmp);
                        tmp = cells[x - speed + j + 1][y];
                    }
                }
                break;
        }
    }

    public void prepareNextPhase() {
        boardAnalyzer.prepareNextPhase();
    }

    public Action getAction() {
        MoveCalculation moveCalculation = new MoveCalculation(cells, us, boardAnalyzer);
        return moveCalculation.calculateBestAction();
    }

    public Action startingStrategy() {
        //TODO: Implement this.
        return Action.CHANGE_NOTHING;
    }

    /**
     * Parse board based on json-format.
     * @param json json from websocket
     * @param players parsed players
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

        //Gson gson = new Gson();

        //log.debug(json.get("cells").toString());
        //int[][] cells = gson.fromJson(json.get("cells").toString(), int[][].class);

        EvaluationBoard evaluationBoard = new EvaluationBoard(width, height, playersArray, us, 0);

        return evaluationBoard;
    }
}
