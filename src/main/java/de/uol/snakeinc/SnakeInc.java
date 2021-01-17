package de.uol.snakeinc;

import de.uol.snakeinc.connection.ConnectionThread;
import de.uol.snakeinc.gui.Gui;
import de.uol.snakeinc.gui.GuiThread;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SnakeInc {

    @Getter @Setter
    private static Gui gui;
    @Getter @Setter
    private static boolean guiReady = false;
    @Getter
    private static String URL = "wss://msoll.de/spe_ed";
    private static String APIKEY = "4J6JBGVMWOPZCSDJ273T4PLBFATDNTVSWUJ7BEB6C3EPMDXZVKDYLUUU";
    @Getter
    private static String TIME_URL = "https://msoll.de/spe_ed_time";

    /**
     *  Main-Thread of Program.
     * @param args Arguments send via console
     */
    public static void main(String[] args) {
        if (args.length == 0 || !args[0].contains("-nogui")) {
            GuiThread guiThread = new GuiThread(args);
            guiThread.start();
        } else {
            SnakeInc.setGuiReady(true);
        }

        if(System.getenv("URL") != null) {
            URL = System.getenv("URL");
        }
        if(System.getenv("APIKEY") != null) {
            APIKEY = System.getenv("APIKEY");
        }
        if(System.getenv("TIME_URL") != null) {
            TIME_URL = System.getenv("TIME_URL");
        }

        ConnectionThread thread = new ConnectionThread(APIKEY);
        thread.start();
    }

    public static boolean hasGui() {
        return SnakeInc.gui != null;
    }
}
