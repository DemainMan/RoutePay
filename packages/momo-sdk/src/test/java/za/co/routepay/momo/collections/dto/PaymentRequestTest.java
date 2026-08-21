package za.co.routepay.momo.collections.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentRequestTest {

    @Test
    @DisplayName("builder creates request with all fields")
    void builderCreatesRequest() {
        PaymentRequest request = PaymentRequest.builder()
                .amount(new BigDecimal("25.50"))
                .currency("ZAR")
                .phone("+27821234567")
                .reference("trip-456")
                .payerMessage("Bus fare")
                .payeeNote("Operator received")
                .build();

        assertThat(request.getAmount()).isEqualByComparingTo(new BigDecimal("25.50"));
        assertThat(request.getCurrency()).isEqualTo("ZAR");
        assertThat(request.getPhone()).isEqualTo("+27821234567");
        assertThat(request.getReference()).isEqualTo("trip-456");
        assertThat(request.getPayerMessage()).isEqualTo("Bus fare");
        assertThat(request.getPayeeNote()).isEqualTo("Operator received");
    }

    @Test
    @DisplayName("builder defaults currency to ZAR")
    void builderDefaultsCurrency() {
        PaymentRequest request = PaymentRequest.builder()
                .amount(new BigDecimal("10.00"))
                .phone("+27821234567")
                .reference("ref-1")
                .build();

        assertThat(request.getCurrency()).isEqualTo("ZAR");
    }

    @Test
    @DisplayName("equals and hashCode work")
    void equalsAndHashCode() {
        PaymentRequest a = PaymentRequest.builder()
                .amount(new BigDecimal("10.00"))
                .currency("ZAR")
                .phone("+27821234567")
                .reference("ref-1")
                .build();

        PaymentRequest b = PaymentRequest.builder()
                .amount(new BigDecimal("10.00"))
                .currency("ZAR")
                .phone("+27821234567")
                .reference("ref-1")
                .build();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("toString includes key fields")
    void toStringIncludesFields() {
        PaymentRequest request = PaymentRequest.builder()
                .amount(new BigDecimal("15.00"))
                .phone("+27821234567")
                .reference("trip-789")
                .build();

        String str = request.toString();
        assertThat(str).contains("15.00");
        assertThat(str).contains("+27821234567");
        assertThat(str).contains("trip-789");
    }
}
