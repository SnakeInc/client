package de.uol.snakeinc.connection;

import de.uol.snakeinc.Config;
import de.uol.snakeinc.SnakeInc;
import lombok.extern.log4j.Log4j2;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

/**
 * Thread handling Connection to Websocket.
 * @author Sebastian Diers
 */
@Log4j2
public class ConnectionThread extends Thread {

    private SpeedWebSocketClient webSocket;
    private URI url;
    private boolean running;

    public ConnectionThread(String apiKey) {
        this.running = true;
        while (!SnakeInc.isGuiReady()) {
            try {
                log.info("Waiting for GUI");
                Thread.sleep(Config.SLEEP_MILLIS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            url = new URI(SnakeInc.getURL() + "?key=" + apiKey);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        log.debug("Starting Connection-Thread");
        while (running) {
            webSocket = new SpeedWebSocketClient(this, url);
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("TLS");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                sslContext.init( null, null, null );
            } catch (NullPointerException | KeyManagementException e) {
                e.printStackTrace();
            }
            SSLSocketFactory factory = sslContext.getSocketFactory();
            webSocket.setSocketFactory(factory);
            webSocket.connect();
            while (webSocket.isOpen() || !this.webSocket.isStopped()) {
               //Busy Waiting
            }
        }
    }

    /**
     * Stop connection with socket.
     */
    public void stopConnection() {
        this.running = false;
        this.webSocket.close();
    }
}
