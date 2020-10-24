package de.uol.snakeinc.connection;

import lombok.CustomLog;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

@CustomLog
public class SpeedWebSocketClient extends WebSocketClient {

    private ConnectionThread thread;

    public SpeedWebSocketClient(ConnectionThread thread, URI url) {
        super(url);
        this.thread = thread;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("Connection established");
        this.thread.callBack();
    }

    @Override
    public void onMessage(String message) {
        log.info("Received message: " + message);
    }

    @Override
    public void onClose(int code, String message, boolean remote) {
        this.thread.callBack();
        log.info("Connection closed: " + message);
    }

    @Override
    public void onError(Exception exception) {
        log.info("Got an exception: " + exception.getStackTrace().toString());
        this.thread.callBack();
    }
}