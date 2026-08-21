package za.co.routepay.momo.mock;

import za.co.routepay.momo.auth.dto.AuthToken;
import za.co.routepay.momo.disbursements.dto.TransferRequest;
import za.co.routepay.momo.disbursements.dto.TransferResponse;
import za.co.routepay.momo.remittances.dto.RemittanceRequest;
import za.co.routepay.momo.remittances.dto.RemittanceResponse;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Mock MoMo backend that returns realistic fake responses.
 * Used for hackathon demos — no external API calls.
 */
public class MockMoMoBackend {

    public za.co.routepay.momo.collections.dto.PaymentResponse collectPayment(
            za.co.routepay.momo.collections.dto.PaymentRequest request) {
        simulateLatency();
        return za.co.routepay.momo.collections.dto.PaymentResponse.builder()
                .referenceId(UUID.randomUUID().toString())
                .status("SUCCESSFUL")
                .financialTransactionId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .mock(true)
                .build();
    }

    public TransferResponse disburse(TransferRequest request) {
        simulateLatency();
        return TransferResponse.builder()
                .referenceId(UUID.randomUUID().toString())
                .status("SUCCESSFUL")
                .financialTransactionId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .mock(true)
                .build();
    }

    public RemittanceResponse remit(RemittanceRequest request) {
        simulateLatency();
        BigDecimal exchangeRate = BigDecimal.valueOf(0.05);
        return RemittanceResponse.builder()
                .referenceId(UUID.randomUUID().toString())
                .status("SUCCESSFUL")
                .financialTransactionId(UUID.randomUUID().toString())
                .sourceAmount(request.getAmount())
                .sourceCurrency(request.getSourceCurrency())
                .targetAmount(request.getAmount().multiply(exchangeRate))
                .targetCurrency(request.getTargetCurrency())
                .timestamp(Instant.now())
                .mock(true)
                .build();
    }

    public AuthToken authenticate(String phone, String otp) {
        simulateLatency();
        if (!"123456".equals(otp)) {
            throw new RuntimeException("Invalid OTP: " + otp);
        }
        return AuthToken.builder()
                .accessToken("mock-jwt-" + UUID.randomUUID())
                .tokenType("Bearer")
                .expiresIn(86400)
                .phone(phone)
                .mock(true)
                .build();
    }

    public za.co.routepay.momo.payments.dto.PaymentResponse payForProduct(
            za.co.routepay.momo.payments.dto.PaymentRequest request) {
        simulateLatency();
        return za.co.routepay.momo.payments.dto.PaymentResponse.builder()
                .referenceId(UUID.randomUUID().toString())
                .status("SUCCESSFUL")
                .financialTransactionId(UUID.randomUUID().toString())
                .timestamp(Instant.now())
                .mock(true)
                .build();
    }

    private void simulateLatency() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
