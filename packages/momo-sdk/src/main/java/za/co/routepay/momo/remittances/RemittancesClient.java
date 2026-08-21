package za.co.routepay.momo.remittances;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import za.co.routepay.momo.config.MoMoProperties;
import za.co.routepay.momo.exception.MoMoApiException;
import za.co.routepay.momo.exception.MoMoConnectionException;
import za.co.routepay.momo.remittances.dto.RemittanceRequest;
import za.co.routepay.momo.remittances.dto.RemittanceResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

/**
 * Client for the MTN MoMo Remittances API.
 *
 * <p>Handles cross-border money transfers for migrant workers sending money home.</p>
 */
@Slf4j
public class RemittancesClient {

    private final MoMoProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RemittancesClient(MoMoProperties properties, HttpClient httpClient, ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Send a cross-border remittance.
     *
     * @param request the remittance request
     * @return remittance response with conversion details
     */
    public RemittanceResponse send(RemittanceRequest request) {
        log.info("🌍 Remittances: send — {} {} → {} from {} to {}",
                request.getAmount(), request.getSourceCurrency(),
                request.getTargetCurrency(), request.getSenderPhone(), request.getRecipientPhone());

        String referenceId = UUID.randomUUID().toString();

        try {
            String json = objectMapper.writeValueAsString(request);
            String url = properties.getBaseUrl() + "/remittance/v1_0/transfer/" + referenceId;

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
                log.info("✅ Remittances: transfer initiated — ref: {}", referenceId);
                return RemittanceResponse.builder()
                        .referenceId(referenceId)
                        .status("PENDING")
                        .sourceAmount(request.getAmount())
                        .sourceCurrency(request.getSourceCurrency())
                        .targetCurrency(request.getTargetCurrency())
                        .build();
            } else {
                log.error("❌ Remittances: failed — status: {}, body: {}", response.statusCode(), response.body());
                throw new MoMoApiException("Remittances transfer failed",
                        response.statusCode(), response.body());
            }
        } catch (MoMoApiException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MoMoConnectionException("Remittances request interrupted", e);
        } catch (java.io.IOException e) {
            log.error("❌ Remittances: connection error — {}", e.getMessage());
            throw new MoMoConnectionException("Failed to connect to MoMo Remittances API", e);
        } catch (RuntimeException e) {
            log.error("❌ Remittances: unexpected error — {}", e.getMessage());
            throw new MoMoApiException("Unexpected error in Remittances API", 500, e.getMessage());
        }
    }
}
