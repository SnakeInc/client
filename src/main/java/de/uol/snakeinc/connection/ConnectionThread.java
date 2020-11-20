package de.uol.snakeinc.connection;

import de.uol.snakeinc.export.ExportManager;
import lombok.CustomLog;

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
@CustomLog
public class ConnectionThread extends Thread {

    private SpeedWebSocketClient webSocket;
    private URI url;
    private boolean running;
    private boolean callback;

    private ExportManager exportManager;

    public ConnectionThread(String apiKey) {
        this.running = true;
        this.exportManager = new ExportManager();
        try {
            //wss://msoll.de/spe_ed?key=
            url = new URI("wss://msoll.de/spe_ed?key=" + apiKey);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        log.debug("Starting Connection-Thread");
        while (running) {
            callback = false;
            webSocket = new SpeedWebSocketClient(this, url, exportManager);
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("TLS");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                sslContext.init( null, null, null );
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            SSLSocketFactory factory = sslContext.getSocketFactory();
            webSocket.setSocketFactory(factory);
            webSocket.connect();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (webSocket.isOpen() || this.callback == false) {
            }
        }
    }

    public SpeedWebSocketClient getWebSocket() {
        return this.webSocket;
    }

    public void callBack() {
        this.callback = true;
    }

    /**
     * Stop connection with socket.
     */
    public void stopConnection() {
        this.running = false;
        this.webSocket.close();
    }
}
