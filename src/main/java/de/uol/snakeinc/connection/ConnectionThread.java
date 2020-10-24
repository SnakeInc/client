package de.uol.snakeinc.connection;

import lombok.CustomLog;

import java.net.URI;
import java.net.URISyntaxException;

@CustomLog
public class ConnectionThread extends Thread {

    private SpeedWebSocketClient webSocket;
    private URI url;
    private boolean running;
    private boolean callback;

    public ConnectionThread(String apiKey) {
        this.running = true;
        try {
            url = new URI("wss://msoll.de/spe_ed?key=" + apiKey);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running) {
            callback = false;
            webSocket = new SpeedWebSocketClient(this, url);
            webSocket.connect();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (webSocket.isOpen()) {
            }
        }
    }

    public SpeedWebSocketClient getWebSocket() {
        return this.webSocket;
    }

    public void callBack() {
        this.callback = true;
    }
}
