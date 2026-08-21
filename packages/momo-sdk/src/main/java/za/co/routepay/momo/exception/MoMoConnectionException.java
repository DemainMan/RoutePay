package za.co.routepay.momo.exception;

/**
 * Thrown when the SDK cannot connect to the MoMo API endpoint.
 */
public class MoMoConnectionException extends RuntimeException {

    public MoMoConnectionException(String message) {
        super(message);
    }

    public MoMoConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
