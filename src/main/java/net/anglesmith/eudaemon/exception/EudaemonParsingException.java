package net.anglesmith.eudaemon.exception;

/**
 * Thrown when specific parsing tasks (like those used by the "roll" command) fail irrecoverably.
 */
public class EudaemonParsingException extends RuntimeException {
    public EudaemonParsingException(String message) {
        super(message);
    }

    public EudaemonParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
