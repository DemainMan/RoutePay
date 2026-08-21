package za.co.routepay.momo.disbursements.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class TransferRequestTest {

    @Test
    @DisplayName("builder creates transfer request")
    void builderCreatesRequest() {
        TransferRequest request = TransferRequest.builder()
                .amount(new BigDecimal("500.00"))
                .currency("ZAR")
                .phone("+27827654321")
                .reference("payout-001")
                .payeeNote("Monthly earnings")
                .payerNote("Operator payout")
                .build();

        assertThat(request.getAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
        assertThat(request.getCurrency()).isEqualTo("ZAR");
        assertThat(request.getPhone()).isEqualTo("+27827654321");
        assertThat(request.getReference()).isEqualTo("payout-001");
        assertThat(request.getPayeeNote()).isEqualTo("Monthly earnings");
        assertThat(request.getPayerNote()).isEqualTo("Operator payout");
    }

    @Test
    @DisplayName("currency defaults to ZAR")
    void currencyDefaultsToZar() {
        TransferRequest request = TransferRequest.builder()
                .amount(new BigDecimal("100.00"))
                .phone("+27827654321")
                .reference("ref-1")
                .build();

        assertThat(request.getCurrency()).isEqualTo("ZAR");
    }
}
