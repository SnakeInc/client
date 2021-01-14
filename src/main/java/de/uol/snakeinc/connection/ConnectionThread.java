package de.uol.snakeinc.connection;

import de.uol.snakeinc.SnakeInc;
import de.uol.snakeinc.export.ExportManager;
import lombok.CustomLog;
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

    private ExportManager exportManager;

    public ConnectionThread(String apiKey) {
        this.running = true;
        this.exportManager = new ExportManager();
        while (!SnakeInc.isGuiReady()) {
            try {
                log.info("Waiting for GUI");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            //wss://msoll.de/spe_ed?key=
            url = new URI("wss://msoll.de/spe_ed?key=" + apiKey);
            //url = new URI("wss://yellowphoenix18.de:554/Joost");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        log.debug("Starting Connection-Thread");
        while (running) {
            webSocket = new SpeedWebSocketClient(this, url, exportManager);
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("TLS");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                sslContext.init( null, null, null );
            } catch (KeyManagementException | NullPointerException e) {
                e.printStackTrace();
            }
            SSLSocketFactory factory = sslContext.getSocketFactory();
            webSocket.setSocketFactory(factory);
            webSocket.connect();
            while (webSocket.isOpen() || !this.webSocket.isStopped()) {
                //todo: should this be empty
            }
        }
    }

    public SpeedWebSocketClient getWebSocket() {
        return this.webSocket;
    }

    /**
     * Stop connection with socket.
     */
    public void stopConnection() {
        this.running = false;
        this.webSocket.close();
    }
}
