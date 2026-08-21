package za.co.routepay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TravelPassResponse {

    private Long id;
    private String passType;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private BigDecimal pricePaid;
    private String status;
    private String momoReference;
}
