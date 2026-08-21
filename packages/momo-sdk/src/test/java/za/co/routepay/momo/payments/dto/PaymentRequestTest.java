package za.co.routepay.momo.payments.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentRequestTest {

    @Test
    @DisplayName("builder creates payments request with all fields")
    void builderCreatesRequest() {
        PaymentRequest request = PaymentRequest.builder()
                .amount(new BigDecimal("99.00"))
                .currency("ZAR")
                .phone("+27821234567")
                .reference("pass-weekly")
                .productType("WEEKLY_PASS")
                .payerMessage("Weekly travel pass")
                .payeeNote("Pass purchase")
                .build();

        assertThat(request.getAmount()).isEqualByComparingTo(new BigDecimal("99.00"));
        assertThat(request.getCurrency()).isEqualTo("ZAR");
        assertThat(request.getPhone()).isEqualTo("+27821234567");
        assertThat(request.getReference()).isEqualTo("pass-weekly");
        assertThat(request.getProductType()).isEqualTo("WEEKLY_PASS");
    }

    @Test
    @DisplayName("currency defaults to ZAR")
    void currencyDefaultsToZar() {
        PaymentRequest request = PaymentRequest.builder()
                .amount(new BigDecimal("50.00"))
                .phone("+27821234567")
                .reference("ref-1")
                .productType("DAILY_PASS")
                .build();

        assertThat(request.getCurrency()).isEqualTo("ZAR");
    }
}
