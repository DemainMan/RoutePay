package za.co.routepay.momo.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration properties for the MTN MoMo API.
 *
 * <p>Set via {@code momo.*} properties in application.yml or environment variables.
 * When {@code environment} is {@code MOCK}, all API calls return realistic fake responses
 * without hitting any external server.</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoMoProperties {

    /** API environment: MOCK (default), SANDBOX, or PRODUCTION. */
    @Builder.Default
    private MoMoEnvironment environment = MoMoEnvironment.MOCK;

    /** MTN MoMo subscription key for the target environment. */
    private String subscriptionKey;

    /** API User UUID (created via /apiuser endpoint). */
    private String apiUser;

    /** API Key (returned when creating an API user). */
    private String apiKey;

    /** Primary key for Ocp-Apim-Subscription-Key header. */
    private String primaryKey;

    /** Callback URL for async notifications from MoMo. */
    private String callbackUrl;

    /** Base URL override — if null, derived from environment. */
    private String baseUrl;
}
