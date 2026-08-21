package za.co.routepay.momo.disbursements.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class TransferResponseTest {

    @Test
    @DisplayName("builder creates response with defaults")
    void builderCreatesWithDefaults() {
        TransferResponse response = TransferResponse.builder()
                .referenceId("ref-789")
                .status("SUCCESSFUL")
                .build();

        assertThat(response.getReferenceId()).isEqualTo("ref-789");
        assertThat(response.getStatus()).isEqualTo("SUCCESSFUL");
        assertThat(response.isMock()).isFalse();
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("all fields settable")
    void allFieldsSettable() {
        Instant now = Instant.parse("2026-09-01T10:00:00Z");
        TransferResponse response = TransferResponse.builder()
                .referenceId("ref-100")
                .status("FAILED")
                .financialTransactionId("ft-200")
                .reason("Timeout")
                .timestamp(now)
                .mock(true)
                .build();

        assertThat(response.getFinancialTransactionId()).isEqualTo("ft-200");
        assertThat(response.getReason()).isEqualTo("Timeout");
        assertThat(response.isMock()).isTrue();
    }
}
