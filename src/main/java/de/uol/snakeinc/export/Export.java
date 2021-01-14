package de.uol.snakeinc.export;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.uol.snakeinc.entities.Game;
import de.uol.snakeinc.entities.Player;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Log4j2
public class Export {

    private Game game;

    public Export(Game game) {
        this.game = game;
    }

    /**
     * Generate Javadoc-File.
     */
    public void generateFile() {
        log.info("Writing log for " + game.getGameId());
        Gson gson = new Gson();

        JsonObject objects = new JsonObject();
        objects.addProperty("rounds", game.getRounds());
        objects.addProperty("date", new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date()));

        objects.addProperty("us", game.getUs().getId());
        HashMap<Integer, String> players = new HashMap<Integer, String>();
        for (Player player : new ArrayList<Player>(game.getPlayers().values())) {
            players.put(player.getId(), player.getName());
        }
        objects.add("players", gson.toJsonTree(players));

        HashMap<Integer, Integer[][]> boards = new HashMap<Integer, Integer[][]>();
        HashMap<Integer, int[][]> dataBoards = game.getBoards();
        for (Integer position : dataBoards.keySet()) {
            int[][] evaluationBoard = dataBoards.get(position);
            Integer[][] positions = new Integer[evaluationBoard.length][evaluationBoard[0].length];
            for (int x = 0; x < evaluationBoard.length; x++) {
                for (int z = 0; z < evaluationBoard[x].length; z++) {
                    positions[x][z] = Integer.valueOf(evaluationBoard[x][z]);
                }
            }
            boards.put(position, positions);
        }
        JsonObject mapObjects = new JsonObject();
        JsonElement jsonElement = gson.toJsonTree(boards);
        mapObjects.addProperty("width", game.getBoards().get(1).length);
        mapObjects.addProperty("height", game.getBoards().get(1)[0].length);
        mapObjects.add("boards", jsonElement);
        objects.add("map", mapObjects);

        String json = gson.toJson(objects);

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            File file = new File("logs/" + formatter.format(date), game.getGameId() + ".json");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(json);
            fileWriter.close();
        } catch (IOException exception) {
            log.error("An error occurred while saving the file.");
            exception.printStackTrace();
        }
    }

}
