package de.uol.snakeinc.entities;

import lombok.CustomLog;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.HashMap;
import java.util.Set;

@CustomLog
public class IntelligentBoard {

    @Getter
    private int width;
    @Getter
    private int height;
    @Getter
    private Player us;

    private BoardAnalyzer boardAnalyzer;

    private Set<Tupel> neighbourCells;


    @Getter
    private Cell cells[][];

    private Player players[];

    public IntelligentBoard(int width, int height, Player[] players, Player us) {
        this.width = width;
        this.height = height;
        this.players = players;
        this.us = us;

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cells = new Cell[i][j];
            }
        }

        for (Player player : players) {
            if (player.getX() >= 0 && player.getX() < width &&
                player.getY() >= 0 && player.getY() < height) {
                cells[player.getX()][player.getY()].isHead(player.getId(), cells[player.getX()][player.getY()]);
            }
        }
        boardAnalyzer = new BoardAnalyzer(cells, players, us);
    }

    public void update(HashMap<Integer, Player> playerHashMap) {
        Player[] playersArray = new Player[playerHashMap.size()];
        int count = 0;
        for (Integer position : playerHashMap.keySet()) {
            playersArray[count] = playerHashMap.get(position);
            count++;
        }
        players = playersArray;
        for (int i=0; i < players.length; i++) {
            int iD = players[i].getId();
            int x = players[i].getX();
            int y = players[i].getY();
            int speed = players[i].getSpeed();
            Cell tmp;
                switch (players[i].getDirection()) {
                    case UP:
                        tmp = cells[x][y];
                        for (int j = 0; j< speed; j++) {
                            tmp.isTail(iD, cells[x][y - 1 - j]);
                            cells[x][y - 1 - j].isHead(iD, tmp);
                            tmp = cells[x][y - j];
                        }
                        break;
                    case DOWN:
                        tmp = cells[x][y];
                        for (int j = 0; j< speed; j++) {
                            tmp.isTail(iD, cells[x][y + 1 + j]);
                            cells[x][y + 1 +j].isHead(iD, tmp);
                            tmp = cells[x][y + j];
                        }
                        break;
                    case LEFT:
                        tmp = cells[x][y];
                        for (int j = 0; j< speed; j++) {
                            tmp.isTail(iD, cells[x- 1 - j][y]);
                            cells[x -1 - j][y].isHead(iD, tmp);
                            tmp = cells[x - j][y];
                        }
                        break;
                    case RIGHT:
                        tmp = cells[x][y];
                        for (int j = 0; j< speed; j++) {
                            tmp.isTail(iD, cells[x + 1 + j][y]);
                            cells[x + 1 + j][y].isHead(iD, tmp);
                            tmp = cells[x + j][y];
                        }
                        break;
                }
        }
        boardAnalyzer.analyze(cells,players);
    }



    /**
     * Parse board based on json-format.
     * @param json json from websocket
     * @param players parsed players
     * @return parsed board
     */
    public static IntelligentBoard initParseFromJson(JsonObject json, HashMap<Integer, Player> players, Player us) {
        int width = json.get("width").getAsInt();
        int height = json.get("height").getAsInt();

        Player[] playersArray = new Player[players.size()];
        int count = 0;
        for (Integer position : players.keySet()) {
            playersArray[count] = players.get(position);
            count++;
        }

        Gson gson = new Gson();

        log.debug(json.get("cells").toString());
        int[][] cells = gson.fromJson(json.get("cells").toString(), int[][].class);

        IntelligentBoard intelligentBoard = new IntelligentBoard(width, height, playersArray, us);

        return intelligentBoard;
    }
}
