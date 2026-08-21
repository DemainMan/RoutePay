package za.co.routepay.momo.collections.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentResponseTest {

    @Test
    @DisplayName("builder creates response with defaults")
    void builderCreatesWithDefaults() {
        PaymentResponse response = PaymentResponse.builder()
                .referenceId("ref-123")
                .status("SUCCESSFUL")
                .build();

        assertThat(response.getReferenceId()).isEqualTo("ref-123");
        assertThat(response.getStatus()).isEqualTo("SUCCESSFUL");
        assertThat(response.isMock()).isFalse();
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("mock flag defaults to false")
    void mockFlagDefaultsFalse() {
        PaymentResponse response = PaymentResponse.builder().build();
        assertThat(response.isMock()).isFalse();
    }

    @Test
    @DisplayName("all fields can be set")
    void allFieldsSettable() {
        Instant now = Instant.parse("2026-09-01T10:00:00Z");
        PaymentResponse response = PaymentResponse.builder()
                .referenceId("ref-456")
                .status("FAILED")
                .financialTransactionId("ft-789")
                .reason("Insufficient funds")
                .timestamp(now)
                .mock(true)
                .build();

        assertThat(response.getReferenceId()).isEqualTo("ref-456");
        assertThat(response.getStatus()).isEqualTo("FAILED");
        assertThat(response.getFinancialTransactionId()).isEqualTo("ft-789");
        assertThat(response.getReason()).isEqualTo("Insufficient funds");
        assertThat(response.getTimestamp()).isEqualTo(now);
        assertThat(response.isMock()).isTrue();
    }
}
