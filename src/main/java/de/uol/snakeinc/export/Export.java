package de.uol.snakeinc.export;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.uol.snakeinc.entities.Board;
import de.uol.snakeinc.entities.Game;
import de.uol.snakeinc.entities.Player;
import lombok.CustomLog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@CustomLog
public class Export {

    private ExportManager manager;
    private Game game;

    public Export(ExportManager manager, Game game) {
        this.manager = manager;
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
        HashMap<Integer, Board> dataBoards = game.getBoards();
        for (Integer position : dataBoards.keySet()) {
            Board board = dataBoards.get(position);
            Integer[][] positions = new Integer[board.getHeight()][board.getWidth()];
            for (int x = 0; x < board.getCells().length; x++) {
                for (int z = 0; z < board.getCells()[x].length; z++) {
                    positions[x][z] = Integer.valueOf(board.getCells()[x][z]);
                }
            }
            boards.put(position, positions);
        }
        JsonObject mapObjects = new JsonObject();
        JsonElement jsonElement = gson.toJsonTree(boards);
        mapObjects.addProperty("width", game.getCurrentBoard().getWidth());
        mapObjects.addProperty("height", game.getCurrentBoard().getHeight());
        mapObjects.add("boards", jsonElement);
        objects.add("map", mapObjects);

        String json = gson.toJson(objects);

        try {
            File file = new File("logs", game.getGameId() + ".json");
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
