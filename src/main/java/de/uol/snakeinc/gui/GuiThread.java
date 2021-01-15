package de.uol.snakeinc.gui;

public class GuiThread extends Thread {

    private String[] args;

    public GuiThread(String[] args) {
        this.args = args;
    }

    public void run() {
        Gui.main(args);
    }

}
