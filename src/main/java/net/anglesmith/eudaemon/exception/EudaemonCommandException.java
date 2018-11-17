package net.anglesmith.eudaemon.exception;

/**
 * Thrown by Eudaemon commands when their <code>execute</code> methods fail.
 */
public class EudaemonCommandException extends Exception {
    public EudaemonCommandException(String message) {
        super(message);
    }

    public EudaemonCommandException(String message, Throwable cause) {
        super(message, cause);
    }
}
