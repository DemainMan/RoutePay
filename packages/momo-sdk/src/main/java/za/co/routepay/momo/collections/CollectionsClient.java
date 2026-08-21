package za.co.routepay.momo.collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import za.co.routepay.momo.collections.dto.PaymentRequest;
import za.co.routepay.momo.collections.dto.PaymentResponse;
import za.co.routepay.momo.config.MoMoProperties;
import za.co.routepay.momo.exception.MoMoApiException;
import za.co.routepay.momo.exception.MoMoConnectionException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

/**
 * Client for the MTN MoMo Collections API.
 *
 * <p>Handles fare payment collection from commuters. Each requestToPay call
 * initiates a push payment to the specified phone number.</p>
 */
@Slf4j
public class CollectionsClient {

    private final MoMoProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CollectionsClient(MoMoProperties properties, HttpClient httpClient, ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Request a payment from a commuter's MoMo wallet.
     *
     * @param request the payment request containing amount, phone, and reference
     * @return payment response with transaction reference and status
     */
    public PaymentResponse requestToPay(PaymentRequest request) {
        log.info("🚀 Collections: requestToPay — {} {} to {} (ref: {})",
                request.getAmount(), request.getCurrency(), request.getPhone(), request.getReference());

        String referenceId = UUID.randomUUID().toString();

        try {
            String json = objectMapper.writeValueAsString(request);
            String url = properties.getBaseUrl() + "/collection/v1_0/requesttopay/" + referenceId;

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("X-Reference-Id", referenceId)
                    .header("X-Target-Environment", properties.getEnvironment().name().toLowerCase())
                    .header("Ocp-Apim-Subscription-Key", properties.getSubscriptionKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("✅ Collections: payment initiated — ref: {}, status: PENDING", referenceId);
                return PaymentResponse.builder()
                        .referenceId(referenceId)
                        .status("PENDING")
                        .build();
            } else {
                log.error("❌ Collections: payment failed — status: {}, body: {}", response.statusCode(), response.body());
                throw new MoMoApiException("Collections requestToPay failed",
                        response.statusCode(), response.body());
            }
        } catch (MoMoApiException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MoMoConnectionException("Collections request interrupted", e);
        } catch (java.io.IOException e) {
            log.error("❌ Collections: connection error — {}", e.getMessage());
            throw new MoMoConnectionException("Failed to connect to MoMo Collections API", e);
        } catch (RuntimeException e) {
            log.error("❌ Collections: unexpected error — {}", e.getMessage());
            throw new MoMoApiException("Unexpected error in Collections API", 500, e.getMessage());
        }
    }
}
