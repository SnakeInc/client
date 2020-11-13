package de.uol.snakeinc.entities;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.uol.snakeinc.possibleMoves.ActionPlayerCoordinates;
import lombok.CustomLog;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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

    public Player(Player player, boolean active) {
        this.id = player.id;
        this.x = player.x;
        this.y = player.y;
        this.direction = player.direction;
        this.speed = player.speed;
        this.active = active;
        this.name = player.name;
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

    public ArrayList<ActionPlayerCoordinates> getPossibleMoves(int height, int width, MapCoordinateBag map) {
        // TODO jumpturns
        var res = new ArrayList<ActionPlayerCoordinates>(5);
        Direction dir;
        int speed;

        for (Action action : Action.values()) {
            switch (action) {
                case TURN_LEFT:
                case TURN_RIGHT:
                    dir = direction.change(action);
                    speed = this.speed;
                    break;
                case SLOW_DOWN:
                    if (this.speed == 1) {
                        var apc = new ActionPlayerCoordinates(Action.SLOW_DOWN, new Player(this, false),
                            new ArrayList<>(0));
                        res.add(apc);
                        continue;
                    }
                    speed = this.speed - 1;
                    dir = this.direction;
                    break;
                case SPEED_UP:
                    if (this.speed == 10) {

                        var apc = new ActionPlayerCoordinates(Action.SPEED_UP, new Player(this, false),
                            new ArrayList<>(0));
                        res.add(apc);
                        continue;
                    }
                    speed = this.speed + 1;
                    dir = this.direction;
                    break;
                case CHANGE_NOTHING:
                    speed = this.speed;
                    dir = this.direction;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + action);
            }
            var ls = new ArrayList<Coordinates>(speed);
            Player player;
            int newOrdinate;
            boolean active;
            switch (dir) {
                case LEFT:
                    newOrdinate = x - speed;
                    active = -1 < newOrdinate;
                    player = new Player(this.id, newOrdinate, y, Direction.LEFT, speed, active, name);
                    if (!active) {
                        newOrdinate = 0;
                    }
                    for (; newOrdinate < x; newOrdinate++) {
                        var tuple = new Coordinates(newOrdinate, y, id);
                        if (!map.contains(tuple)) {
                            ls.add(tuple);
                        } else {
                            player.setActive(false);
                        }
                    }
                    break;
                case RIGHT:
                    newOrdinate = x + speed;
                    active = width >= newOrdinate;
                    player = new Player(this.id, newOrdinate, y, Direction.LEFT, speed, active, name);
                    if (!active) {
                        newOrdinate = width - 1;
                    }
                    for (; newOrdinate > x; newOrdinate--) {
                        var tuple = new Coordinates(newOrdinate, y, id);
                        if (!map.contains(tuple)) {
                            ls.add(tuple);
                        } else {
                            player.setActive(false);
                        }
                    }
                    break;
                case DOWN:
                    newOrdinate = y - speed;
                    active = -1 < newOrdinate;
                    player = new Player(this.id, x, newOrdinate, Direction.LEFT, speed, active, name);
                    if (!active) {
                        newOrdinate = 0;
                    }
                    for (; newOrdinate < y; newOrdinate++) {
                        var tuple = new Coordinates(x, newOrdinate, id);
                        if (!map.contains(tuple)) {
                            ls.add(tuple);
                        } else {
                            player.setActive(false);
                        }
                    }
                    break;
                case UP:
                    newOrdinate = y + speed;
                    active = newOrdinate >= height;
                    player = new Player(this.id, x, newOrdinate, Direction.LEFT, speed, active, name);
                    if (!active) {
                        newOrdinate = height - 1;
                    }
                    for (; newOrdinate > y; newOrdinate--) {
                        var tuple = new Coordinates(x, newOrdinate, id);
                        if (!map.contains(tuple)) {
                            ls.add(tuple);
                        } else {
                            player.setActive(false);
                        }
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + dir);
            }
            res.add(new ActionPlayerCoordinates(action, player, ls));
        }
        return res;
    }
}
