package top.niunaijun.blackreflection;

/**
 * Thrown when a required reflected member is not found.
 */
public class BlackNullPointerException extends RuntimeException {
    public BlackNullPointerException(String message) {
        super(message);
    }

    public BlackNullPointerException(String message, Throwable cause) {
        super(message, cause);
    }
}
