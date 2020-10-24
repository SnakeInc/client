package de.uol.snakeinc.connection;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.uol.snakeinc.entities.Action;
import de.uol.snakeinc.entities.Board;
import de.uol.snakeinc.entities.Game;
import de.uol.snakeinc.entities.Player;
import lombok.extern.log4j.Log4j2;
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

    public SpeedWebSocketClient(ConnectionThread thread, URI url) {
        super(url);
        this.thread = thread;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("Connection established");
        this.thread.callBack();
        this.game = new Game();
    }

    @Override
    public void onMessage(String message) {
        log.info("Received message: " + message);
        try {
            JsonElement jsonTree = JsonParser.parseString(message);
            JsonObject jsonObject = jsonTree.getAsJsonObject();
            for (Player player : Player.parseFromJson(jsonObject)) {
                game.addPlayer(player);
            }
            game.setCurrentBoard(Board.parseFromJson(jsonObject, game.getPlayers()));
            game.setUs(jsonObject.get("you").getAsInt());
            game.runAction(this);
        } catch (Exception exception) {
            exception.printStackTrace();
            this.thread.stopConnection();
        }
    }

    @Override
    public void onClose(int code, String message, boolean remote) {
        this.thread.callBack();
        log.info("Connection closed: " + message);
        // Run Game-Logging
    }

    @Override
    public void onError(Exception exception) {
        log.info("Got an exception: " + exception.getMessage());
        this.thread.callBack();
    }

    /**
     * Send Playing-Action to websocket.
     * @param action Playing-Action for next turn
     */
    public void sendAction(Action action) {
        HashMap<String, String> json = new HashMap<String, String>();
        json.put("action", action.toString().toLowerCase());

        Gson gson = new Gson();

        this.send(gson.toJson(json));
    }
}