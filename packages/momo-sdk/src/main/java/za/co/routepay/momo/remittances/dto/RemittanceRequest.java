package za.co.routepay.momo.remittances.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request to send a cross-border remittance via MoMo Remittances API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RemittanceRequest {

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Source currency is required")
    private String sourceCurrency;

    @NotBlank(message = "Target currency is required")
    private String targetCurrency;

    @NotBlank(message = "Recipient phone is required")
    private String recipientPhone;

    @NotBlank(message = "Sender phone is required")
    private String senderPhone;

    @NotBlank(message = "Reference is required")
    private String reference;

    private String senderNote;

    private String recipientNote;
}
