package de.uol.snakeinc;

import de.uol.snakeinc.connection.ConnectionThread;
import lombok.CustomLog;

@CustomLog
public class SnakeInc {

    /**
     *  Main-Thread of Program.
     * @param args Arguments send via console
     */
    public static void main(String[] args) {
        String apiKey = "4J6JBGVMWOPZCSDJ273T4PLBFATDNTVSWUJ7BEB6C3EPMDXZVKDYLUUU";
        ConnectionThread thread = new ConnectionThread(apiKey);
        thread.start();
    }
}
