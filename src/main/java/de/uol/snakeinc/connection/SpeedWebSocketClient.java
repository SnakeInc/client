package de.uol.snakeinc.connection;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.uol.snakeinc.Config;
import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.EvaluationBoard;
import de.uol.snakeinc.entities.Game;
import de.uol.snakeinc.entities.Player;
import de.uol.snakeinc.export.Export;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.digest.DigestUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;

/**
 * Websocket for Spe_ed - the game.
 * @author Sebastian Diers
 */
@Log4j2
public class SpeedWebSocketClient extends WebSocketClient {

    private ConnectionThread thread;
    private Game game;
    private String serverId;
    private boolean initialMessage = true;
    @Getter
    private boolean stopped = false;
    private Gson gson;

    public SpeedWebSocketClient(ConnectionThread thread, URI url) {
        super(url);
        this.gson = new Gson();
        this.thread = thread;
        //CHECKSTYLE:OFF
        this.serverId = DigestUtils.md5Hex(url.getHost()).substring(0, 4);
        //CHECKSTYLE:ON
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("Connection established");
        this.game = new Game(serverId);
        this.initialMessage = true;
    }

    @Override
    public void onMessage(String message) {
        //log.info("Received message: " + message);
        try {
            JsonElement jsonTree = JsonParser.parseString(message);
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            for (Player player : Player.parseFromJson(jsonObject)) {
                game.addPlayer(player);
            }
            game.setUs(jsonObject.get("you").getAsInt());
            if (initialMessage) {
                initialMessage = false;
                game.informIntelligentBoard(EvaluationBoard
                    .initParseFromJson(jsonObject, game.getPlayers(), game.getUs()), getRawBoard(jsonObject));
            } else {
                game.informIntelligentBoard(getRawBoard(jsonObject));
            }
            game.runAction(this);
        } catch (Exception exception) {
            exception.printStackTrace();
            this.thread.stopConnection();
        }
    }

    @Override
    public void onClose(int code, String message, boolean remote) {
        log.info("Connection closed: " + message);
        // Run Game-Logging
        if (this.game != null) {
            try {
                if (Config.LOG_WRITING_ENABLED) {
                    log.info("Logging game");
                    var export = new Export(game, gson);
                    export.generateFile();
                }
            } catch (Exception exception) {
                log.error("Error while logging the game");
                exception.printStackTrace();
                this.thread.stopConnection();
            }
        }
        this.stopped = true;
    }

    @Override
    public void onError(Exception exception) {
        log.info("Got an exception: " + exception.getMessage());
        exception.printStackTrace();
    }

    /**
     * Send Playing-Action to websocket.
     * @param action Playing-Action for next turn
     */
    public void sendAction(Action action) {
        HashMap<String, String> json = new HashMap<String, String>();
        json.put("action", action.toString().toLowerCase());

        this.send(gson.toJson(json));
        log.info(gson.toJson(json));
    }

    private int[][] getRawBoard(JsonObject json) {
        return gson.fromJson(json.get("cells").toString(), int[][].class);
    }
}