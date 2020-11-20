package de.uol.snakeinc.possibleMoves;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import de.uol.snakeinc.entities.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class possibleMovesCalculation2Test {

    public Game getGame(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file, "UTF-8");
        String json = scanner.useDelimiter("\\A").next();
        scanner.close();

        Gson gson = new Gson();
        JsonObject jsonData = gson.fromJson(json, JsonObject.class);

        int rounds = jsonData.get("rounds").getAsInt();

        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> playerData = gson.fromJson(jsonData.get("players"), type);
        HashMap<Integer, String> players = new HashMap<Integer, String>();
        for (String player : playerData.keySet()) {
            players.put(Integer.valueOf(player), playerData.get(player));
        }

        int us = jsonData.get("us").getAsInt();

        JsonObject map = jsonData.get("map").getAsJsonObject();
        int width = map.get("width").getAsInt();
        int height = map.get("height").getAsInt();

        Type typeBoard = new TypeToken<Map<String, Integer[][]>>() {
        }.getType();
        Map<String, Integer[][]> boardData = gson.fromJson(map.get("boards"), typeBoard);

        HashMap<Integer, Integer[][]> boards = new HashMap<Integer, Integer[][]>();
        for (String board : boardData.keySet()) {
            boards.put(Integer.valueOf(board), boardData.get(board));
        }

        players.put(us, "SnakeInc");

        return new Game(rounds, players, us, width, height, boards);
    }

    public class Game {

        private int rounds;
        private HashMap<Integer, String> players;
        private PlayerMap playerMap;
        private int us;

        private int width;
        private int height;

        private HashMap<Integer, Integer[][]> boards;

        public Game(int rounds, HashMap<Integer, String> players, int us, int width, int height, HashMap<Integer, Integer[][]> boards) {
            this.rounds = rounds;
            this.players = players;
            this.playerMap = new PlayerMap(players.size());

            var firstboard = boards.get(1);

            for (var entry : players.entrySet()) {
                var key = (int) entry.getKey();
                var value = entry.getValue();

                int x = 0;
                int y = 0;
                outer:
                for (; x < firstboard.length; x++) {
                    for (; y < firstboard[y].length; y++) {
                        if (firstboard[x][y] == key) {
                            break outer;
                        }
                    }
                    y = 0;
                }

                playerMap.put(new Player())
            }
            this.us = us;

            this.width = width;
            this.height = height;
            this.boards = boards;
        }

        public int getRounds() {
            return this.rounds;
        }

        public HashMap<Integer, String> getPlayers() {
            return this.players;
        }

        public int getUs() {
            return this.us;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public HashMap<Integer, Integer[][]> getBoards() {
            return this.boards;
        }

    }

}