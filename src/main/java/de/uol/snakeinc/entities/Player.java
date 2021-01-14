package de.uol.snakeinc.entities;

import lombok.CustomLog;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@CustomLog
public class Player {

    @Getter
    private int id;
    @Getter
    private int x;
    @Getter
    private int y;
    @Getter
    private Direction direction;
    @Getter
    private int speed;
    @Getter
    private boolean active;
    @Getter
    private String name;
    @Getter
    private int leftRightBalance;

    public Player(int id, int x, int y, Direction direction, int speed, boolean active, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.active = active;
        this.name = name;
        this.leftRightBalance = 0;
    }

    /**
     * updates the left right balance of the player.
     * @param action whether the payer moves left to right
     */
    public void updateLeftRightBalance(Action action) {
        switch (action) {
            case TURN_LEFT:
                if (leftRightBalance > -3) {
                    leftRightBalance--;
                }
                return;
            case TURN_RIGHT:
                if (leftRightBalance < 3) {
                    leftRightBalance++;
                }
                return;
            default:
                //return;
        }
    }

    /**
     * Parse players based on json-format.
     * @param json json-object send by websocket
     * @return List of players
     */
    public static List<Player> parseFromJson(JsonObject json) {
        List<Player> players = new ArrayList<Player>();
        boolean running = json.get("running").getAsBoolean();

        JsonObject jsonPlayers = json.get("players").getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entries = jsonPlayers.entrySet();
        for (Map.Entry<String, JsonElement> entry : entries) {
            int id = Integer.valueOf(entry.getKey());
            JsonObject player = jsonPlayers.get(entry.getKey()).getAsJsonObject();
            int x = player.get("x").getAsInt();
            int y = player.get("y").getAsInt();
            Direction direction = Direction.valueOf(player.get("direction").getAsString().toUpperCase());
            int speed = player.get("speed").getAsInt();
            boolean active = player.get("active").getAsBoolean();
            String name = "";
            if (!running) {
                name = player.get("name").getAsString();
            }
            players.add(new Player(id, x, y, direction, speed, active, name));
        }
        return players;
    }
}
