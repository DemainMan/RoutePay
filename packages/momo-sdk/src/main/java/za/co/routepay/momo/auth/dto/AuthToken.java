package za.co.routepay.momo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response from OTP verification — contains the access token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken {

    /** JWT access token. */
    private String accessToken;

    /** Token type (always "Bearer"). */
    @Builder.Default
    private String tokenType = "Bearer";

    /** Time-to-live in seconds. */
    private int expiresIn;

    /** The phone number this token is issued for. */
    private String phone;

    /** Whether this was created by the mock backend. */
    @Builder.Default
    private boolean mock = false;
}
