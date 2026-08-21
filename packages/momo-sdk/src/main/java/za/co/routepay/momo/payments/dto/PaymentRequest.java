package za.co.routepay.momo.payments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request to initiate a payment via MoMo Payments API (for passes, premium features).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Builder.Default
    private String currency = "ZAR";

    @NotBlank(message = "Payer phone is required")
    private String phone;

    @NotBlank(message = "Reference is required")
    private String reference;

    @NotBlank(message = "Product type is required")
    private String productType;

    private String payerMessage;
    private String payeeNote;
}
