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

        String apiKey = "4J6JBGVMWOPZCSDJ273T4PLBFATDNTVSWUJ7BEB6C3EPMDXZVKDYLUUU";
        ConnectionThread thread = new ConnectionThread(apiKey);
        thread.start();
        /*PathfindTester tester = new PathfindTester();
        tester.testPathfinding();*/
    }

    public static boolean hasGui() {
        return SnakeInc.gui != null;
    }
}
