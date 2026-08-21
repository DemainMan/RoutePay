package za.co.routepay.momo.collections.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request to collect a fare payment from a commuter via MoMo Collections API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    /** Amount in the specified currency (e.g. "15.00" for ZAR 15.00). */
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    /** ISO 4217 currency code (e.g. "ZAR"). */
    @NotBlank(message = "Currency is required")
    @Builder.Default
    private String currency = "ZAR";

    /** Commuter's MSISDN in international format (e.g. "+27821234567"). */
    @NotBlank(message = "Phone number is required")
    private String phone;

    /** External reference (e.g. trip ID, pass ID). */
    @NotBlank(message = "Reference is required")
    private String reference;

    /** Human-readable payer message. */
    private String payerMessage;

    /** Human-readable payee note. */
    private String payeeNote;
}
