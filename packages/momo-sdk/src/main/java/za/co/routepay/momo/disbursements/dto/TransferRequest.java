package za.co.routepay.momo.disbursements.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request to disburse funds to an operator via MoMo Disbursements API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    /** Amount to disburse. */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    /** ISO 4217 currency code. */
    @NotBlank(message = "Currency is required")
    @Builder.Default
    private String currency = "ZAR";

    /** Operator's MSISDN (e.g. "+27821234567"). */
    @NotBlank(message = "Phone number is required")
    private String phone;

    /** External reference (e.g. payout batch ID). */
    @NotBlank(message = "Reference is required")
    private String reference;

    /** Payee note. */
    private String payeeNote;

    /** Payer note. */
    private String payerNote;
}
