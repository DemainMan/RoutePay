package za.co.routepay.momo.payments.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response from the MTN MoMo Payments API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private String referenceId;
    private String status;
    private String financialTransactionId;
    private String reason;
    @Builder.Default
    private Instant timestamp = Instant.now();
    @Builder.Default
    private boolean mock = false;
}
