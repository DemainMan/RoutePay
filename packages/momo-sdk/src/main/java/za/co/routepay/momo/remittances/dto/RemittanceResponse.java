package za.co.routepay.momo.remittances.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response from the MTN MoMo Remittances API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemittanceResponse {

    private String referenceId;
    private String status;
    private String financialTransactionId;
    private BigDecimal sourceAmount;
    private String sourceCurrency;
    private BigDecimal targetAmount;
    private String targetCurrency;
    private String reason;
    @Builder.Default
    private Instant timestamp = Instant.now();
    @Builder.Default
    private boolean mock = false;
}
