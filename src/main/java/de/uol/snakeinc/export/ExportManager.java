package de.uol.snakeinc.export;

import de.uol.snakeinc.entities.Game;

public class ExportManager {

    /**
     * Generate Export for a Game.
     * @param game game to export
     */
    public void generateExport(Game game) {
        Export export = new Export(this, game);
        export.generateFile();
    }

}
