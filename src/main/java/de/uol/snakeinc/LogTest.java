package de.uol.snakeinc;

import lombok.CustomLog;

@CustomLog
public class LogTest {

    /**
     * Main-Thread of Program.
     * @param args Arguments send via console
     */
    public static void main(String[] args) {
        log.ai("test ai");
        log.debug("test log");
    }
}
