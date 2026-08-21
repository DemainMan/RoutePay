package za.co.routepay.momo.disbursements.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response from the MTN MoMo Disbursements API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {

    /** MoMo transaction reference (UUID). */
    private String referenceId;

    /** Status of the disbursement. */
    private String status;

    /** Financial transaction ID. */
    private String financialTransactionId;

    /** Reason for failure, if applicable. */
    private String reason;

    @Builder.Default
    private Instant timestamp = Instant.now();

    @Builder.Default
    private boolean mock = false;
}
