package za.co.routepay.momo.remittances.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RemittanceRequestTest {

    @Test
    @DisplayName("builder creates remittance request")
    void builderCreatesRequest() {
        RemittanceRequest request = RemittanceRequest.builder()
                .amount(new BigDecimal("1000.00"))
                .sourceCurrency("ZAR")
                .targetCurrency("USD")
                .senderPhone("+27821234567")
                .recipientPhone("+14155551234")
                .reference("rem-001")
                .senderNote("For family")
                .recipientNote("Monthly support")
                .build();

        assertThat(request.getAmount()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(request.getSourceCurrency()).isEqualTo("ZAR");
        assertThat(request.getTargetCurrency()).isEqualTo("USD");
        assertThat(request.getSenderPhone()).isEqualTo("+27821234567");
        assertThat(request.getRecipientPhone()).isEqualTo("+14155551234");
        assertThat(request.getReference()).isEqualTo("rem-001");
        assertThat(request.getSenderNote()).isEqualTo("For family");
        assertThat(request.getRecipientNote()).isEqualTo("Monthly support");
    }
}
