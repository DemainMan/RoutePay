package za.co.routepay.momo.exception;

/**
 * Thrown when MoMo authentication or token refresh fails.
 */
public class MoMoAuthException extends RuntimeException {

    public MoMoAuthException(String message) {
        super(message);
    }

    public MoMoAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
