package za.co.routepay.momo.disbursements;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import za.co.routepay.momo.config.MoMoEnvironment;
import za.co.routepay.momo.config.MoMoProperties;
import za.co.routepay.momo.disbursements.dto.TransferRequest;
import za.co.routepay.momo.disbursements.dto.TransferResponse;
import za.co.routepay.momo.exception.MoMoApiException;
import za.co.routepay.momo.exception.MoMoConnectionException;
import za.co.routepay.momo.mock.MockMoMoBackend;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

/**
 * Client for the MTN MoMo Disbursements API.
 *
 * <p>Handles instant payouts to taxi/bus operators.</p>
 */
@Slf4j
public class DisbursementsClient {

    private final MoMoProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final MockMoMoBackend mockBackend;

    public DisbursementsClient(MoMoProperties properties, HttpClient httpClient,
                               ObjectMapper objectMapper, MockMoMoBackend mockBackend) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.mockBackend = mockBackend;
    }

    /**
     * Transfer funds to an operator's MoMo wallet.
     *
     * @param request the transfer request
     * @return transfer response with transaction reference
     */
    public TransferResponse transfer(TransferRequest request) {
        log.info("💸 Disbursements: transfer — {} {} to {} (ref: {})",
                request.getAmount(), request.getCurrency(), request.getPhone(), request.getReference());

        if (properties.getEnvironment() == MoMoEnvironment.MOCK) {
            log.info("Disbursements: MOCK mode — delegating to mock backend");
            return mockBackend.disburse(request);
        }

        String referenceId = UUID.randomUUID().toString();

        try {
            String json = objectMapper.writeValueAsString(request);
            String url = properties.getBaseUrl() + "/disbursement/v1_0/transfer/" + referenceId;

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
                log.info("✅ Disbursements: transfer initiated — ref: {}, status: PENDING", referenceId);
                return TransferResponse.builder()
                        .referenceId(referenceId)
                        .status("PENDING")
                        .build();
            } else {
                log.error("❌ Disbursements: transfer failed — status: {}, body: {}", response.statusCode(), response.body());
                throw new MoMoApiException("Disbursements transfer failed",
                        response.statusCode(), response.body());
            }
        } catch (MoMoApiException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MoMoConnectionException("Disbursements request interrupted", e);
        } catch (java.io.IOException e) {
            log.error("❌ Disbursements: connection error — {}", e.getMessage());
            throw new MoMoConnectionException("Failed to connect to MoMo Disbursements API", e);
        } catch (RuntimeException e) {
            log.error("❌ Disbursements: unexpected error — {}", e.getMessage());
            throw new MoMoApiException("Unexpected error in Disbursements API", 500, e.getMessage());
        }
    }
}
