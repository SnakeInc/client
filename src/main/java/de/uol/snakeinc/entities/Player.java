package de.uol.snakeinc.entities;

import lombok.EqualsAndHashCode;
import lombok.CustomLog;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EqualsAndHashCode
@CustomLog
public class Player {

    @Getter @Setter
    private int id;
    @Getter @Setter
    private int x;
    @Getter @Setter
    private int y;
    @Getter @Setter
    private Direction direction;
    @Getter @Setter
    private int speed;
    @Getter @Setter
    private boolean active;
    @Getter @Setter @EqualsAndHashCode.Exclude
    private String name;


    static Player get() {
        return new Player();
    }

    private Player()
    { }


    public Player(int id, int x, int y, Direction direction, int speed, boolean active, String name) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.speed = speed;
        this.active = active;
        this.name = name;
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
