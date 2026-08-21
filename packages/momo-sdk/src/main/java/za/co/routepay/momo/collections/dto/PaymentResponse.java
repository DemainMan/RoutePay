package za.co.routepay.momo.collections.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response from the MTN MoMo Collections API after a payment request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    /** MoMo transaction reference (UUID). */
    private String referenceId;

    /** Status of the payment request. */
    private String status;

    /** Financial transaction ID (assigned by MoMo after processing). */
    private String financialTransactionId;

    /** Reason for failure, if applicable. */
    private String reason;

    /** Timestamp of the response. */
    @Builder.Default
    private Instant timestamp = Instant.now();

    /** Whether this response came from the mock backend. */
    @Builder.Default
    private boolean mock = false;
}
