package za.co.routepay.momo.payments.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentResponseTest {

    @Test
    @DisplayName("builder creates response with defaults")
    void builderCreatesWithDefaults() {
        PaymentResponse response = PaymentResponse.builder()
                .referenceId("pay-ref-1")
                .status("SUCCESSFUL")
                .build();

        assertThat(response.getReferenceId()).isEqualTo("pay-ref-1");
        assertThat(response.getStatus()).isEqualTo("SUCCESSFUL");
        assertThat(response.isMock()).isFalse();
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("all fields settable")
    void allFieldsSettable() {
        PaymentResponse response = PaymentResponse.builder()
                .referenceId("ref-500")
                .status("FAILED")
                .financialTransactionId("ft-600")
                .reason("Product not found")
                .mock(true)
                .build();

        assertThat(response.getFinancialTransactionId()).isEqualTo("ft-600");
        assertThat(response.getReason()).isEqualTo("Product not found");
        assertThat(response.isMock()).isTrue();
    }
}
