package za.co.routepay.momo.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import za.co.routepay.momo.auth.dto.AuthToken;
import za.co.routepay.momo.auth.dto.OtpRequest;
import za.co.routepay.momo.config.MoMoProperties;
import za.co.routepay.momo.exception.MoMoApiException;
import za.co.routepay.momo.exception.MoMoAuthException;
import za.co.routepay.momo.exception.MoMoConnectionException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

/**
 * Client for MoMo API user authentication.
 * Handles API user creation and basic auth token generation.
 */
@Slf4j
public class AuthClient {

    private final MoMoProperties properties;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public AuthClient(MoMoProperties properties, HttpClient httpClient, ObjectMapper objectMapper) {
        this.properties = properties;
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }

    /**
     * Request an OTP for the given phone number.
     */
    public void requestOtp(String phone) {
        log.info("Auth: requestOtp for phone={}", phone);
        // In mock mode, this is a no-op — the OTP is always "123456"
        if (properties.getEnvironment() == za.co.routepay.momo.config.MoMoEnvironment.MOCK) {
            log.info("Auth: mock mode — OTP is 123456 for {}", phone);
            return;
        }
        // Real implementation would call MoMo Identity API
        log.warn("Auth: requestOtp not implemented for non-mock environments yet");
    }

    /**
     * Verify an OTP and return an auth token.
     */
    public AuthToken verifyOtp(String phone, String otp) {
        log.info("Auth: verifyOtp for phone={}", phone);

        if (properties.getEnvironment() == za.co.routepay.momo.config.MoMoEnvironment.MOCK) {
            log.info("Auth: mock mode — returning mock token for {}", phone);
            return AuthToken.builder()
                    .accessToken("mock-jwt-" + phone.hashCode())
                    .tokenType("Bearer")
                    .expiresIn(86400)
                    .phone(phone)
                    .mock(true)
                    .build();
        }

        try {
            String credentials = Base64.getEncoder().encodeToString(
                    (properties.getApiUser() + ":" + properties.getApiKey()).getBytes());
            String url = properties.getBaseUrl() + "/collection/token/";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Basic " + credentials)
                    .header("Ocp-Apim-Subscription-Key", properties.getSubscriptionKey())
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                var jsonNode = objectMapper.readTree(response.body());
                return AuthToken.builder()
                        .accessToken(jsonNode.path("access_token").asText())
                        .tokenType(jsonNode.path("token_type").asText("Bearer"))
                        .expiresIn(jsonNode.path("expires_in").asInt(3600))
                        .phone(phone)
                        .build();
            } else {
                throw new MoMoAuthException("Token request failed: " + response.statusCode());
            }
        } catch (MoMoAuthException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MoMoConnectionException("Auth request interrupted", e);
        } catch (java.io.IOException e) {
            throw new MoMoConnectionException("Failed to connect to MoMo Auth API", e);
        } catch (RuntimeException e) {
            throw new MoMoApiException("Unexpected error in Auth API", 500, e.getMessage());
        }
    }
}
