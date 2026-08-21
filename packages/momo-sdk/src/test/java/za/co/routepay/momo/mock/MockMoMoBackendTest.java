package za.co.routepay.momo.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import za.co.routepay.momo.auth.dto.AuthToken;
import za.co.routepay.momo.disbursements.dto.TransferRequest;
import za.co.routepay.momo.disbursements.dto.TransferResponse;
import za.co.routepay.momo.remittances.dto.RemittanceRequest;
import za.co.routepay.momo.remittances.dto.RemittanceResponse;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MockMoMoBackendTest {

    private MockMoMoBackend backend;

    @BeforeEach
    void setUp() {
        backend = new MockMoMoBackend();
    }

    @Nested
    @DisplayName("collectPayment()")
    class CollectPayment {

        @Test
        @DisplayName("returns SUCCESSFUL response with mock flag")
        void returnsSuccessfulResponse() {
            var request = za.co.routepay.momo.collections.dto.PaymentRequest.builder()
                    .amount(new BigDecimal("15.00"))
                    .currency("ZAR")
                    .phone("+27821234567")
                    .reference("trip-123")
                    .build();

            var response = backend.collectPayment(request);

            assertThat(response.getStatus()).isEqualTo("SUCCESSFUL");
            assertThat(response.isMock()).isTrue();
            assertThat(response.getReferenceId()).isNotBlank();
            assertThat(response.getFinancialTransactionId()).isNotBlank();
            assertThat(response.getTimestamp()).isNotNull();
        }
    }

    @Nested
    @DisplayName("disburse()")
    class Disburse {

        @Test
        @DisplayName("returns SUCCESSFUL transfer response")
        void returnsSuccessfulTransfer() {
            TransferRequest request = TransferRequest.builder()
                    .amount(new BigDecimal("500.00"))
                    .currency("ZAR")
                    .phone("+27827654321")
                    .reference("payout-batch-1")
                    .build();

            TransferResponse response = backend.disburse(request);

            assertThat(response.getStatus()).isEqualTo("SUCCESSFUL");
            assertThat(response.isMock()).isTrue();
            assertThat(response.getReferenceId()).isNotBlank();
            assertThat(response.getFinancialTransactionId()).isNotBlank();
        }
    }

    @Nested
    @DisplayName("remit()")
    class Remit {

        @Test
        @DisplayName("returns converted amounts for cross-border remittance")
        void returnsConvertedAmounts() {
            RemittanceRequest request = RemittanceRequest.builder()
                    .amount(new BigDecimal("1000.00"))
                    .sourceCurrency("ZAR")
                    .targetCurrency("USD")
                    .senderPhone("+27821234567")
                    .recipientPhone("+14155551234")
                    .reference("rem-001")
                    .build();

            RemittanceResponse response = backend.remit(request);

            assertThat(response.getStatus()).isEqualTo("SUCCESSFUL");
            assertThat(response.isMock()).isTrue();
            assertThat(response.getSourceAmount()).isEqualByComparingTo(new BigDecimal("1000.00"));
            assertThat(response.getSourceCurrency()).isEqualTo("ZAR");
            assertThat(response.getTargetCurrency()).isEqualTo("USD");
            assertThat(response.getTargetAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        }
    }

    @Nested
    @DisplayName("authenticate()")
    class Authenticate {

        @Test
        @DisplayName("returns token for valid OTP 123456")
        void returnsTokenForValidOtp() {
            AuthToken token = backend.authenticate("+27821234567", "123456");

            assertThat(token.getAccessToken()).startsWith("mock-jwt-");
            assertThat(token.getTokenType()).isEqualTo("Bearer");
            assertThat(token.getExpiresIn()).isEqualTo(86400);
            assertThat(token.getPhone()).isEqualTo("+27821234567");
            assertThat(token.isMock()).isTrue();
        }

        @Test
        @DisplayName("throws RuntimeException for invalid OTP")
        void throwsForInvalidOtp() {
            assertThatThrownBy(() -> backend.authenticate("+27821234567", "000000"))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Invalid OTP");
        }
    }

    @Nested
    @DisplayName("payForProduct()")
    class PayForProduct {

        @Test
        @DisplayName("returns SUCCESSFUL payment response for product purchase")
        void returnsSuccessfulPayment() {
            var request = za.co.routepay.momo.payments.dto.PaymentRequest.builder()
                    .amount(new BigDecimal("99.00"))
                    .currency("ZAR")
                    .phone("+27821234567")
                    .reference("pass-weekly")
                    .productType("WEEKLY_PASS")
                    .build();

            var response = backend.payForProduct(request);

            assertThat(response.getStatus()).isEqualTo("SUCCESSFUL");
            assertThat(response.isMock()).isTrue();
            assertThat(response.getReferenceId()).isNotBlank();
        }
    }
}
