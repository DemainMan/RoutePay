package za.co.routepay.momo.payments;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import za.co.routepay.momo.config.MoMoEnvironment;
import za.co.routepay.momo.config.MoMoProperties;
import za.co.routepay.momo.exception.MoMoApiException;
import za.co.routepay.momo.exception.MoMoConnectionException;
import za.co.routepay.momo.mock.MockMoMoBackend;
import za.co.routepay.momo.payments.dto.PaymentRequest;
import za.co.routepay.momo.payments.dto.PaymentResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.UUID;

/**
 * Client for the MTN MoMo Payments API.
 * Used for purchasing travel passes and other premium features.
 */
@Slf4j
public class PaymentsClient {

    private final MoMoProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final MockMoMoBackend mockBackend;

    public PaymentsClient(MoMoProperties properties, HttpClient httpClient,
                          ObjectMapper objectMapper, MockMoMoBackend mockBackend) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.mockBackend = mockBackend;
    }

    public PaymentResponse requestPayment(PaymentRequest request) {
        log.info("Payments: requestPayment {} {} for {} ref={} product={}",
                request.getAmount(), request.getCurrency(), request.getPhone(),
                request.getReference(), request.getProductType());

        if (properties.getEnvironment() == MoMoEnvironment.MOCK) {
            log.info("Payments: MOCK mode — delegating to mock backend");
            return mockBackend.payForProduct(request);
        }

        String referenceId = UUID.randomUUID().toString();

        try {
            String json = objectMapper.writeValueAsString(request);
            String url = properties.getBaseUrl() + "/payment/v1_0/requesttopay/" + referenceId;

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
                log.info("Payments: initiated ref={} status=PENDING", referenceId);
                return PaymentResponse.builder()
                        .referenceId(referenceId)
                        .status("PENDING")
                        .build();
            } else {
                log.error("Payments: failed status={} body={}", response.statusCode(), response.body());
                throw new MoMoApiException("Payments requestPayment failed",
                        response.statusCode(), response.body());
            }
        } catch (MoMoApiException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MoMoConnectionException("Payments request interrupted", e);
        } catch (java.io.IOException e) {
            log.error("Payments: connection error {}", e.getMessage());
            throw new MoMoConnectionException("Failed to connect to MoMo Payments API", e);
        } catch (RuntimeException e) {
            log.error("Payments: unexpected error {}", e.getMessage());
            throw new MoMoApiException("Unexpected error in Payments API", 500, e.getMessage());
        }
    }
}
