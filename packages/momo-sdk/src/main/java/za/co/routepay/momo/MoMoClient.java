package za.co.routepay.momo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import za.co.routepay.momo.auth.AuthClient;
import za.co.routepay.momo.collections.CollectionsClient;
import za.co.routepay.momo.config.MoMoEnvironment;
import za.co.routepay.momo.config.MoMoProperties;
import za.co.routepay.momo.disbursements.DisbursementsClient;
import za.co.routepay.momo.exception.MoMoApiException;
import za.co.routepay.momo.exception.MoMoAuthException;
import za.co.routepay.momo.exception.MoMoConnectionException;
import za.co.routepay.momo.mock.MockMoMoBackend;
import za.co.routepay.momo.payments.PaymentsClient;
import za.co.routepay.momo.remittances.RemittancesClient;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Main entry point for the MoMo SDK.
 *
 * <p>Usage:</p>
 * <pre>{@code
 * MoMoClient client = MoMoClient.builder()
 *     .subscriptionKey("your-key")
 *     .environment(MoMoEnvironment.SANDBOX)
 *     .build();
 *
 * PaymentResponse response = client.collections()
 *     .requestToPay(PaymentRequest.builder()
 *         .amount(new BigDecimal("15.00"))
 *         .phone("+27821234567")
 *         .reference("trip-123")
 *         .build());
 * }</pre>
 */
@Slf4j
@Getter
public class MoMoClient {

    private final MoMoProperties properties;
    private final CollectionsClient collections;
    private final DisbursementsClient disbursements;
    private final RemittancesClient remittances;
    private final PaymentsClient payments;
    private final AuthClient auth;
    private final MockMoMoBackend mockBackend;

    private MoMoClient(Builder builder) {
        this.properties = MoMoProperties.builder()
                .environment(builder.environment)
                .subscriptionKey(builder.subscriptionKey)
                .apiUser(builder.apiUser)
                .apiKey(builder.apiKey)
                .primaryKey(builder.primaryKey)
                .callbackUrl(builder.callbackUrl)
                .baseUrl(builder.baseUrl)
                .build();

        if (this.properties.getBaseUrl() == null) {
            this.properties.setBaseUrl(this.properties.getEnvironment().getBaseUrl());
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        this.collections = new CollectionsClient(properties, httpClient, objectMapper);
        this.disbursements = new DisbursementsClient(properties, httpClient, objectMapper);
        this.remittances = new RemittancesClient(properties, httpClient, objectMapper);
        this.payments = new PaymentsClient(properties, httpClient, objectMapper);
        this.auth = new AuthClient(properties, httpClient, objectMapper);
        this.mockBackend = new MockMoMoBackend();

        log.info("MoMoClient initialized — env={}, baseUrl={}",
                properties.getEnvironment(), properties.getBaseUrl());
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private MoMoEnvironment environment = MoMoEnvironment.MOCK;
        private String subscriptionKey;
        private String apiUser;
        private String apiKey;
        private String primaryKey;
        private String callbackUrl;
        private String baseUrl;

        public Builder environment(MoMoEnvironment environment) {
            this.environment = environment;
            return this;
        }

        public Builder subscriptionKey(String subscriptionKey) {
            this.subscriptionKey = subscriptionKey;
            return this;
        }

        public Builder apiUser(String apiUser) {
            this.apiUser = apiUser;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder primaryKey(String primaryKey) {
            this.primaryKey = primaryKey;
            return this;
        }

        public Builder callbackUrl(String callbackUrl) {
            this.callbackUrl = callbackUrl;
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
            return this;
        }

        public MoMoClient build() {
            return new MoMoClient(this);
        }
    }
}
