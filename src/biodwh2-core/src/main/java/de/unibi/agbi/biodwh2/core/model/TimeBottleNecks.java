package de.unibi.agbi.biodwh2.core.model;


/**
 * Utility class for time-related helper methods.
 */
public final class TimeBottleNecks{
 
    // Prevent instantiation
    private TimeBottleNecks() {}
 
    /**
     * Formats a duration given in milliseconds into a human-readable string
     * of the form "X days, X hours, X minutes, X seconds".
     *
     * @param millis elapsed time in milliseconds
     * @return formatted duration string
     */
    public static String formatElapsed(final long millis) {
        final long seconds = millis / 1000;
        final long minutes = seconds / 60;
        final long hours   = minutes / 60;
        final long days    = hours   / 24;
 
        return String.format("%d days, %d hours, %d minutes, %d seconds",
            days,
            hours   % 24,
            minutes % 60,
            seconds % 60);
    }
}