package za.co.routepay.momo.remittances.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RemittanceResponseTest {

    @Test
    @DisplayName("builder creates response with all fields")
    void builderCreatesResponse() {
        RemittanceResponse response = RemittanceResponse.builder()
                .referenceId("ref-300")
                .status("SUCCESSFUL")
                .financialTransactionId("ft-400")
                .sourceAmount(new BigDecimal("1000.00"))
                .sourceCurrency("ZAR")
                .targetAmount(new BigDecimal("50.00"))
                .targetCurrency("USD")
                .mock(true)
                .build();

        assertThat(response.getReferenceId()).isEqualTo("ref-300");
        assertThat(response.getStatus()).isEqualTo("SUCCESSFUL");
        assertThat(response.getSourceAmount()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(response.getSourceCurrency()).isEqualTo("ZAR");
        assertThat(response.getTargetAmount()).isEqualByComparingTo(new BigDecimal("50.00"));
        assertThat(response.getTargetCurrency()).isEqualTo("USD");
        assertThat(response.isMock()).isTrue();
    }

    @Test
    @DisplayName("defaults are correct")
    void defaultsCorrect() {
        RemittanceResponse response = RemittanceResponse.builder().build();

        assertThat(response.isMock()).isFalse();
        assertThat(response.getTimestamp()).isNotNull();
        assertThat(response.getSourceAmount()).isNull();
    }
}
