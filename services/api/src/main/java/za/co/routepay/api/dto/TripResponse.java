package za.co.routepay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripResponse {

    private Long id;
    private Long routeId;
    private String routeName;
    private String status;
    private BigDecimal farePaid;
    private String momoReference;
    private Instant createdAt;
}
