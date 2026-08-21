package za.co.routepay.momo.exception;

import lombok.Getter;

/**
 * Thrown when the MoMo API returns a non-success HTTP status.
 */
@Getter
public class MoMoApiException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;

    public MoMoApiException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public MoMoApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.responseBody = null;
    }
}
