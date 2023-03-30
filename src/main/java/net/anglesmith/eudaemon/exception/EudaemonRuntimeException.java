package net.anglesmith.eudaemon.exception;

/**
 * Thrown when fulfilling a command-related task is impossible for any generic reason.
 */
public class EudaemonRuntimeException extends RuntimeException {
    public EudaemonRuntimeException(String message) {
        super(message);
    }

    public EudaemonRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
