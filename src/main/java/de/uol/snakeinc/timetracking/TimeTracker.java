package de.uol.snakeinc.timetracking;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class TimeTracker {

    private long startTime;
    private long lastPause;
    private static final double NANOS_IN_A_MS = 1000000.0D;

    public TimeTracker() {
        this.startTime = System.nanoTime();
        this.lastPause = this.startTime;
    }

    /**
     * Log time for the current process.
     * @param information Information to the process
     */
    public void logTime(String information) {
        long time = System.nanoTime();
        double totalTime = (time - this.lastPause) / NANOS_IN_A_MS;
        log.info("Tracked " + totalTime + "ms - " + information);
        this.lastPause = time;
    }

    /**
     * Log final time for complete process between initializing TimeTracker and logging.
     */
    public void logFinal() {
        long time = System.nanoTime();
        double totalTime = (time - this.startTime) / NANOS_IN_A_MS;
        log.info("Finalized Process, time needed " + totalTime + "ms");
    }

}
