package ca.bazlur.scopevalue;

public class MutableLoggingContext {
    // A ThreadLocal holding the current log level (e.g., "INFO", "DEBUG", etc.)
    private static final ThreadLocal<String> LOG_LEVEL = new ThreadLocal<>();

    public static void setLogLevel(String level) {
        LOG_LEVEL.set(level);
    }

    public static String getLogLevel() {
        return LOG_LEVEL.get();
    }

    // Simulated logging method
    public static void log(String message) {
        System.out.println("[" + getLogLevel() + "] " + message);
    }

    public static void main(String[] args) {
        setLogLevel("INFO");
        log("Starting process...");

        // Another part of the code can abruptly change the log level
        new Thread(() -> {
            setLogLevel("DEBUG");
            log("Thread-specific debug mode enabled");
        }).start();

        // Meanwhile, this thread might still assume it's "INFO"
        log("Continuing with INFO level...");
    }
}
